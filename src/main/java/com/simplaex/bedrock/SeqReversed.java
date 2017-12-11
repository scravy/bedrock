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

  @Nonnull
  @Override
  public Seq<E> sorted() {
    final Object[] array = backingArray.clone();
    Arrays.sort(array);
    return new SeqSimple<>(array);
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
    final int backingEndOffset = length() - beginOffset;
    final int backingBeginOffset = length() - endOffset;
    final int len = endOffset - beginOffset;
    if (len <= 0 || backingBeginOffset + len > length()) {
      return empty();
    }
    final Object[] array = new Object[len];
    System.arraycopy(backingArray, backingBeginOffset, array, 0, len);
    return new SeqReversed<>(array);
  }

  @Nonnull
  @Override
  public Seq<E> subSequenceView(@Nonnegative final int beginOffset, @Nonnegative final int endOffset) {
    final int len = endOffset - beginOffset;
    if (len <= 0 || beginOffset + len > length()) {
      return empty();
    }
    final Object[] array = new Object[len];
    return new SeqReversedView<>(array, backingArray.length - endOffset - 1, backingArray.length - beginOffset - 1);
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
