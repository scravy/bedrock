package com.simplaex.bedrock;

import java.util.function.BiFunction;

@FunctionalInterface
public interface ThrowingBiFunction<A, B, R> extends BiFunction<A, B, R> {

  R execute(final A a, final B b) throws Exception;

  @Override
  default R apply(final A a, final B b) {
    try {
      return execute(a, b);
    } catch (final Error | RuntimeException exc) {
      throw exc;
    } catch (final Exception exc) {
      throw new RuntimeException(exc);
    }
  }

  default BiFunction<A, B, Try<R>> safe() {
    return (a, b) -> Try.execute(() -> execute(a, b));
  }
}
