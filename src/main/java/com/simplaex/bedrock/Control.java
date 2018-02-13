package com.simplaex.bedrock;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.UtilityClass;
import lombok.val;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * Missing control structures for Java.
 */
@UtilityClass
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
      if (branch.clazz.isAssignableFrom(value.getClass())) {
        return Try.execute(() -> (T) ((ThrowingFunction) branch.callable).execute(value)).orElseThrowRuntime();
      }
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  public static void dispatch(final Object value, final DispatchVoidBranch<?>... branches) {
    for (val branch : branches) {
      if (branch.clazz.isAssignableFrom(value.getClass())) {
        Try.run(() -> ((ThrowingConsumer) branch.callable).accept(value));
        return;
      }
    }
  }

  public static void forever(final @Nonnull Runnable runnable) {
    while (!Thread.currentThread().isInterrupted()) {
      runnable.run();
    }
  }

  public static void forever(final @Nonnull Consumer<Exception> exceptionHandler, final @Nonnull ThrowingRunnable runnable) {
    while (!Thread.currentThread().isInterrupted()) {
      try {
        runnable.run();
      } catch (final Exception exc) {
        exceptionHandler.accept(exc);
      }
    }
  }

}
