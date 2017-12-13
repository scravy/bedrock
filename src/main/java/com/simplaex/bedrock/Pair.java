package com.simplaex.bedrock;

import lombok.Value;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Map;

/**
 * An immutable tuple.
 *
 * @param <A> The first component of the tuple.
 * @param <B> The second component of the tuple.
 */
@Value
public class Pair<A, B> implements Map.Entry<A, B>, Serializable, Comparable<Pair<A, B>> {

  public final A fst;
  public final B snd;

  @Override
  public A getKey() {
    return fst;
  }

  @Override
  public B getValue() {
    return snd;
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
    if (fst == null) {
      if (p.fst == null) {
        r = 0;
      } else {
        return -1;
      }
    } else if (p.fst == null) {
      return 1;
    } else {
      r = ((Comparable) fst).compareTo(p.fst);
    }
    if (r != 0) {
      return r;
    }
    if (snd == null) {
      if (p.snd == null) {
        return 0;
      } else {
        return -1;
      }
    } else if (p.snd == null) {
      return 1;
    } else {
      return ((Comparable) snd).compareTo(p.snd);
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

}
