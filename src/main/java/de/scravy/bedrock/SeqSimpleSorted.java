package de.scravy.bedrock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

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
    return nonEmpty(Seq::empty, () -> {
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
      }
      return builder.resultSortedInternal();
    });
  }

  @Nonnull
  @Override
  public Seq<E> union(final Seq<E> other) {
    return nonEmpty(other::distinct, () -> {
      if (other.isEmpty()) {
        return this.distinct();
      }
      if (!(other instanceof SeqSimpleSorted)) {
        return super.union(other);
      }
      final SeqBuilder<E> builder = Seq.builder(size() + other.size());
      final Comparator<E> comparator = Comparator.nullsFirst(Comparable::compareTo);
      int i = 0;
      int j = 0;
      E mostRecentlyAdded = comparator.compare(get(i), other.get(j)) <= 0 ? get(i++) : other.get(j++);
      builder.add(mostRecentlyAdded);
      while (i < size() && j < other.size()) {
        final E toAdd = comparator.compare(get(i), other.get(j)) <= 0 ? get(i++) : other.get(j++);
        if (!Objects.equals(mostRecentlyAdded, toAdd)) {
          mostRecentlyAdded = toAdd;
          builder.add(mostRecentlyAdded);
        }
      }
      while (i < size()) {
        final E toAdd = get(i++);
        if (!Objects.equals(mostRecentlyAdded, toAdd)) {
          mostRecentlyAdded = toAdd;
          builder.add(mostRecentlyAdded);
        }
      }
      while (j < other.size()) {
        final E toAdd = other.get(j++);
        if (!Objects.equals(mostRecentlyAdded, toAdd)) {
          mostRecentlyAdded = toAdd;
          builder.add(mostRecentlyAdded);
        }
      }
      return builder.resultSortedInternal();
    });
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
