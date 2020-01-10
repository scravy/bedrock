package com.simplaex.bedrock;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A builder for immutable Sets.
 *
 * @param <E>
 */
public final class SetBuilder<E extends Comparable<? super E>>
  extends AbstractBuilder<E, Set<E>, SetBuilder<E>> {

  private final SortedSet<E> underlyingSet = new TreeSet<>();

  @Nonnull
  @Override
  public Set<E> result() {
    return Set.ofSortedSet(underlyingSet);
  }

  @Nonnull
  @Override
  public SetBuilder<E> add(final E elem) {
    underlyingSet.add(elem);
    return this;
  }

  @Nonnull
  @Override
  public Iterator<E> iterator() {
    return new Iterator<E>() {
      private final Iterator<E> it = underlyingSet.iterator();

      @Override
      public boolean hasNext() {
        return it.hasNext();
      }

      @Override
      public E next() {
        return it.next();
      }
    };
  }
}
