package com.simplaex.bedrock;

import lombok.SneakyThrows;

import java.util.function.BiFunction;

@FunctionalInterface
public interface ThrowingBiFunction<A, B, R> extends BiFunction<A, B, R> {

  R execute(final A a, final B b) throws Exception;

  @Override
  @SneakyThrows
  default R apply(final A a, final B b) {
    return execute(a, b);
  }

  default BiFunction<A, B, Try<R>> safe() {
    return (a, b) -> Try.execute(() -> execute(a, b));
  }
}
