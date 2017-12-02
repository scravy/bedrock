package com.simplaex.bedrock;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class SeqBuilder<E> implements Iterable<E> {

  private final ArrayList<E> arrayList = new ArrayList<>();

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
  public SeqBuilder<E> addIterable(final Iterable<E> elems) {
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
