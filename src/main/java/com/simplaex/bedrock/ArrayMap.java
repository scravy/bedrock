package com.simplaex.bedrock;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * An ArrayMap is a very simple immutable map that is backed by two arrays
 * (one for the keys, one for the values). Lookups are performed by performing
 * binary searches on the key array - thus the keys need to implement the
 * Comparable interface.
 *
 * Some operations are implemented quite space efficiently. For instance
 * both mapValues and mapValuesWithKey share the key array with the source
 * ArrayMap.
 */
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ArrayMap<K, V> implements Function<K, V>, Iterable<Pair<K, V>> {

  private final Object[] keys;
  private final Object[] values;

  @SuppressWarnings("unchecked")
  public Optional<V> get(final K key) {
    final int ix = Arrays.binarySearch(keys, key);
    if (ix >= 0) {
      return Optional.ofNullable((V) values[ix]);
    } else {
      return Optional.empty();
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public V apply(final K key) {
    final int ix = Arrays.binarySearch(keys, key);
    if (ix >= 0) {
      return (V) values[ix];
    } else {
      throw new NoSuchElementException();
    }
  }

  @Nonnull
  @SuppressWarnings("unchecked")
  public <W> ArrayMap<K, W> mapValues(@Nonnull final Function<V, W> f) {
    final Object[] vs = new Object[values.length];
    for (int i = 0; i < values.length; i += 1) {
      vs[i] = f.apply((V) values[i]);
    }
    return new ArrayMap<>(keys, vs);
  }

  @Nonnull
  @SuppressWarnings("unchecked")
  public <W> ArrayMap<K, W> mapValuesWithKey(@Nonnull final BiFunction<K, V, W> f) {
    final Object[] vs = new Object[values.length];
    for (int i = 0; i < values.length; i += 1) {
      vs[i] = f.apply((K) keys[i], (V) values[i]);
    }
    return new ArrayMap<>(keys, vs);
  }

  @Nonnull
  @Override
  @SuppressWarnings("unchecked")
  public Iterator<Pair<K, V>> iterator() {
    return new Iterator<Pair<K, V>>() {
      private int i = 0;

      @Override
      @Nonnull
      public Pair<K, V> next() {
        return Pair.of((K) keys[i], (V) values[i++]);
      }

      @Override
      public boolean hasNext() {
        return i < keys.length;
      }
    };
  }

  @Nonnull
  public Seq<K> keys() {
    return new SeqSimple<K>(keys);
  }

  @Nonnull
  public Seq<V> values() {
    return new SeqSimple<V>(values);
  }

  @Nonnull
  @SafeVarargs
  public static <K extends Comparable<K>, V> ArrayMap<K, V> of(@Nonnull final Pair<K, V>... pairs) {

    final Object[] keys = new Object[pairs.length];
    final Object[] values = new Object[pairs.length];

    final Pair<K, V>[] sorted = pairs.clone();
    Arrays.sort(sorted, Comparator.comparing(p -> p.fst));

    for (int i = 0; i < sorted.length; i += 1) {
      keys[i] = sorted[i].fst;
      values[i] = sorted[i].snd;
    }
    return new ArrayMap<K, V>(keys, values);
  }
}
