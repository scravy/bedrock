package com.simplaex.bedrock;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.simplaex.bedrock.Functions.curry;
import static com.simplaex.bedrock.Functions.predicate;

@FunctionalInterface
public interface Container<E> extends ExtendedIterable<E> {

  default boolean isEmpty() {
    return !iterator().hasNext();
  }

  default boolean nonEmpty() {
    return !isEmpty();
  }

  @Nonnull
  default String asString() {
    final StringBuilder b = new StringBuilder();
    forEach(b::append);
    return b.toString();
  }

  @Nonnull
  default String asString(@Nonnull final String delimiter) {
    return stream().map(Objects::toString).collect(Collectors.joining(delimiter));
  }

  default E draw(@Nonnull final Random random) throws NoSuchElementException {
    if (isEmpty()) {
      throw new NoSuchElementException();
    }
    return Seq.ofCollectionInternal(toList()).draw(random);
  }

  default E draw() throws NoSuchElementException {
    return draw(ThreadLocalRandom.current());
  }

  default boolean forAll(@Nonnull final Predicate<? super E> predicate) {
    Objects.requireNonNull(predicate, "'predicate' must not be null");
    for (final E e : this) {
      if (!predicate.test(e)) {
        return false;
      }
    }
    return true;
  }

  default boolean exists(@Nonnull final Predicate<? super E> predicate) {
    Objects.requireNonNull(predicate, "'predicate' must not be null");
    for (final E e : this) {
      if (predicate.test(e)) {
        return true;
      }
    }
    return false;
  }

  default boolean contains(final E element) {
    return exists(predicate(curry(Objects::equals).apply(element)));
  }

  @Nonnull
  default Stream<E> stream() {
    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(), 0), false);
  }

  @Nonnull
  default List<E> toList() {
    final List<E> list = new ArrayList<>();
    for (final E e : this) {
      list.add(e);
    }
    return list;
  }

  static <E> Container<E> fromIterable(final Iterable<E> e) {
    return e::iterator;
  }
}
