package com.simplaex.bedrock;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.AbstractQueue;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class RingBuffer<E> extends AbstractQueue<E> implements ExtendedIterable<E>, Container<E> {

  @Nonnull
  private final E[] underlying;

  @Nonnegative
  private int upper = 0;

  @Nonnegative
  private int lower = 0;

  @Nonnegative
  private int size = 0;

  @SuppressWarnings("unchecked")
  public RingBuffer(@Nonnegative final int capacity) {
    this.underlying = (E[]) new Object[capacity];
  }

  @Nonnull
  @Override
  public Stream<E> stream() {
    return StreamSupport.stream(Spliterators.spliterator(iterator(), size(), 0), false);
  }

  @Override
  @Nonnull
  public Iterator<E> iterator() {
    return new Iterator<E>() {

      private int startedLower = lower;
      private int startedUpper = upper;
      private int current = 0;
      private int endAt = size();

      @Override
      public boolean hasNext() {
        return current < endAt;
      }

      @Override
      public E next() {
        if (lower != startedLower || upper != startedUpper) {
          throw new ConcurrentModificationException();
        }
        if (!hasNext()) {
          throw new EmptyIteratorException();
        }
        final E e = underlying[(startedLower + current) % capacity()];
        current += 1;
        return e;
      }
    };
  }

  @Override
  @Nonnegative
  public int size() {
    return size;
  }

  @Nonnegative
  public int capacity() {
    return underlying.length;
  }

  @Override
  public boolean offer(final E e) {
    if (!(size() < capacity())) {
      return false;
    }
    underlying[upper] = e;
    size += 1;
    upper = next(upper);
    return true;
  }

  @Override
  public E poll() {
    if (isEmpty()) {
      return null;
    }
    final E e = underlying[lower];
    size -= 1;
    lower = next(lower);
    return e;
  }

  @Override
  public E peek() {
    if (isEmpty()) {
      return null;
    }
    return underlying[lower];
  }

  @Nonnegative
  private int next(@Nonnegative final int current) {
    return (current + 1) % capacity();
  }

}
