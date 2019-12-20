package com.simplaex.bedrock;

import java.util.function.BiFunction;

@FunctionalInterface
public interface Function2<A, B, R> extends BiFunction<A, B, R> {

  @Override
  R apply(final A a, final B b);

  default Function1<B, R> bind(final A a) {
    return (b) -> Function2.this.apply(a, b);
  }

  default Function0<R> bind(final A a, final B b) {
    return () -> Function2.this.apply(a, b);
  }

  default Function1<Tuple2<A, B>, R> tupled() {
    return (t) -> Function2.this.apply(t.getFirst(), t.getSecond());
  }

  static <A, B, R> Function2<A, B, R> from(final BiFunction<A, B, R> f) {
    return f::apply;
  }
}
