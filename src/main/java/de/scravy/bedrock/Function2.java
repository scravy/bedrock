package de.scravy.bedrock;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;

@FunctionalInterface
public interface Function2<A, B, R> extends BiFunction<A, B, R> {

  @Override
  R apply(final A a, final B b);

  @Nonnull
  default Function1<B, R> bind(final A a) {
    return (b) -> Function2.this.apply(a, b);
  }

  @Nonnull
  default Function0<R> bind(final A a, final B b) {
    return () -> Function2.this.apply(a, b);
  }

  @Nonnull
  default Function1<Tuple2<A, B>, R> tupled() {
    return (t) -> Function2.this.apply(t.getFirst(), t.getSecond());
  }

  @Nonnull
  default Function1<A, Function1<B, R>> curried() {
    return a -> b -> this.apply(a, b);
  }

  @Nonnull
  default Function2<B, A, R> flipped() {
    return new Function2<B, A, R>() {
      @Override
      public R apply(final B b, final A a) {
        return Function2.this.apply(a, b);
      }

      @Override
      public Function2<A, B, R> flipped() {
        return Function2.this;
      }
    };
  }

  @Nonnull
  static <A, B, R> Function2<A, B, R> from(final BiFunction<A, B, R> f) {
    return f::apply;
  }
}
