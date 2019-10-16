package com.simplaex.bedrock;

import lombok.*;
import lombok.experimental.UtilityClass;
import lombok.With;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;

/**
 * Missing control structures for Java.
 */
@UtilityClass
public class Control {

  @Value
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class TypeOfBranch<A, T> {
    private final Class<A> clazz;
    private final ThrowingFunction<A, T> callable;
  }

  @Value
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class TypeOfVoidBranch<A> {
    private final Class<A> clazz;
    private final ThrowingConsumer<A> callable;
  }

  @Nonnull
  public static <A, T> TypeOfBranch<A, T> type(@Nonnull final Class<A> clazz, @Nonnull final ThrowingFunction<A, T> f) {
    return new TypeOfBranch<>(clazz, f);
  }

  @Nonnull
  public static <A> TypeOfVoidBranch<A> type_(@Nonnull final Class<A> clazz, @Nonnull final ThrowingConsumer<A> f) {
    return new TypeOfVoidBranch<>(clazz, f);
  }

  @SuppressWarnings("unchecked")
  @SafeVarargs
  public static <T> T typeOf(final Object value, final TypeOfBranch<?, ? extends T>... typeOfBranches) {
    for (val branch : typeOfBranches) {
      if (branch.getClazz().isAssignableFrom(value.getClass())) {
        return Try.execute(() -> (T) ((ThrowingFunction) branch.getCallable()).execute(value)).orElseThrowRuntime();
      }
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  public static void typeOf(final Object value, final TypeOfVoidBranch<?>... branches) {
    for (val branch : branches) {
      if (branch.getClazz().isAssignableFrom(value.getClass())) {
        Try.run(() -> ((ThrowingConsumer) branch.getCallable()).accept(value));
        return;
      }
    }
  }

  @Value
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class ValueOfBranch<A, T> {
    private final A value;
    private final ThrowingFunction<A, T> callable;
  }

  @Value
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class ValueOfVoidBranch<A> {
    private final A value;
    private final ThrowingConsumer<A> callable;
  }

  @Nonnull
  public static <A, T> ValueOfBranch<A, T> value(@Nonnull final A value, @Nonnull final ThrowingFunction<A, T> f) {
    return new ValueOfBranch<>(value, f);
  }

  @Nonnull
  public static <A> ValueOfVoidBranch<A> value_(@Nonnull final A value, @Nonnull final ThrowingConsumer<A> f) {
    return new ValueOfVoidBranch<>(value, f);
  }


  @SuppressWarnings("unchecked")
  @SafeVarargs
  public static <T> T valueOf(final Object value, final ValueOfBranch<?, ? extends T>... typeOfBranches) {
    for (val branch : typeOfBranches) {
      if (Objects.equals(value, branch.getValue())) {
        return Try.execute(() -> (T) ((ThrowingFunction) branch.getCallable()).execute(value)).orElseThrowRuntime();
      }
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  public static void valueOf(final Object value, final ValueOfVoidBranch<?>... branches) {
    for (val branch : branches) {
      if (Objects.equals(value, branch.getValue())) {
        Try.run(() -> ((ThrowingConsumer) branch.getCallable()).accept(value));
        return;
      }
    }
  }

  public static Thread.UncaughtExceptionHandler uncaughtExceptionHandler() {
    return Optional
      .ofNullable(Thread.currentThread().getUncaughtExceptionHandler())
      .orElse(NoOp.uncaughtExceptionHandler());
  }

  public static void report(@Nonnull final Object err) {
    uncaughtExceptionHandler().uncaughtException(Thread.currentThread(), toThrowable(err));
  }

  public static void forever(@Nonnull final ThrowingRunnable runnable) {
    forever(uncaughtExceptionHandler(), runnable);
  }

  public static void forever(
    @Nonnull final Thread.UncaughtExceptionHandler exceptionHandler,
    @Nonnull final ThrowingRunnable runnable
  ) {
    while (!Thread.currentThread().isInterrupted()) {
      try {
        runnable.execute();
      } catch (final InterruptedException exc) {
        return;
      } catch (final Exception exc) {
        exceptionHandler.uncaughtException(Thread.currentThread(), exc);
      }
    }
  }

  @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
  public static boolean wait(final Object monitor) {
    while (!Thread.currentThread().isInterrupted()) {
      try {
        synchronized (monitor) {
          monitor.wait();
        }
      } catch (final InterruptedException exc) {
        return false;
      }
    }
    return true;
  }

  public static void sleep(final Duration duration) {
    final long started = System.nanoTime();
    do {
      try {
        Thread.sleep(duration.toMillis());
        return;
      } catch (final InterruptedException ignore) {
      }
    } while ((System.nanoTime() - started) < duration.toNanos());
  }

  public static void parallel(@Nonnull final Executor executor, @Nonnull final ThrowingRunnable... runnables)
    throws ParallelExecutionException {
    Objects.requireNonNull(executor, "executor must not be null");
    Objects.requireNonNull(runnables, "nullables must not be null");
    final Semaphore semaphore = new Semaphore(0);
    final List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());
    for (final ThrowingRunnable runnable : runnables) {
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
      throw new ParallelExecutionException(Seq.ofCollection(exceptions));
    }
  }

  @SuppressWarnings("unchecked")
  @SafeVarargs
  public static <T> Seq<T> parallel(final @Nonnull Executor executor, final @Nonnull Callable<? extends T>... runnables)
    throws ParallelExecutionException {
    Objects.requireNonNull(executor, "executor must not be null");
    Objects.requireNonNull(runnables, "nullables must not be null");
    final Promise[] promises = new Promise[runnables.length];
    int i = 0;
    for (final Callable<? extends T> runnable : runnables) {
      final Promise<T> promise = Promise.promise();
      promises[i++] = promise;
      executor.execute(() -> {
        try {
          final T result = runnable.call();
          promise.fulfill(result);
        } catch (final Exception exc) {
          promise.fail(exc);
        }
      });
    }
    final SeqBuilder<T> results = Seq.builder();
    final SeqBuilder<Throwable> exceptions = Seq.builder();
    for (final Promise promise : promises) {
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
    throw new ParallelExecutionException(exceptions.result());
  }

  @SafeVarargs
  public static <T> ThrowingConsumer<Callback<Seq<T>>> parallel(@Nonnull final ThrowingConsumer<Callback<T>>... actions) {
    return (callback) -> {
      final boolean[] hasResult = new boolean[actions.length];
      final Object[] results = new Object[actions.length];
      final Object[] errors = new Object[actions.length];
      final AtomicInteger outstanding = new AtomicInteger(actions.length);
      final Function<Integer, Callback<T>> cb = (ix) -> (err, res) -> {
        synchronized (actions[ix]) {
          if (hasResult[ix]) {
            report(new TaskCompletedMoreThanOnceException(actions[ix], errors[ix], results[ix], err, res));
            return;
          }
          hasResult[ix] = true;
        }
        errors[ix] = err;
        results[ix] = res;
        if (outstanding.decrementAndGet() == 0) {
          final Seq<Object> errorsSeq = Seq.ofArrayZeroCopyInternal(errors);
          final Seq<T> resultsSeq = Seq.ofArrayZeroCopyInternal(results);
          final Object error;
          if (errorsSeq.exists(Objects::nonNull)) {
            error = new ParallelExecutionException(errorsSeq.map(Control::toThrowable));
          } else {
            error = null;
          }
          callback.call(error, resultsSeq);
        }
      };
      int i = 0;
      for (final ThrowingConsumer<Callback<T>> action : actions) {
        final int myIndex = i++;
        try {
          action.consume(cb.apply(myIndex));
        } catch (final Exception exc) {
          cb.apply(myIndex).fail(exc);
        }
      }
    };
  }

  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @With(AccessLevel.PRIVATE)
  public static final class Async<In, Out> implements ThrowingBiConsumer<In, Callback<Out>>, Function<In, Promise<Out>> {

    @Value
    @With
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
      this((opts, arg, callback) -> function.consume(arg, (error, result) -> callback.call(opts, error, result)));
    }

    @Nonnull
    @SuppressWarnings("CodeBlock2Expr")
    public final <T> Async<In, T> then(@Nonnull final ThrowingBiConsumer<Out, Callback<T>> function) {
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
    public final <T> Async<In, Seq<T>> then(
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
        } else {
          promise.fail(toThrowable(error));
        }
      });
      return promise;
    }

    @Override
    public void consume(final In argument, @Nonnull final Callback<Out> callback) {
      run(argument, callback);
    }

    @Nonnull
    @Override
    public Promise<Out> apply(final In in) {
      return runPromised(in);
    }
  }

  @Nonnull
  public static <A> Async<A, A> async() {
    return new Async<>((t, cb) -> cb.success(t));
  }

  @Nonnull
  public static <In, Out> Async<In, Out> async(@Nonnull final ThrowingBiConsumer<In, Callback<Out>> function) {
    Objects.requireNonNull(function, "'function' must not be null.");
    return new Async<>(function);
  }

  @Nonnull
  public static <A> Async<A, A> waterfall(@Nonnull final List<ThrowingBiConsumer<A, Callback<A>>> fs) {
    return waterfall(Seq.wrap(fs));
  }

  @Nonnull
  public static <A> Async<A, A> waterfall(@Nonnull final Seq<ThrowingBiConsumer<A, Callback<A>>> fs) {
    return fs.foldl(Async::then, async());
  }

  @Nonnull
  @SafeVarargs
  public static <A> Async<A, A> waterfall(@Nonnull final ThrowingBiConsumer<A, Callback<A>>... fs) {
    return waterfall(Seq.ofArrayZeroCopyInternal(fs));
  }

  @SuppressWarnings("unchecked")
  public static Throwable toThrowable(final Object object) {
    if (object instanceof Throwable) {
      return (Throwable) object;
    }
    if (object instanceof String) {
      return new LightweightRuntimeException((String) object);
    }
    if (object instanceof Seq) {
      if (((Seq) object).forAll(obj -> obj instanceof Throwable)) {
        return new ParallelExecutionException((Seq<Throwable>) object);
      }
    }
    return new AsyncExecutionException(object);
  }

  /**
   * Iterates a function on a given value until a certain exit criterion is met.
   * <p>
   * Example: Finding the square root of 10 until the error is less than 0.01.
   * <pre>
   * final double squareRoot = Control.iterate(
   *     value -&gt; value * (value * value &gt; 10.0 ? 0.5 : 1.5),
   *     (prev, curr) -&gt; Math.abs(10.0 - curr * curr) &lt; 0.01,
   *     10.0
   * );
   * </pre>
   * <p>
   * The exit criterion is a predicate that has access to the current value as
   * well as the value of the previous iteration. This allows for exit criteria
   * like "iterate until the value does not change much".
   *
   * @param operation     The function to iterate on the given value.
   * @param exitCriterion The predicate that decided when to stop iterating, with
   *                      respect to the previous' iteration value and the current's
   *                      iteration value.
   * @param startValue    The value to start iterating with.
   * @param <A>           The type of the iterated values.
   * @return The resulting value.
   */
  public static <A> A iterate(
    @Nonnull final UnaryOperator<A> operation,
    @Nonnull final BiPredicate<A, A> exitCriterion,
    final A startValue
  ) {
    Objects.requireNonNull(operation, "'operation' must not be null");
    Objects.requireNonNull(exitCriterion, "'exitCriterion' must not be null");
    A previousValue;
    A currentValue = startValue;
    do {
      previousValue = currentValue;
      currentValue = operation.apply(currentValue);
    } while (!exitCriterion.test(previousValue, currentValue));
    return currentValue;
  }

  /**
   * Applies the given function on a value until that value does not change anymore
   * (i.e. until it reaches a fixed point).
   *
   * @param operation  The function to be applied iteratively.
   * @param startValue The value to start with.
   * @param <A>        The type of the value to be iterated on.
   * @return A fixed point of the given operation.
   */
  public static <A> A exhaustively(@Nonnull final UnaryOperator<A> operation, final A startValue) {
    Objects.requireNonNull(operation, "'operation' must not be null");
    return iterate(operation, Objects::equals, startValue);
  }

  public static <E> void swap(@Nonnull final E[] array, final int i, final int j) {
    Objects.requireNonNull(array, "'array' must not be null");
    final E src = array[i];
    array[i] = array[j];
    array[j] = src;
  }

  public static void swap(@Nonnull final boolean[] array, final int i, final int j) {
    Objects.requireNonNull(array, "'array' must not be null");
    final boolean src = array[i];
    array[i] = array[j];
    array[j] = src;
  }

  public static void swap(@Nonnull final char[] array, final int i, final int j) {
    Objects.requireNonNull(array, "'array' must not be null");
    final char src = array[i];
    array[i] = array[j];
    array[j] = src;
  }

  public static void swap(@Nonnull final byte[] array, final int i, final int j) {
    Objects.requireNonNull(array, "'array' must not be null");
    final byte src = array[i];
    array[i] = array[j];
    array[j] = src;
  }

  public static void swap(@Nonnull final short[] array, final int i, final int j) {
    Objects.requireNonNull(array, "'array' must not be null");
    final short src = array[i];
    array[i] = array[j];
    array[j] = src;
  }

  public static void swap(@Nonnull final int[] array, final int i, final int j) {
    Objects.requireNonNull(array, "'array' must not be null");
    final int src = array[i];
    array[i] = array[j];
    array[j] = src;
  }

  public static void swap(@Nonnull final long[] array, final int i, final int j) {
    Objects.requireNonNull(array, "'array' must not be null");
    final long src = array[i];
    array[i] = array[j];
    array[j] = src;
  }

  public static void swap(@Nonnull final double[] array, final int i, final int j) {
    Objects.requireNonNull(array, "'array' must not be null");
    final double src = array[i];
    array[i] = array[j];
    array[j] = src;
  }

  public static void swap(@Nonnull final float[] array, final int i, final int j) {
    Objects.requireNonNull(array, "'array' must not be null");
    final float src = array[i];
    array[i] = array[j];
    array[j] = src;
  }

  /**
   * Turns a function into a memoizing function which caches result values for the given
   * arguments.
   *
   * <pre>
   * final Function&lt;A, B&gt; f = Control.memoizing(arg -&gt; expensiveCalculation(arg));
   * final A one = ...;
   * final A two = ...;
   * f.apply(one); // actually invokes expensiveCalculation(one)
   * f.apply(one); // returns the cached value for the key "one"
   * f.apply(two); // actually invokes expensiveCalculation(two)
   * </pre>
   *
   * @param function A pure function (a proper function which returns the same value for a given argument every time)
   * @param <A>      The type of the argument to the function.
   * @param <R>      The type of the results of the function.
   * @return The memoizing function.
   */
  @Nonnull
  public static <A, R> Function<A, R> memoizing(@Nonnull final Function<A, R> function) {
    Objects.requireNonNull(function, "'function' must not be null");
    final HashMap<A, R> cachedValues = new HashMap<>();
    return arg -> cachedValues.computeIfAbsent(arg, function);
  }

  @Nonnull
  public static <R> IntFunction<R> memoizing(@Nonnull final IntFunction<R> function) {
    Objects.requireNonNull(function, "'function' must not be null");
    final HashMap<Integer, R> cachedValues = new HashMap<>();
    return arg -> cachedValues.computeIfAbsent(arg, function::apply);
  }

  @Nonnull
  public static <R> LongFunction<R> memoizing(@Nonnull final LongFunction<R> function) {
    Objects.requireNonNull(function, "'function' must not be null");
    final HashMap<Long, R> cachedValues = new HashMap<>();
    return arg -> cachedValues.computeIfAbsent(arg, function::apply);
  }

  @AllArgsConstructor
  private final static class SupplierMemoBox<T> {
    private Supplier<T> supplier;
    private T value;
  }

  /**
   * Turns a supplier into a memoizing supplier, which invokes the actual supplier only
   * once, caches the result, and returns that henceforth.
   *
   * <pre>
   * class Thing {
   *   static Thing expensiveFactory() { ... }
   * }
   * final Supplier&lt;Thing&gt; supplier = Control.memoizing(Thing::expensiveFactory);
   * final Thing thing = supplier.get();
   * final Thing thing2 = supplier.get(); // returns the same thing
   * </pre>
   *
   * @param supplier The supplier to be turned into a memoizing supplier.
   * @param <T>      The type of the thing the supplier supplies.
   * @return A memoizing supplier which calls the given supplier only ever once.
   */
  @SuppressWarnings("unchecked")
  @Nonnull
  public static <T> Supplier<T> memoizing(@Nonnull final Supplier<T> supplier) {
    Objects.requireNonNull(supplier, "'supplier' must not be null");
    final SupplierMemoBox<T> box = new SupplierMemoBox<>(supplier, null);
    return () -> {
      if (box.supplier != null) {
        box.value = box.supplier.get();
        box.supplier = null;
      }
      return box.value;
    };
  }

  /**
   * Turns a supplier into a thread safe supplier, that is the invocation of get will be
   * guarded by synchronization.
   * <p>
   * Useful to get a thread-safe version of a memoizing supplier:
   * <pre>
   * final Supplier&lt;Thing&gt; thingSupplier = &lt;/Thing&gt;Control.atomic(Control.memoizing(Thing::expensiveFactory));
   * </pre>
   */
  @Nonnull
  public static <T> Supplier<T> atomic(@Nonnull final Supplier<T> supplier) {
    Objects.requireNonNull(supplier, "'supplier' must not be null");
    return () -> {
      synchronized (supplier) {
        return supplier.get();
      }
    };
  }
}
