package com.simplaex.bedrock;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A builder for immutable sequences. Can not be instantiated directly, use Seq.builder
 *
 * @param <E> The type of the elements the builder accepts.
 */
public final class SeqBuilder<E> implements Iterable<E> {

  private final ArrayList<E> arrayList;

  SeqBuilder() {
    this.arrayList = new ArrayList<>();
  }

  SeqBuilder(final int sizeHint) {
    this.arrayList = new ArrayList<>(sizeHint);
  }

  @Nonnull
  public SeqBuilder<E> add(final E elem) {
    arrayList.add(elem);
    return this;
  }

  @SafeVarargs
  @Nonnull
  public final SeqBuilder<E> addAll(final E... elems) {
    for (final E elem : elems) {
      add(elem);
    }
    return this;
  }

  @Nonnull
  public SeqBuilder<E> addElements(final Iterable<? extends E> elems) {
    for (final E elem : elems) {
      add(elem);
    }
    return this;
  }

  @Nonnull
  public Seq<E> result() {
    if (arrayList.isEmpty()) {
      return Seq.empty();
    }
    final Object[] array = arrayList.toArray(new Object[arrayList.size()]);
    return new SeqSimple<>(array);
  }

  @Nonnull
  public Seq<E> build() {
    return result();
  }

  public boolean isEmpty() {
    return arrayList.isEmpty();
  }

  public int size() {
    return arrayList.size();
  }

  @Override
  public Iterator<E> iterator() {
    return new Iterator<E>() {
      private int n = arrayList.size();
      private int i = 0;

      @Override
      public boolean hasNext() {
        return i < n;
      }

      @Override
      public E next() {
        return arrayList.get(i++);
      }
    };
  }
}
