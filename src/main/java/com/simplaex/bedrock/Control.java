package com.simplaex.bedrock;

import lombok.*;
import lombok.experimental.UtilityClass;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.function.Consumer;

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

  public static <A, T> DispatchBranch<A, T> branch(final Class<A> clazz, final ThrowingFunction<A, T> f) {
    return new DispatchBranch<>(clazz, f);
  }

  public static <A> DispatchVoidBranch<A> voidBranch(final Class<A> clazz, final ThrowingConsumer<A> f) {
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

  public static void forever(final @Nonnull ThrowingRunnable runnable) {
    while (!Thread.currentThread().isInterrupted()) {
      try {
        runnable.run();
      } catch (final InterruptedException exc) {
        return;
      } catch (final Exception ignore) {
      }
    }
  }

  public static void forever(final @Nonnull Consumer<Exception> exceptionHandler, final @Nonnull ThrowingRunnable runnable) {
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

}
