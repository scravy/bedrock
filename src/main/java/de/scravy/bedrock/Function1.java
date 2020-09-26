package de.scravy.bedrock;

import javax.annotation.Nonnull;
import java.util.function.Function;

@FunctionalInterface
public interface Function1<A, R> extends Function<A, R> {

  @Override
  R apply(final A arg);

  @Nonnull
  default Function0<R> bind(final A a) {
    return () -> Function1.this.apply(a);
  }

  @Nonnull
  static <A, R> Function1<A, R> from(final Function<A, R> f) {
    return f::apply;
  }
}
