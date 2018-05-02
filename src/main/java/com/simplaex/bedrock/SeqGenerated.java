package com.simplaex.bedrock;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.IntFunction;

class SeqGenerated<E> extends Seq<E> {

  private final IntFunction<E> backingFunction;
  private final int length;

  SeqGenerated(@Nonnull final IntFunction<E> function, @Nonnegative final int length) {
    this.backingFunction = function;
    this.length = length;
  }

  @SuppressWarnings("ConstantConditions")
  @Override
  public E get(@Nonnegative final int index) {
    if (index >= length || index < 0) {
      throw new IndexOutOfBoundsException();
    }
    return backingFunction.apply(index);
  }

  @Nonnull
  @Override
  public Seq<E> sortedBy(@Nonnull final Comparator<? super E> comparator) {
    return trimmedToSize().sortedBy(comparator);
  }

  @Nonnull
  @Override
  public E[] toArray(@Nonnull final Class<E> evidence) {
    Objects.requireNonNull(evidence);
    final int length = size();
    @SuppressWarnings("unchecked") final E[] array = (E[]) Array.newInstance(evidence, length);
    for (int i = 0; i < length; i += 1) {
      array[i] = get(i);
    }
    return array;
  }

  @Nonnull
  @Override
  public Object[] toArray() {
    final Object[] array = new Object[size()];
    for (int i = 0; i < size(); i += 1) {
      array[i] = backingFunction.apply(i);
    }
    return array;
  }

  @Override
  public int length() {
    return length;
  }

  @Nonnull
  @Override
  public Seq<E> reversed() {
    return trimmedToSize().reversed();
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  @Override
  public Seq<E> sorted() {
    return trimmedToSize().sorted();
  }

  @Nonnull
  @Override
  public Seq<E> trimmedToSize() {
    return Seq.ofArrayZeroCopyInternal(toArray());
  }

  @Nonnull
  @Override
  public Seq<E> subSequence(final int beginOffset, final int endOffset) {
    final int begin = Math.max(0, beginOffset);
    final int end = Math.min(length(), endOffset);
    final int len = end - begin;
    if (len <= 0) {
      return empty();
    }
    final Object[] array = new Object[len];
    for (int i = 0; i < len; i += 1) {
      array[i] = get(begin + i);
    }
    return Seq.ofArrayZeroCopyInternal(array);
  }

  @Nonnull
  @Override
  public Seq<E> subSequenceView(final int beginOffset, final int endOffset) {
    return subSequence(beginOffset, endOffset);
  }
}
