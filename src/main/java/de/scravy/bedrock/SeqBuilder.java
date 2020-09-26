package de.scravy.bedrock;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A builder for immutable sequences. Can not be instantiated directly, use Seq.builder
 *
 * @param <E> The type of the elements the builder accepts.
 */
public final class SeqBuilder<E> extends AbstractBuilder<E, Seq<E>, SeqBuilder<E>> {

  private final ArrayList<E> arrayList;

  public SeqBuilder() {
    this.arrayList = new ArrayList<>();
  }

  public SeqBuilder(final int sizeHint) {
    this.arrayList = new ArrayList<>(sizeHint);
  }

  @Nonnull
  @Override
  public SeqBuilder<E> add(final E elem) {
    arrayList.add(elem);
    return this;
  }

  @Nonnull
  @Override
  public Seq<E> result() {
    if (arrayList.isEmpty()) {
      return Seq.empty();
    }
    final Object[] array = arrayList.toArray(new Object[0]);
    return new SeqSimple<>(array);
  }

  @Nonnull
  <F extends Comparable<? super F>> Seq<F> resultSortedInternal() {
    if (arrayList.isEmpty()) {
      return Seq.empty();
    }
    final Object[] array = arrayList.toArray(new Object[0]);
    return new SeqSimpleSorted<>(array);
  }

  public boolean isEmpty() {
    return arrayList.isEmpty();
  }

  public boolean nonEmpty() {
    return !isEmpty();
  }

  @Nonnegative
  public int size() {
    return arrayList.size();
  }

  @Nonnull
  public SeqBuilder<E> clear() {
    arrayList.clear();
    return this;
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
