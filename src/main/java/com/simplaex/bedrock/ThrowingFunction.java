package com.simplaex.bedrock;

import java.util.function.Function;

@FunctionalInterface
public interface ThrowingFunction<A, R> extends Function1<A, R> {

  R execute(final A a) throws Exception;

  @Override
  default R apply(final A arg) {
    try {
      return execute(arg);
    } catch (final Error | RuntimeException exc) {
      throw exc;
    } catch (final Exception exc) {
      throw new RuntimeException(exc);
    }
  }

  default Function<A, Try<R>> safe() {
    return arg -> Try.execute(() -> execute(arg));
  }
}
