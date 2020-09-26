package de.scravy.bedrock.hlist;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.BiFunction;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Nil extends HList<Nil> {

  final static Nil INSTANCE = new Nil();

  @Override
  @Nonnegative
  public int size() {
    return 0;
  }

  @Override
  public <T> T foldl(@Nonnull final BiFunction<T, Object, T> f, final T init) {
    return init;
  }

  @Override
  public <T> T foldr(@Nonnull final BiFunction<Object, T, T> f, final T init) {
    return init;
  }

  @Override
  Nil mask(@Nonnegative final int index, @Nonnull final ForEachWithIndexPredicate predicate) {
    return this;
  }

  @Nonnull
  public static <E> C<E, Nil> cons(final E elem) {
    return new C<>(elem, INSTANCE);
  }

  @Nonnull
  @Override
  public Iterator<Object> iterator() {
    return Collections.emptyIterator();
  }

  @Override
  public int compareTo(@Nonnull final Nil ignore) {
    return 0;
  }
}
