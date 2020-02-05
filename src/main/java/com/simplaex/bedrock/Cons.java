package com.simplaex.bedrock;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.NoSuchElementException;

@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Unstable
public class Cons<E> implements Container<E> {

  private static class Empty<E> extends Cons<E> {
    private Empty() {
      super(null, null);
    }

    @Override
    public E head() {
      throw new NoSuchElementException();
    }

    @Override
    @Nonnull
    public Cons<E> tail() {
      throw new NoSuchElementException("invoked tail() on en empty() cons");
    }

    @Override
    public boolean isEmpty() {
      return true;
    }

    @Override
    public boolean nonEmpty() {
      return false;
    }

    @Override
    public boolean equals(final Object e) {
      return e instanceof Empty;
    }

    @Nonnull
    public String toString() {
      return "(<empty>)";
    }
  }

  private final static Cons<?> EMPTY = new Empty<>();

  @SuppressWarnings("unchecked")
  @Nonnull
  public static <E> Cons<E> empty() {
    return (Cons<E>) EMPTY;
  }

  @Nonnull
  public static <E> Cons<E> singleton(final E value) {
    return new Cons<>(value, empty());
  }

  @Nonnull
  public static <E> Cons<E> cons(final E value, final Cons<E> cons) {
    return new Cons<>(value, cons);
  }

  private final E head;
  private final Cons<E> tail;

  public E head() {
    return head;
  }

  @Nonnull
  public Cons<E> tail() {
    return tail;
  }

  @Override
  public boolean isEmpty() {
    // this is correct by construction
    return false;
  }

  @AllArgsConstructor
  private static final class ConsIterator<E> implements Iterator<E> {

    @Nonnull
    private Cons<E> current;

    @Override
    public boolean hasNext() {
      return current.nonEmpty();
    }

    @Override
    public E next() {
      if (current.isEmpty()) {
        throw new EmptyIteratorException();
      }
      final E result = current.head();
      current = current.tail();
      return result;
    }
  }

  @Override
  @Nonnull
  public String toString() {
    return String.format("(%s,%s)", head(), tail());
  }

  @Override
  @Nonnull
  public Iterator<E> iterator() {
    return new ConsIterator<>(this);
  }
}
