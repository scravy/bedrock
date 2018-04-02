package com.simplaex.bedrock;

import lombok.val;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

class SeqSimple<E> extends Seq<E> {

  final Object[] backingArray;

  SeqSimple(@Nonnull final Object[] array) {
    this.backingArray = array;
  }

  @SuppressWarnings("unchecked")
  @Override
  public E get(@Nonnegative final int index) {
    checkBounds(index);
    return (E) backingArray[index];
  }

  @Nonnull
  @Override
  public Seq<E> reversed() {
    return new SeqReversed<>(backingArray);
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  @Override
  public Seq<E> sorted() {
    return sortedInternal();
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  SeqSimpleSorted sortedInternal() {
    final Object[] array = backingArray.clone();
    Arrays.sort(array, nullAcceptingComparator);
    return new SeqSimpleSorted(array);
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  @Override
  public Seq<E> sortedBy(@Nonnull final Comparator<? super E> comparator) {
    Objects.requireNonNull(comparator);
    final Object[] array = backingArray.clone();
    Arrays.sort((E[]) array, comparator);
    return new SeqSimple<>(array);
  }

  @Nonnull
  @Override
  public Seq<E> trimmedToSize() {
    return this;
  }

  @Nonnull
  @Override
  public E[] toArray(@Nonnull final Class<E> evidence) {
    Objects.requireNonNull(evidence);
    val length = backingArray.length;
    @SuppressWarnings("unchecked") final E[] array = (E[]) Array.newInstance(evidence, length);
    //noinspection SuspiciousSystemArraycopy
    System.arraycopy(backingArray, 0, array, 0, length);
    return array;
  }

  @Nonnull
  @Override
  public Object[] toArray() {
    return backingArray.clone();
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
    System.arraycopy(backingArray, begin, array, 0, len);
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
    return new SeqSimpleView<>(backingArray, begin, end);
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  @Override
  public Stream<E> stream() {
    return Arrays.stream((E[]) backingArray);
  }

  @Override
  @Nonnegative
  public int length() {
    return backingArray.length;
  }
}
