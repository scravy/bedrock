package com.simplaex.bedrock;

import lombok.Value;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.List;
import java.util.Map;

/**
 * An immutable tuple.
 *
 * @param <A> The first component of the tuple.
 * @param <B> The second component of the tuple.
 */
@Value
public class Pair<A, B> implements Map.Entry<A, B>, Serializable, Comparable<Pair<A, B>> {

  public final A first;
  public final B second;

  public A fst() {
    return first;
  }

  public B snd() {
    return second;
  }

  @Override
  public A getKey() {
    return first;
  }

  @Override
  public B getValue() {
    return second;
  }

  /**
   * Since tuples are immutable this method will throw an UnsupportedOperationException.
   * It is only defined to implement the Map.Entry interface.
   *
   * @param value No matter what you throw in, this method is not supported.
   * @return Nothing, it throws an UnsupportedOperationException.
   * @throws UnsupportedOperationException Thrown always.
   */
  @Override
  public B setValue(final B value) throws UnsupportedOperationException {
    throw new UnsupportedOperationException();
  }

  @SuppressWarnings("unchecked")
  @Override
  public int compareTo(@Nonnull final Pair<A, B> p) {
    final int r;
    if (first == null) {
      if (p.first == null) {
        r = 0;
      } else {
        return -1;
      }
    } else if (p.first == null) {
      return 1;
    } else {
      r = ((Comparable) first).compareTo(p.first);
    }
    if (r != 0) {
      return r;
    }
    if (second == null) {
      if (p.second == null) {
        return 0;
      } else {
        return -1;
      }
    } else if (p.second == null) {
      return 1;
    } else {
      return ((Comparable) second).compareTo(p.second);
    }
  }

  @Nonnull
  public static <A, B> Pair<A, B> pair(final A a, final B b) {
    return new Pair<>(a, b);
  }

  @Nonnull
  public static <A, B> Pair<A, B> of(final A a, final B b) {
    return new Pair<>(a, b);
  }

  public static <C, A extends C, B extends C> List<C> toList(final Pair<A, B> pair) {
    return new AbstractList<C>() {
      @Override
      public C get(final int index) {
        switch (index) {
          case 0:
            return pair.fst();
          case 1:
            return pair.snd();
        }
        return null;
      }

      @Override
      public int size() {
        return 2;
      }
    };
  }

  public List<Object> toList() {
    return new AbstractList<Object>() {
      @Override
      public Object get(final int index) {
        switch (index) {
          case 0:
            return first;
          case 1:
            return second;
        }
        return null;
      }

      @Override
      public int size() {
        return 2;
      }
    };
  }
}
