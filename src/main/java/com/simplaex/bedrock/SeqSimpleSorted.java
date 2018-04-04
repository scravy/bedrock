package com.simplaex.bedrock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

class SeqSimpleSorted<E extends Comparable<E>> extends SeqSimple<E> {

  SeqSimpleSorted(@Nonnull final Object[] array) {
    super(array);
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean contains(@Nullable final E element) {
    return Arrays.binarySearch(backingArray, element, NULL_ACCEPTING_COMPARATOR) >= 0;
  }

  @Override
  public int find(@Nullable final E element) {
    final int ix = Arrays.binarySearch(backingArray, element, NULL_ACCEPTING_COMPARATOR);
    return ix >= 0 ? ix : -1;
  }

  @Nonnull
  @Override
  public Seq<E> sorted() {
    return this;
  }

}
