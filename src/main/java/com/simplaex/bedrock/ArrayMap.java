package com.simplaex.bedrock;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A very simple immutable map that is backed by two arrays
 * (one for the keys, one for the values). Lookups are performed by performing
 * binary searches on the key array - thus the keys need to implement the
 * Comparable interface.
 * <p>
 * Some operations are implemented quite space efficiently. For instance
 * both mapValues and mapValuesWithKey share the key array with the source
 * ArrayMap.
 *
 * @param <K>
 * @param <V>
 */
@Immutable
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class ArrayMap<K, V> implements Mapping<K, V> {

  private static final ArrayMap EMPTY;

  static {
    final Object[] empty = new Object[0];
    EMPTY = new ArrayMap(empty, empty);
  }

  private final Object[] keys;
  private final Object[] values;

  @SuppressWarnings("unchecked")
  @Nonnull
  @Override
  public Optional<V> get(final K key) {
    final int ix = Arrays.binarySearch(keys, key);
    if (ix >= 0) {
      return Optional.ofNullable((V) values[ix]);
    } else {
      return Optional.empty();
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
  @Override
  public Seq<K> keys() {
    return new SeqSimple<>(keys);
  }

  @Nonnull
  @Override
  public Seq<V> values() {
    return new SeqSimple<>(values);
  }

  @Nonnull
  @SafeVarargs
  public static <K extends Comparable<K>, V> ArrayMap<K, V> of(@Nonnull final Pair<K, V>... pairs) {

    final Object[] keys = new Object[pairs.length];
    final Object[] values = new Object[pairs.length];

    final Pair<K, V>[] sorted = pairs.clone();
    Arrays.sort(sorted, Comparator.comparing(Pair::fst));

    for (int i = 0; i < sorted.length; i += 1) {
      keys[i] = sorted[i].fst();
      values[i] = sorted[i].snd();
    }
    return new ArrayMap<>(keys, values);
  }

  @Nonnull
  public static <K extends Comparable<K>, V> ArrayMap<K, V> ofSeq(@Nonnull final Seq<Pair<K, V>> pairs) {

    final Object[] keys = new Object[pairs.length()];
    final Object[] values = new Object[pairs.length()];

    final Seq<Pair<K, V>> sorted = pairs.sortedBy(Comparator.comparing(Pair::fst));

    for (int i = 0; i < sorted.length(); i += 1) {
      keys[i] = sorted.get(i).getFirst();
      values[i] = sorted.get(i).getSecond();
    }
    return new ArrayMap<>(keys, values);
  }

  @Nonnull
  public static <K extends Comparable<K>, V> ArrayMap<K, V> ofMap(@Nonnull final Map<K, V> pairs) {

    if (pairs instanceof TreeMap) {
      return ofMap((TreeMap<K, V>) pairs);
    }

    final Object[] keys = Seq.ofCollection(pairs.keySet()).sorted().backingArray;
    final Object[] values = new Object[pairs.size()];

    for (int i = 0; i < keys.length; i += 1) {
      //noinspection SuspiciousMethodCalls
      values[i] = pairs.get(keys[i]);
    }
    return new ArrayMap<>(keys, values);
  }

  @Nonnull
  public static <K extends Comparable<K>, V> ArrayMap<K, V> ofMap(@Nonnull final TreeMap<K, V> pairs) {

    final Object[] keys = Seq.ofCollection(pairs.keySet()).backingArray;
    final Object[] values = new Object[pairs.size()];

    for (int i = 0; i < keys.length; i += 1) {
      //noinspection SuspiciousMethodCalls
      values[i] = pairs.get(keys[i]);
    }
    return new ArrayMap<>(keys, values);
  }

  @SuppressWarnings("unchecked")
  public static <K, V> ArrayMap<K, V> empty() {
    return (ArrayMap<K, V>) EMPTY;
  }
}
