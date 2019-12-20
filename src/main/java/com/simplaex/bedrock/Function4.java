package com.simplaex.bedrock;

@FunctionalInterface
public interface Function4<A, B, C, D, R> {

  R apply(final A a, final B b, final C c, final D d);

  default Function3<B, C, D, R> bind(final A a) {
    return (b, c, d) -> Function4.this.apply(a, b, c, d);
  }

  default Function2<C, D, R> bind(final A a, final B b) {
    return (c, d) -> Function4.this.apply(a, b, c, d);
  }

  default Function1<D, R> bind(final A a, final B b, final C c) {
    return (d) -> Function4.this.apply(a, b, c, d);
  }

  default Function0<R> bind(final A a, final B b, final C c, final D d) {
    return () -> Function4.this.apply(a, b, c, d);
  }
}
