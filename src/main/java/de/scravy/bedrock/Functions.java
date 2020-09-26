package de.scravy.bedrock;

import lombok.experimental.UtilityClass;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.*;

@UtilityClass
public class Functions {

  /**
   * Returns the identity function. Identical to {@link Function#identity()}.
   *
   * @param <A> The type of the argument and return value.
   * @return A function that always returns what is passed into it.
   */
  @Nonnull
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
  @Nonnull
  public static <A, B> Function<B, A> constant(final A a) {
    return ignored -> a;
  }

  @Nonnull
  public static <A> ToIntFunction<A> constantInt(final int a) {
    return ignored -> a;
  }

  @Nonnull
  public static <A> ToLongFunction<A> constantLong(final long a) {
    return ignored -> a;
  }

  @Nonnull
  public static <A> ToDoubleFunction<A> constantDouble(final double a) {
    return ignored -> a;
  }

  @Nonnull
  public static <A> IntFunction<A> intConstant(final A a) {
    return ignored -> a;
  }

  @Nonnull
  public static <A> LongFunction<A> longConstant(final A a) {
    return ignored -> a;
  }

  @Nonnull
  public static <A> DoubleFunction<A> doubleConstant(final A a) {
    return ignored -> a;
  }

  @Nonnull
  public static <A, B> Function<A, Function<B, A>> constant() {
    return a -> b -> a;
  }

  @Nonnull
  public static <A, B, C> Function<A, C> compose(@Nonnull final Function<B, C> f, @Nonnull final Function<A, B> g) {
    return a -> f.apply(g.apply(a));
  }

  @Nonnull
  public static <A, B, C> Function<A, Function<B, C>> curry(@Nonnull final BiFunction<A, B, C> f) {
    return a -> b -> f.apply(a, b);
  }

  @Nonnull
  public static <A, B, C> Function<Pair<A, B>, C> curryPair(@Nonnull final BiFunction<A, B, C> f) {
    return p -> f.apply(p.fst(), p.snd());
  }

  @Nonnull
  public static <A, B, C> Function<A, Function<B, C>> curry(@Nonnull final Function<Pair<A, B>, C> f) {
    return a -> b -> f.apply(Pair.of(a, b));
  }

  @Nonnull
  public static <A, B, C> BiFunction<A, B, C> uncurry(@Nonnull final Function<A, Function<B, C>> f) {
    return (a, b) -> f.apply(a).apply(b);
  }

  @Nonnull
  public static <A, B, C> Function<Pair<A, B>, C> uncurryPair(@Nonnull final Function<A, Function<B, C>> f) {
    return p -> f.apply(p.fst()).apply(p.snd());
  }

  @Nonnull
  public static <T> Predicate<T> not(@Nonnull final Predicate<T> predicate) {
    Objects.requireNonNull(predicate, "'predicate' must not be null");
    return predicate.negate();
  }

  /**
   * Combines many predicates into a predicate that accepts its input only if it passes all predicates.
   *
   * @param ps  The predicates - if no predicates are given the predicate will return true always.
   * @param <T> The type of the value to be tested by the predicates.
   * @return
   */
  @SuppressWarnings("unchecked")
  @SafeVarargs
  @Nonnull
  public static <T> Predicate<T> and(final Predicate<? extends T>... ps) {
    return c -> {
      for (final Predicate<? extends T> p : ps) {
        if (!((Predicate<T>) p).test(c)) {
          return false;
        }
      }
      return true;
    };
  }

  /**
   * Combines many predicates into a predicate that accepts its input it if it passes one of these.
   *
   * @param ps  The predicates - if no predicates are given the predicate will return false always.
   * @param <T> The type of the value to be tested by the predicates.
   * @return
   */
  @SuppressWarnings("unchecked")
  @SafeVarargs
  @Nonnull
  public static <T> Predicate<T> or(final Predicate<? extends T>... ps) {
    return c -> {
      for (final Predicate<? extends T> p : ps) {
        if (((Predicate<T>) p).test(c)) {
          return true;
        }
      }
      return false;
    };
  }

  /**
   * Turns a function that results in a boxed Boolean into a Predicate.
   *
   * @param predicate The function.
   * @param <T>       The type of the argument of the function.
   * @return The same function as a Predicate.
   */
  @Nonnull
  public static <T> Predicate<T> predicate(@Nonnull final Function<T, Boolean> predicate) {
    return predicate::apply;
  }

  @Nonnull
  public static <A, B, R> Function<B, R> bind(@Nonnull final BiFunction<A, B, R> f, final A a) {
    return b -> f.apply(a, b);
  }

  @FunctionalInterface
  public interface ToBooleanBiFunction<A, B> {
    boolean applyAsBool(final A a, final B b);
  }

  @Nonnull
  public static <A, B, R> Predicate<B> bindToBool(@Nonnull final ToBooleanBiFunction<A, B> f, final A a) {
    return b -> f.applyAsBool(a, b);
  }

  @Nonnull
  public static LongUnaryOperator bindLong(@Nonnull final LongBinaryOperator f, final long a) {
    return b -> f.applyAsLong(a, b);
  }

  @Nonnull
  public static DoubleUnaryOperator bindDouble(@Nonnull final DoubleBinaryOperator f, final double a) {
    return b -> f.applyAsDouble(a, b);
  }

  @Nonnull
  public static <A, B, R> Function2<B, A, R> flip(@Nonnull final BiFunction<A, B, R> f) {
    return (a, b) -> f.apply(b, a);
  }

  public static <A, R> Function1<A, R> f1(final Function<A, R> f) {
    return f::apply;
  }

  public static <A, B, R> Function2<A, B, R> f2(final BiFunction<A, B, R> f) {
    return f::apply;
  }

  public static <T> void call(final T arg, final Consumer<T> consumer) {
    consumer.accept(arg);
  }

  public static <T, U> void call(final T t, final U u, final BiConsumer<T, U> consumer) {
    consumer.accept(t, u);
  }

  public static <A, R> R apply(final A arg, final Function<A, R> f) {
    return f.apply(arg);
  }

  public static <A, B, R> R apply(final A arg1, final B arg2, final BiFunction<A, B, R> f) {
    return f.apply(arg1, arg2);
  }

}
