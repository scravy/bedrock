package com.simplaex.bedrock;

import javax.annotation.Nonnull;
import java.util.Iterator;

final class PermutationIterator<E> implements Iterator<Seq<E>> {

  private final Seq<E> source;
  private final int[] indices;
  private final int[] directions;
  private Seq<E> upcoming;

  PermutationIterator(@Nonnull final Seq<E> source) {
    this.source = source;
    this.indices = new int[source.length()];
    this.directions = new int[source.length()];
    for (int i = 1; i < source.length(); i += 1) {
      this.indices[i] = i;
      this.directions[i] = -1;
    }
    this.upcoming = source;
  }

  private void swap(final int i, final int j) {
    int tmp = indices[i];
    indices[i] = indices[j];
    indices[j] = tmp;

    tmp = directions[i];
    directions[i] = directions[j];
    directions[j] = tmp;
  }

  private Seq<E> makeResult() {
    final Object[] array = new Object[source.length()];
    for (int i = 0; i < source.length(); i += 1) {
      array[i] = source.get(indices[i]);
    }
    return new SeqSimple<>(array);
  }

  @Override
  public boolean hasNext() {
    return upcoming != null;
  }

  private int findMaxIndex() {
    for (int i = 0; i < source.length(); i += 1) {
      if (directions[i] != 0) {
        return i;
      }
    }
    return -1;
  }

  @Nonnull
  @Override
  public Seq<E> next() {
    int maxIndex = findMaxIndex();
    final Seq<E> result = upcoming;
    if (maxIndex == -1) {
      upcoming = null;
    } else {
      for (int i = maxIndex + 1; i < source.length(); i += 1) {
        if (directions[i] != 0 && indices[i] > indices[maxIndex]) {
          maxIndex = i;
        }
      }
      final int moveTo = maxIndex + directions[maxIndex];
      swap(maxIndex, moveTo);
      if (moveTo == 0 || moveTo == (source.length() - 1) || indices[moveTo + directions[moveTo]] > indices[moveTo]) {
        directions[moveTo] = 0;
      }
      for (int i = 0; i < source.length(); i += 1) {
        if (indices[i] > indices[moveTo]) {
          if (i < moveTo) {
            directions[i] = 1;
          } else {
            directions[i] = -1;
          }
        }
      }
      upcoming = makeResult();
    }
    return result;
  }
}
