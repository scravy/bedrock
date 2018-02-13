package com.simplaex.bedrock;

import lombok.SneakyThrows;

import java.util.function.Function;

@FunctionalInterface
public interface ThrowingFunction<A, R> extends Function<A, R> {

  R execute(final A a) throws Exception;

  @Override
  @SneakyThrows
  default R apply(final A a) {
    return execute(a);
  }
}
