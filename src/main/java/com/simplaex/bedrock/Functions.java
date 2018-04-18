package com.simplaex.bedrock;

import lombok.experimental.UtilityClass;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

@UtilityClass
public class Functions {

  public static <A, B, C> Function<A, C> compose(@Nonnull final Function<B, C> f, @Nonnull final Function<A, B> g) {
    return a -> f.apply(g.apply(a));
  }

  public static <A, B, C> Function<A, Function<B, C>> curry(@Nonnull final BiFunction<A, B, C> f) {
    return a -> b -> f.apply(a, b);
  }

  public static <A, B, C> BiFunction<A, B, C> uncurry(@Nonnull final Function<A, Function<B, C>> f) {
    return (a, b) -> f.apply(a).apply(b);
  }

  public static <A, B, C> Function<Pair<A, B>, C> curryPair(@Nonnull final BiFunction<A, B, C> f) {
    return p -> f.apply(p.fst(), p.snd());
  }

  public static <A, B, C> BiFunction<A, B, C> uncurryPair(@Nonnull final Function<Pair<A, B>, C> f) {
    return (a, b) -> f.apply(Pair.of(a, b));
  }

  public static <T> Predicate<T> not(@Nonnull final Predicate<T> predicate) {
    return predicate.negate();
  }

}
