package com.simplaex.bedrock;

import lombok.*;
import lombok.experimental.UtilityClass;
import lombok.experimental.Wither;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Missing control structures for Java.
 */
@UtilityClass
@SuppressWarnings("WeakerAccess")
public class Control {

  @Value
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class DispatchBranch<A, T> {
    private final Class<A> clazz;
    private final ThrowingFunction<A, T> callable;
  }

  @Value
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class DispatchVoidBranch<A> {
    private final Class<A> clazz;
    private final ThrowingConsumer<A> callable;
  }

  @Nonnull
  public static <A, T> DispatchBranch<A, T> branch(@Nonnull final Class<A> clazz, @Nonnull final ThrowingFunction<A, T> f) {
    return new DispatchBranch<>(clazz, f);
  }

  @Nonnull
  public static <A> DispatchVoidBranch<A> voidBranch(@Nonnull final Class<A> clazz, @Nonnull final ThrowingConsumer<A> f) {
    return new DispatchVoidBranch<>(clazz, f);
  }

  @SuppressWarnings("unchecked")
  @SafeVarargs
  public static <T> T dispatch(final Object value, final DispatchBranch<?, ? extends T>... dispatchBranches) {
    for (val branch : dispatchBranches) {
      if (branch.getClazz().isAssignableFrom(value.getClass())) {
        return Try.execute(() -> (T) ((ThrowingFunction) branch.getCallable()).execute(value)).orElseThrowRuntime();
      }
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  public static void dispatch(final Object value, final DispatchVoidBranch<?>... branches) {
    for (val branch : branches) {
      if (branch.getClazz().isAssignableFrom(value.getClass())) {
        Try.run(() -> ((ThrowingConsumer) branch.getCallable()).accept(value));
        return;
      }
    }
  }

  public static void forever(@Nonnull final ThrowingRunnable runnable) {
    while (!Thread.currentThread().isInterrupted()) {
      try {
        runnable.run();
      } catch (final InterruptedException exc) {
        return;
      } catch (final Exception ignore) {
      }
    }
  }

  public static void forever(
    @Nonnull final Consumer<Exception> exceptionHandler,
    @Nonnull final ThrowingRunnable runnable
  ) {
    while (!Thread.currentThread().isInterrupted()) {
      try {
        runnable.run();
      } catch (final InterruptedException exc) {
        return;
      } catch (final Exception exc) {
        exceptionHandler.accept(exc);
      }
    }
  }

  @SneakyThrows
  public static void sleep(final Duration duration) {
    Thread.sleep(duration.toMillis());
  }

  public static void parallel(@Nonnull final Executor executor, @Nonnull final ThrowingRunnable... runnables)
    throws ExecutionException {
    Objects.requireNonNull(executor, "executor must not be null");
    Objects.requireNonNull(runnables, "nullables must not be null");
    val semaphore = new Semaphore(0);
    val exceptions = Collections.synchronizedList(new ArrayList<Exception>());
    for (val runnable : runnables) {
      executor.execute(() -> {
        try {
          runnable.run();
        } catch (final Exception exc) {
          exceptions.add(exc);
        } finally {
          semaphore.release(1);
        }
      });
    }
    semaphore.acquireUninterruptibly(runnables.length);
    if (!exceptions.isEmpty()) {
      throw new ExecutionException(Seq.ofCollection(exceptions));
    }
  }

  @SuppressWarnings("unchecked")
  @SafeVarargs
  public static <T> Seq<T> parallel(final @Nonnull Executor executor, final @Nonnull Callable<? extends T>... runnables)
    throws ExecutionException {
    Objects.requireNonNull(executor, "executor must not be null");
    Objects.requireNonNull(runnables, "nullables must not be null");
    val promises = new Promise[runnables.length];
    int i = 0;
    for (val runnable : runnables) {
      val promise = Promise.<T>promise();
      promises[i++] = promise;
      executor.execute(() -> {
        try {
          val result = runnable.call();
          promise.fulfill(result);
        } catch (final Exception exc) {
          promise.fail(exc);
        }
      });
    }
    val results = Seq.<T>builder();
    val exceptions = Seq.<Throwable>builder();
    for (val promise : promises) {
      promise.waitFor();
      if (promise.isSuccess()) {
        results.add((T) promise.get());
      } else {
        exceptions.add(promise.getException());
      }
    }
    if (exceptions.isEmpty()) {
      return results.build();
    }
    throw new ExecutionException(exceptions.result());
  }

  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @Wither(AccessLevel.PRIVATE)
  public static final class Async<In, Out> implements ThrowingBiConsumer<In, Callback<Out>>, Function<In, Promise<Out>> {

    @Value
    @Wither
    private static class AsyncOptions {

      private final Executor executor;
      private final Consumer<ThrowingRunnable> callbackHandler;

      private static final AsyncOptions DEFAULT_OPTIONS = new AsyncOptions(Runnable::run, Try::unfailable);

      @Nonnull
      public static AsyncOptions defaultOptions() {
        return DEFAULT_OPTIONS;
      }
    }

    @FunctionalInterface
    private interface AsyncCallback<Out> {
      void call(@Nonnull final AsyncOptions opts, final Object error, final Out result) throws Exception;
    }

    @FunctionalInterface
    private interface AsyncFunction<In, Out> {
      void run(@Nonnull final AsyncOptions options, final In arg, @Nonnull final AsyncCallback<Out> callback) throws Exception;
    }

    private final AsyncFunction<In, Out> function;
    private final AsyncOptions options;

    private Async(@Nonnull final AsyncFunction<In, Out> function) {
      this(function, AsyncOptions.defaultOptions());
    }

    private Async(@Nonnull final ThrowingBiConsumer<In, Callback<Out>> function) {
      this((opts, arg, callback) -> function.accept(arg, (error, result) -> callback.call(opts, error, result)));
    }

    @Nonnull
    @SuppressWarnings("CodeBlock2Expr")
    public final <T> Async<In, T> andThen(@Nonnull final ThrowingBiConsumer<Out, Callback<T>> function) {
      Objects.requireNonNull(function, "'function' must not be null.");
      return new Async<>((options, argument, callback) -> {
        runWithOptions(options, argument, (opts, error, result) -> {
          if (error == null) {
            async(function).runWithOptions(opts, result, callback);
          } else {
            opts.getCallbackHandler().accept(() -> callback.call(opts, error, null));
          }
        });
      }, options);
    }

    @SuppressWarnings("CodeBlock2Expr")
    @Nonnull
    @SafeVarargs
    public final <T> Async<In, Seq<T>> andThen(
      @Nonnull final ThrowingBiConsumer<Out, Callback<T>> function,
      @Nonnull final ThrowingBiConsumer<Out, Callback<T>>... functions
    ) {
      Objects.requireNonNull(function, "'function' must not be null.");
      Objects.requireNonNull(functions, "'functions' must not be null.");
      @SuppressWarnings("unchecked") final Async<Out, T>[] asyncs = new Async[1 + functions.length];
      asyncs[0] = async(function);
      for (int i = 0; i < functions.length; ) {
        final Async<Out, T> asyncFunction = async(functions[i]);
        i += 1;
        asyncs[i] = asyncFunction;
      }
      return new Async<>((options, argument, callback) -> {
        runWithOptions(options, argument, (opts, error, result) -> {
          if (error == null) {
            final Object[] results = new Object[asyncs.length];
            final Object[] errors = new Object[asyncs.length];
            final Box.IntBox numberOfReturns = Box.intBox(0);
            final Box.IntBox numberOfErrors = Box.intBox(0);
            for (int i = 0; i < asyncs.length; i += 1) {
              final int myIndex = i;
              asyncs[i].runWithOptions(opts, result, (opts2, error2, result2) -> {
                if (error2 == null) {
                  results[myIndex] = result2;
                } else {
                  errors[myIndex] = error2;
                  synchronized (numberOfErrors) {
                    numberOfErrors.update(n -> n + 1);
                  }
                }
                final boolean lastOne;
                synchronized (numberOfReturns) {
                  lastOne = numberOfReturns.update(n -> n + 1) == asyncs.length;
                }
                if (lastOne) {
                  if (numberOfErrors.getValue() == 0) {
                    opts.getCallbackHandler().accept(() -> callback.call(opts, null, new SeqSimple<>(results)));
                  } else {
                    opts.getCallbackHandler().accept(() -> callback.call(opts, new SeqSimple<>(errors), new SeqSimple<>(results)));
                  }
                }
              });
            }
          } else {
            opts.getCallbackHandler().accept(() -> callback.call(opts, error, null));
          }
        });
      }, options);
    }

    @SuppressWarnings("CodeBlock2Expr")
    private void runWithOptions(
      final @Nonnull AsyncOptions options,
      final In argument,
      final @Nonnull AsyncCallback<Out> callback
    ) {
      try {
        options.getExecutor().execute(() -> {
          try {
            function.run(options, argument, (opts, error, result) -> {
              opts.getCallbackHandler().accept(() -> callback.call(opts, error, result));
            });
          } catch (final Exception exc) {
            options.getCallbackHandler().accept(() -> callback.call(options, exc, null));
          }
        });
      } catch (final Exception exc) {
        options.getCallbackHandler().accept(() -> callback.call(options, exc, null));
      }
    }

    public void run(final In argument, @Nonnull final Callback<Out> callback) {
      runWithOptions(options, argument, (opts, error, result) -> callback.call(error, result));
    }

    public void run(final @Nonnull Executor executor, final In argument, final @Nonnull Callback<Out> callback) {
      runWithOptions(options.withExecutor(executor), argument, (opts, error, result) -> callback.call(error, result));
    }

    @Nonnull
    public Promise<Out> runPromised(final In argument) {
      return runPromised(options, argument);
    }

    @Nonnull
    public Promise<Out> runPromised(final @Nonnull Executor executor, final In argument) {
      return runPromised(options.withExecutor(executor), argument);
    }

    @Nonnull
    private Promise<Out> runPromised(final @Nonnull AsyncOptions options, final In argument) {
      final Promise<Out> promise = Promise.promise();
      runWithOptions(options, argument, (opts, error, result) -> {
        if (error == null) {
          promise.fulfill(result);
        } else if (error instanceof Exception) {
          promise.fail((Exception) error);
        } else if (error instanceof String) {
          promise.fail(new LightweightRuntimeException((String) error));
        } else {
          promise.fail(new AsyncException(error));
        }
      });
      return promise;
    }

    @Override
    public void accept(final In argument, @Nonnull final Callback<Out> callback) {
      run(argument, callback);
    }

    @Nonnull
    @Override
    public Promise<Out> apply(final In in) {
      return runPromised(in);
    }
  }

  @Nonnull
  public static <In, Out> Async<In, Out> async(@Nonnull final ThrowingBiConsumer<In, Callback<Out>> function) {
    Objects.requireNonNull(function, "'function' must not be null.");
    return new Async<>(function);
  }
}
