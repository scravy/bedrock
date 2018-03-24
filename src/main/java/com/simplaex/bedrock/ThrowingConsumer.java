package com.simplaex.bedrock;

import java.util.function.Consumer;

@FunctionalInterface
public interface ThrowingConsumer<A> extends Consumer<A> {

  void consume(final A arg) throws Exception;

  @Override
  default void accept(final A arg) {
    try {
      consume(arg);
    } catch (final Error | RuntimeException exc) {
      throw exc;
    } catch (final Exception exc) {
      throw new RuntimeException(exc);
    }
  }

  default Consumer<A> safe(final Consumer<? super Exception> errorHandler) {
    return arg -> {
      try {
        consume(arg);
      } catch (final Exception exc) {
        errorHandler.accept(exc);
      }
    };
  }
}
