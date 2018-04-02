package com.simplaex.bedrock;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

class SeqSimpleView<E> extends Seq<E> {

  private final int beginOffset;
  private final int endOffset;

  private final Object[] backingArray;

  SeqSimpleView(
    @Nonnull final Object[] array,
    @Nonnegative final int beginOffset,
    @Nonnegative final int endOffset
  ) {
    this.backingArray = array;
    this.beginOffset = beginOffset;
    this.endOffset = endOffset;
  }

  @SuppressWarnings("unchecked")
  @Override
  public E get(@Nonnegative final int index) {
    checkBounds(index);
    return (E) backingArray[beginOffset + index];
  }

  @Nonnull
  @Override
  public Seq<E> reversed() {
    return new SeqReversedView<>(backingArray, beginOffset, endOffset);
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  @Override
  public Seq<E> sorted() {
    final Object[] array = new Object[length()];
    System.arraycopy(backingArray, beginOffset, array, 0, length());
    Arrays.sort(array, nullAcceptingComparator);
    return new SeqSimpleSorted(array);
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  @Override
  public Seq<E> sortedBy(@Nonnull Comparator<? super E> comparator) {
    Objects.requireNonNull(comparator);
    final Object[] array = new Object[length()];
    System.arraycopy(backingArray, beginOffset, array, 0, length());
    Arrays.sort(array, (Comparator<Object>) comparator);
    return new SeqSimple<>(array);
  }

  @Nonnull
  @Override
  public Seq<E> trimmedToSize() {
    final Object[] array = new Object[length()];
    System.arraycopy(backingArray, beginOffset, array, 0, length());
    return new SeqSimple<>(array);
  }

  @Nonnull
  @Override
  public Object[] toArray() {
    final int len = length();
    final Object[] array = new Object[len];
    System.arraycopy(backingArray, beginOffset, array, 0, len);
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
    System.arraycopy(backingArray, this.beginOffset + begin, array, 0, len);
    return new SeqSimple<>(array);
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
    return new SeqSimpleView<>(backingArray, this.beginOffset + begin, this.beginOffset + end);
  }

  @Nonnull
  @Override
  public E[] toArray(@Nonnull final Class<E> evidence) {
    Objects.requireNonNull(evidence);
    final int len = length();
    @SuppressWarnings("unchecked") final E[] array = (E[]) Array.newInstance(evidence, len);
    //noinspection SuspiciousSystemArraycopy
    System.arraycopy(backingArray, beginOffset, array, 0, len);
    return array;
  }

  @Nonnegative
  @Override
  public int length() {
    return endOffset - beginOffset;
  }
}
