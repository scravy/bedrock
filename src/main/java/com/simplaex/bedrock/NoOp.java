package com.simplaex.bedrock;

import lombok.experimental.UtilityClass;

import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("unused")
@UtilityClass
public class NoOp {

  public static <T> Function<T, T> identity() {
    return x -> x;
  }

  public static <T, U> BiFunction<T, U, T> constant() {
    return (a, b) -> a;
  }

  public static <T> Callable<T> callable(final T argToBeReturned) {
    return () -> argToBeReturned;
  }

  public static <T> Consumer<T> consumer() {
    return __ -> {
    };
  }

  public static Thread.UncaughtExceptionHandler uncaughtExceptionHandler() {
    return (thread, exc) -> {
    };
  }

  public static <T, U> BiConsumer<T, U> biConsumer() {
    return (_1, _2) -> {
    };
  }

  public static Runnable runnable() {
    return () -> {
    };
  }

}
