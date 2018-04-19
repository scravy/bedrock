package com.simplaex.bedrock;

import lombok.experimental.UtilityClass;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

@UtilityClass
public class Functions {

  public static <A> UnaryOperator<A> id() {
    return x -> x;
  }

  public static <A, B> Function<B, A> constant(final A a) {
    return b -> a;
  }

  public static <A, B> Function<A, Function<B, A>> constant() {
    return a -> b -> a;
  }

  public static <A, B, C> Function<A, C> compose(@Nonnull final Function<B, C> f, @Nonnull final Function<A, B> g) {
    return a -> f.apply(g.apply(a));
  }

  public static <A, B, C> Function<A, Function<B, C>> curry(@Nonnull final BiFunction<A, B, C> f) {
    return a -> b -> f.apply(a, b);
  }

  public static <A, B, C> BiFunction<A, B, C> uncurry(@Nonnull final Function<A, Function<B, C>> f) {
    return (a, b) -> f.apply(a).apply(b);
  }

  public static <A, B, C> Function<A, Function<B, C>> curryPair(@Nonnull final Function<Pair<A, B>, C> f) {
    return a -> b -> f.apply(Pair.of(a, b));
  }

  public static <A, B, C> Function<Pair<A, B>, C> uncurryPair(@Nonnull final Function<A, Function<B, C>> f) {
    return p -> f.apply(p.fst()).apply(p.snd());
  }

  public static <T> Predicate<T> not(@Nonnull final Predicate<T> predicate) {
    Objects.requireNonNull(predicate, "'predicate' must not be null");
    return predicate.negate();
  }

  public static <T> Predicate<T> predicate(@Nonnull final Function<T, Boolean> predicate) {
    return predicate::apply;
  }

}
