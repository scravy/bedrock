package com.simplaex.bedrock;

import java.util.function.Function;

@FunctionalInterface
public interface Function1<A, R> extends Function<A, R> {

  @Override
  R apply(final A arg);

  default Function0<R> bind(final A a) {
    return () -> Function1.this.apply(a);
  }

  static <A, R> Function1<A, R> from(final Function<A, R> f) {
    return f::apply;
  }
}
