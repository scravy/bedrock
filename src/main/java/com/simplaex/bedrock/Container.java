package com.simplaex.bedrock;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Stream;

public interface Container<E> extends Iterable<E> {

  boolean isEmpty();

  default boolean nonEmpty() {
    return !isEmpty();
  }

  @Nonnull
  default String asString() {
    return asString("");
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
