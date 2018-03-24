package com.simplaex.bedrock;

import java.util.function.Consumer;

@FunctionalInterface
public interface ThrowingRunnable extends Runnable {

  void execute() throws Exception;

  @Override
  default void run() {
    try {
      execute();
    } catch (final Error | RuntimeException exc) {
      throw exc;
    } catch (final Exception exc) {
      throw new RuntimeException(exc);
    }
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
