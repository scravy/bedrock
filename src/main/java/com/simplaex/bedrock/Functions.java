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

  /**
   * Returns the identity function. Identical to {@link Function#identity()}.
   *
   * @param <A> The type of the argument and return value.
   * @return A function that always returns what is passed into it.
   */
  public static <A> UnaryOperator<A> id() {
    return x -> x;
  }

  /**
   * Returns a function that ignores it's arguments and always returns the
   * given value.
   *
   * @param a   The given value.
   * @param <A> The type of the argument which is returned by the returned function.
   * @param <B> The type of the returned functions parameter.
   * @return The constant function.
   */
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

  /**
   * Turns a function that results in a boxed Boolean into a Predicate.
   *
   * @param predicate The function.
   * @param <T>       The type of the argument of the function.
   * @return The same function as a Predicate.
   */
  public static <T> Predicate<T> predicate(@Nonnull final Function<T, Boolean> predicate) {
    return predicate::apply;
  }

}
