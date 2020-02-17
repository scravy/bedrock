package com.simplaex.bedrock.hlist;

import com.simplaex.bedrock.Container;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.BiFunction;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class HList<This extends HList<This>> implements Container<Object>, Comparable<This> {

  @Nonnull
  public static Nil empty() {
    return Nil.INSTANCE;
  }

  @Nonnull
  public static Nil nil() {
    return empty();
  }

  @Nonnull
  public static <E, L extends HList<L>> C<E, L> cons(final E head, final L tail) {
    return new C<>(head, Objects.requireNonNull(tail, "'tail' must not be null"));
  }

  @Nonnull
  public static <E> C<E, Nil> hlist(final E e) {
    return cons(e, empty());
  }

  @Nonnull
  public static <E, F> C<E, C<F, Nil>> hlist(final E e, final F f) {
    return cons(e, hlist(f));
  }

  @Nonnull
  public static <E, F, G> C<E, C<F, C<G, Nil>>> hlist(final E e, final F f, final G g) {
    return cons(e, hlist(f, g));
  }

  @Nonnull
  public static <E, F, G, H> C<E, C<F, C<G, C<H, Nil>>>> hlist(final E e, final F f, final G g, final H h) {
    return cons(e, hlist(f, g, h));
  }

  @Nonnull
  public static <E, F, G, H, I> C<E, C<F, C<G, C<H, C<I, Nil>>>>> hlist(
    final E e, final F f, final G g, final H h, final I i
  ) {
    return cons(e, hlist(f, g, h, i));
  }

  @Nonnull
  public static <E, F, G, H, I, J> C<E, C<F, C<G, C<H, C<I, C<J, Nil>>>>>> hlist(
    final E e, final F f, final G g, final H h, final I i, final J j
  ) {
    return cons(e, hlist(f, g, h, i, j));
  }

  @Nonnull
  public static <E, F, G, H, I, J, K> C<E, C<F, C<G, C<H, C<I, C<J, C<K, Nil>>>>>>> hlist(
    final E e, final F f, final G g, final H h, final I i, final J j, final K k
  ) {
    return cons(e, hlist(f, g, h, i, j, k));
  }

  @Nonnull
  public static <E, F, G, H, I, J, K, L> C<E, C<F, C<G, C<H, C<I, C<J, C<K, C<L, Nil>>>>>>>> hlist(
    final E e, final F f, final G g, final H h, final I i, final J j, final K k, final L l
  ) {
    return cons(e, hlist(f, g, h, i, j, k, l));
  }

  @Nonnull
  public static <E, F, G, H, I, J, K, L, M> C<E, C<F, C<G, C<H, C<I, C<J, C<K, C<L, C<M, Nil>>>>>>>>> hlist(
    final E e, final F f, final G g, final H h, final I i, final J j, final K k, final L l, final M m
  ) {
    return cons(e, hlist(f, g, h, i, j, k, l, m));
  }

  @Nonnull
  public static <E, F, G, H, I, J, K, L, M, N> C<E, C<F, C<G, C<H, C<I, C<J, C<K, C<L, C<M, C<N, Nil>>>>>>>>>> hlist(
    final E e, final F f, final G g, final H h, final I i, final J j, final K k, final L l, final M m, final N n
  ) {
    return cons(e, hlist(f, g, h, i, j, k, l, m, n));
  }

  @Nonnull
  public static <E, F, G, H, I, J, K, L, M, N, O> C<E, C<F, C<G, C<H, C<I, C<J, C<K, C<L, C<M, C<N, C<O, Nil>>>>>>>>>>> hlist(
    final E e, final F f, final G g, final H h, final I i, final J j, final K k, final L l, final M m, final N n, final O o
  ) {
    return cons(e, hlist(f, g, h, i, j, k, l, m, n, o));
  }

  @Nonnegative
  public abstract int size();

  public abstract <T> T foldl(@Nonnull final BiFunction<T, Object, T> f, final T init);

  public abstract <T> T foldr(@Nonnull final BiFunction<Object, T, T> f, final T init);

  @FunctionalInterface
  public interface ForEachWithIndexPredicate {
    boolean test(@Nonnegative final int index, final Object obj);
  }

  @Nonnull
  public This mask(@Nonnull final ForEachWithIndexPredicate predicate) {
    return mask(0, predicate);
  }

  abstract This mask(@Nonnegative final int index, @Nonnull final ForEachWithIndexPredicate predicate);
}
