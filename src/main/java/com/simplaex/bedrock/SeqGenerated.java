package com.simplaex.bedrock;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.function.IntFunction;

class SeqGenerated<E> extends Seq<E> {

  private final IntFunction<E> backingFunction;
  private final int length;
  private SeqSimple<E> materializedSeq = null;

  SeqGenerated(@Nonnull final IntFunction<E> function, @Nonnegative final int length) {
    this.backingFunction = function;
    this.length = length;
  }

  private void materialize() {
    if (materializedSeq == null) {
      final Object[] array = new Object[length];
      for (int i = 0; i < length; i += 1) {
        array[i] = backingFunction.apply(i);
      }
      materializedSeq = Seq.ofArrayZeroCopyInternal(array);
    }
  }

  @Override
  public E get(@Nonnegative final int index) {
    if (index >= length) {
      throw new IndexOutOfBoundsException();
    }
    return backingFunction.apply(index);
  }

  @Nonnull
  @Override
  public Seq<E> sortedBy(@Nonnull final Comparator<? super E> comparator) {
    materialize();
    return materializedSeq.sortedBy(comparator);
  }

  @Nonnull
  @Override
  public E[] toArray(@Nonnull final Class<E> clazz) {
    materialize();
    return materializedSeq.toArray(clazz);
  }

  @Nonnull
  @Override
  public Object[] toArray() {
    materialize();
    return materializedSeq.toArray();
  }

  @Override
  public int length() {
    return length;
  }

  @Nonnull
  @Override
  public Seq<E> reversed() {
    materialize();
    return materializedSeq.reversed();
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  @Override
  public Seq<E> sorted() {
    materialize();
    return materializedSeq.sortedInternal();
  }

  @Nonnull
  @Override
  public Seq<E> trimmedToSize() {
    materialize();
    return materializedSeq.trimmedToSize();
  }

  @Nonnull
  @Override
  public Seq<E> subSequence(final int beginOffset, final int endOffset) {
    materialize();
    return materializedSeq.subSequence(beginOffset, endOffset);
  }

  @Nonnull
  @Override
  public Seq<E> subSequenceView(final int beginOffset, final int endOffset) {
    materialize();
    return materializedSeq.subSequenceView(beginOffset, endOffset);
  }
}
