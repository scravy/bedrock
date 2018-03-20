package com.simplaex.bedrock;

import lombok.SneakyThrows;

import java.util.function.Consumer;
import java.util.function.Function;

@FunctionalInterface
public interface ThrowingConsumer<A> extends Consumer<A> {

  void consume(final A arg) throws Exception;

  @Override
  @SneakyThrows
  default void accept(final A arg) {
    consume(arg);
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
