package com.simplaex.bedrock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

class SeqSimpleSorted<E extends Comparable<? super E>> extends SeqSimple<E> {

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
  public SeqSimpleSorted<E> sorted() {
    return this;
  }

  @Nonnull
  @Override
  public Seq<E> distinct() {
    if (isEmpty()) {
      return this;
    }
    E prev = head();
    final SeqBuilder<E> builder = Seq.builder();
    for (int i = 1; i < size(); i += 1) {
      final E current = get(i);
      if (current.equals(prev)) {
        if (builder.isEmpty()) {
          builder.addElements(subSequenceView(0, i));
        }
      } else if (builder.nonEmpty()) {
        builder.add(current);
      }
      prev = current;
    }
    if (builder.isEmpty()) {
      return this;
    } else {
      return builder.resultSortedInternal();
    }
  }

  @Nonnull
  @Override
  public Seq<E> union(final Seq<E> other) {
    if (other.isEmpty()) {
      return this;
    }
    if (isEmpty()) {
      return other;
    }
    if (other instanceof SeqSimpleSorted) {
      final SeqBuilder<E> builder = Seq.builder(size() + other.size());
      final int len = Math.min(size(), other.size());
      int i = 0;
      int j = 0;
      E currentHead;
      E left = head();
      E right = other.head();
      int comparison = left.compareTo(right);

      if (comparison <= 0) {
        builder.add(left);
        i += 1;
        currentHead = left;
        if (comparison == 0) {
          j += 1;
        }
      } else {
        builder.add(right);
        j += 1;
        currentHead = right;
      }
      while (i < len && j < len) {
        left = get(i);
        right = other.get(j);
        comparison = left.compareTo(right);
        if (comparison <= 0) {
          if (!currentHead.equals(left)) {
            builder.add(left);
            currentHead = left;
          }
          i += 1;
          if (comparison == 0) {
            j += 1;
          }
        } else {
          if (!currentHead.equals(right)) {
            builder.add(right);
            currentHead = right;
          }
          j += 1;
        }
      }
      while (i < size()) {
        left = get(i);
        if (!currentHead.equals(left)) {
          builder.add(left);
          currentHead = left;
        }
        i += 1;
      }
      while (j < other.size()) {
        right = other.get(j);
        if (!currentHead.equals(right)) {
          builder.add(right);
          currentHead = right;
        }
        j += 1;
      }
      return builder.resultSortedInternal();
    } else {
      return super.union(other);
    }
  }

  @Nonnull
  @Override
  public Seq<E> without(final Seq<E> other) {
    final SeqBuilder<E> builder = Seq.builder();
    forEach(element -> {
      if (!other.contains(element)) {
        builder.add(element);
      }
    });
    return builder.resultSortedInternal();
  }

}
