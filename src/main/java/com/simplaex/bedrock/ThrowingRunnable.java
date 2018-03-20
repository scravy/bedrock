package com.simplaex.bedrock;

import lombok.SneakyThrows;

import java.util.function.Consumer;

@FunctionalInterface
public interface ThrowingRunnable extends Runnable {

  void execute() throws Exception;

  @SneakyThrows
  @Override
  default void run() {
    execute();
  }

  default Runnable safe(final Consumer<? super Exception> errorHandler) {
    return () -> {
      try {
        execute();
      } catch (final Exception exc) {
        errorHandler.accept(exc);
      }
    };
  }
}
