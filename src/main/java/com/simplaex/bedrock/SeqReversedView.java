package com.simplaex.bedrock;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

class SeqReversedView<E> extends Seq<E> {

  private final int beginOffset;
  private final int endOffset;

  private final Object[] backingArray;

  SeqReversedView(@Nonnull final Object[] array, @Nonnegative final int beginOffset, @Nonnegative final int endOffset) {
    this.backingArray = array;
    this.beginOffset = beginOffset;
    this.endOffset = endOffset;
  }

  @SuppressWarnings("unchecked")
  @Override
  public E get(@Nonnegative final int index) {
    checkBounds(index);
    return (E) backingArray[this.beginOffset + length() - index - 1];
  }

  @Nonnull
  @Override
  public Seq<E> reversed() {
    return new SeqSimpleView<>(backingArray, beginOffset, endOffset);
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  @Override
  public Seq<E> sorted() {
    final int len = length();
    final Object[] array = new Object[len];
    System.arraycopy(backingArray, beginOffset, array, 0, len);
    Arrays.sort(array, nullAcceptingComparator);
    return new SeqSimpleSorted(array);
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  @Override
  public Seq<E> sortedBy(@Nonnull Comparator<? super E> comparator) {
    Objects.requireNonNull(comparator);
    final int len = length();
    final Object[] array = new Object[len];
    System.arraycopy(backingArray, beginOffset, array, 0, len);
    Arrays.sort(array, (Comparator<Object>) comparator);
    return Seq.ofArrayZeroCopyInternal(array);
  }

  @Nonnull
  @Override
  public Seq<E> trimmedToSize() {
    final int len = length();
    final Object[] array = new Object[len];
    System.arraycopy(backingArray, beginOffset, array, 0, len);
    return new SeqReversed<>(array);
  }

  @Nonnull
  @Override
  public Object[] toArray() {
    final Object[] array = new Object[length()];
    int i = 0;
    for (final E e : this) {
      array[i++] = e;
    }
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
    System.arraycopy(backingArray, this.beginOffset + length() - end, array, 0, len);
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
    return new SeqReversedView<>(backingArray, this.beginOffset + length() - end, this.beginOffset + length() - begin);
  }

  @Nonnull
  @Override
  public E[] toArray(@Nonnull final Class<E> evidence) {
    Objects.requireNonNull(evidence);
    @SuppressWarnings("unchecked") final E[] array = (E[]) Array.newInstance(evidence, length());
    int i = 0;
    for (final E e : this) {
      array[i++] = e;
    }
    return array;
  }

  @Nonnegative
  @Override
  public int length() {
    return endOffset - beginOffset;
  }
}
