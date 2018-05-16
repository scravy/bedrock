package com.simplaex.bedrock;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.simplaex.bedrock.Functions.curry;
import static com.simplaex.bedrock.Functions.predicate;

public interface Container<E> extends Iterable<E> {

  boolean isEmpty();

  default boolean nonEmpty() {
    return !isEmpty();
  }

  @Nonnull
  default String asString() {
    return asString("");
  }

  E draw(Random random) throws NoSuchElementException;

  default E draw() throws NoSuchElementException {
    return draw(ThreadLocalRandom.current());
  }

  boolean forAll(@Nonnull Predicate<? super E> predicate);

  boolean exists(@Nonnull Predicate<? super E> predicate);

  default boolean contains(E element) {
    return exists(predicate(curry(Objects::equals).apply(element)));
  }

  @Nonnull
  String asString(String delimiter);

  @Nonnull
  default Stream<E> stream() {
    return toList().stream();
  }

  @Nonnull
  List<E> toList();

}
