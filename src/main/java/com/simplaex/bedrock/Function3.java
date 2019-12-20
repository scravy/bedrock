package com.simplaex.bedrock;

@FunctionalInterface
public interface Function3<A, B, C, R> {

  R apply(final A a, final B b, final C c);

  default Function2<B, C, R> bind(final A a) {
    return (b, c) -> Function3.this.apply(a, b, c);
  }

  default Function1<C, R> bind(final A a, final B b) {
    return (c) -> Function3.this.apply(a, b, c);
  }

  default Function0<R> bind(final A a, final B b, final C c) {
    return () -> Function3.this.apply(a, b, c);
  }

  default Function1<Tuple3<A, B, C>, R> tupled() {
    return (t) -> Function3.this.apply(t.getFirst(), t.getSecond(), t.getThird());
  }
}
