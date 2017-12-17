package com.simplaex.bedrock;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

class SeqReversed<E> extends Seq<E> {

  SeqReversed(@Nonnull final Object[] array) {
    super(array);
  }

  @SuppressWarnings("unchecked")
  @Override
  public E get(@Nonnegative final int index) {
    checkBounds(index);
    return (E) backingArray[backingArray.length - index - 1];
  }

  @Nonnull
  @Override
  public Seq<E> reversed() {
    return new SeqSimple<>(backingArray);
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  @Override
  public Seq<E> sorted() {
    final Object[] array = backingArray.clone();
    Arrays.sort(array, (left, right) -> {
      if (left == null && right == null) {
        return 0;
      }
      if (left == null) {
        return -1;
      }
      if (right == null) {
        return 1;
      }
      return ((Comparable) left).compareTo(right);
    });
    return new SeqSimpleSorted(array);
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  @Override
  public Seq<E> sortedBy(@Nonnull final Comparator<? super E> comparator) {
    Objects.requireNonNull(comparator);
    final Object[] array = backingArray.clone();
    Arrays.sort(array, (Comparator<Object>) comparator);
    return new SeqSimple<>(array);
  }

  @Nonnull
  @Override
  public Seq<E> trimmedToSize() {
    return this;
  }

  @Nonnull
  @Override
  public Object[] toArray() {
    final Object[] array = backingArray.clone();
    reverse(array);
    return array;
  }

  @Nonnull
  @Override
  public Seq<E> subSequence(@Nonnegative final int beginOffset, @Nonnegative final int endOffset) {
    final int begin = Math.max(0, beginOffset);
    final int end = Math.min(length(), endOffset);
    final int len = end - begin;
    if (len <= 0) {
      return empty();
    }
    final Object[] array = new Object[len];
    System.arraycopy(backingArray, length() - end, array, 0, len);
    return new SeqReversed<>(array);
  }

  @Nonnull
  @Override
  public Seq<E> subSequenceView(@Nonnegative final int beginOffset, @Nonnegative final int endOffset) {
    final int begin = Math.max(0, beginOffset);
    final int end = Math.min(length(), endOffset);
    final int len = end - begin;
    if (len <= 0) {
      return empty();
    }
    return new SeqReversedView<>(backingArray, length() - end, length() - begin);
  }

  @Nonnull
  @Override
  public E[] toArray(@Nonnull final Class<E> evidence) {
    Objects.requireNonNull(evidence);
    final int len = backingArray.length;
    @SuppressWarnings("unchecked") final E[] array = (E[]) Array.newInstance(evidence, len);
    //noinspection SuspiciousSystemArraycopy
    System.arraycopy(backingArray, 0, array, 0, len);
    reverse(array);
    return array;
  }

  @Override
  @Nonnegative
  public int length() {
    return backingArray.length;
  }
}
