package com.simplaex.bedrock;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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
 * @param <K> The type of the keys of this ArrayMap. Must implement Comparable. Does not allow for null values.
 * @param <V> The type of the values of this ArrayMap. Can be anything. Allows for null values.
 */
@Immutable
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class ArrayMap<K extends Comparable<? super K>, V> implements Mapping<K, V> {

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
  public ArrayMap<K, V> filter(final Predicate<K> predicate) {
    Objects.requireNonNull(predicate, "'predicate' must not be null");
    return stream().filter(entry -> predicate.test(entry.getKey())).collect(collector());
  }

  @Nonnull
  public ArrayMap<K, V> filterWithValue(final BiPredicate<K, V> predicate) {
    Objects.requireNonNull(predicate, "'predicate' must not be null");
    return stream().filter(entry -> predicate.test(entry.getKey(), entry.getValue())).collect(collector());
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
  public ArrayMap<K, V> union(@Nonnull final ArrayMap<K, V> arrayMap) {
    final int targetSize = size() + arrayMap.size();
    final Object[] keys = new Object[targetSize];
    final Object[] values = new Object[targetSize];
    final Seq<K> is = keys();
    final Seq<K> js = arrayMap.keys();
    int ix = 0;
    int jx = 0;
    int kx = 0;
    while (ix < is.size() && jx < js.size()) {
      final K i = is.apply(ix);
      final K j = js.apply(jx);
      final int c = i.compareTo(j);
      if (c < 0) {
        keys[kx] = i;
        values[kx] = apply(i);
        ix += 1;
      } else if (c == 0) {
        keys[kx] = i;
        values[kx] = apply(i);
        ix += 1;
        jx += 1;
      } else {
        keys[kx] = j;
        values[kx] = arrayMap.apply(j);
        jx += 1;
      }
      kx += 1;
    }
    while (ix < is.size()) {
      final K i = is.get(ix);
      keys[kx] = i;
      values[kx] = apply(i);
      ix += 1;
      kx += 1;
    }
    while (jx < js.size()) {
      final K j = js.get(jx);
      keys[kx] = j;
      values[kx] = arrayMap.apply(j);
      jx += 1;
      kx += 1;
    }
    final Object[] finalKeys = new Object[kx];
    final Object[] finalValues = new Object[kx];
    System.arraycopy(keys, 0, finalKeys, 0, kx);
    System.arraycopy(values, 0, finalValues, 0, kx);
    return new ArrayMap<>(finalKeys, finalValues);
  }

  @Nonnull
  public ArrayMap<K, V> intersect(@Nonnull final ArrayMap<K, V> arrayMap) {
    final int targetSize = Math.max(size(), arrayMap.size());
    final Object[] keys = new Object[targetSize];
    final Object[] values = new Object[targetSize];
    final Seq<K> is = keys();
    final Seq<K> js = arrayMap.keys();
    int ix = 0;
    int jx = 0;
    int kx = 0;
    while (ix < is.size() && jx < js.size()) {
      final K i = is.apply(ix);
      final K j = js.apply(jx);
      final int c = i.compareTo(j);
      if (c < 0) {
        ix += 1;
      } else if (c == 0) {
        keys[kx] = i;
        values[kx] = apply(i);
        ix += 1;
        jx += 1;
        kx += 1;
      } else {
        jx += 1;
      }
    }
    final Object[] finalKeys = new Object[kx];
    final Object[] finalValues = new Object[kx];
    System.arraycopy(keys, 0, finalKeys, 0, kx);
    System.arraycopy(values, 0, finalValues, 0, kx);
    return new ArrayMap<>(finalKeys, finalValues);
  }

  public String toString() {
    return stream()
      .map(entry -> String.format("%s = %s", entry.fst(), entry.snd()))
      .collect(Collectors.joining(", ", "{ ", " }"));
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
  public static <K extends Comparable<? super K>, V> ArrayMap<K, V> ofMap(@Nonnull final Map<K, V> pairs) {

    if (pairs instanceof TreeMap) {
      return ofTreeMap((TreeMap<K, V>) pairs);
    }

    final Object[] keys = Seq.ofCollectionInternal(pairs.keySet()).sortedInternal().backingArray;
    final Object[] values = new Object[pairs.size()];

    for (int i = 0; i < keys.length; i += 1) {
      //noinspection SuspiciousMethodCalls
      values[i] = pairs.get(keys[i]);
    }
    return new ArrayMap<>(keys, values);
  }

  @SuppressWarnings("WeakerAccess")
  @Nonnull
  public static <K extends Comparable<? super K>, V> ArrayMap<K, V> ofTreeMap(@Nonnull final TreeMap<K, V> pairs) {

    final Object[] keys = Seq.ofCollectionInternal(pairs.keySet()).backingArray;
    final Object[] values = new Object[pairs.size()];

    for (int i = 0; i < keys.length; i += 1) {
      //noinspection SuspiciousMethodCalls
      values[i] = pairs.get(keys[i]);
    }
    return new ArrayMap<>(keys, values);
  }

  @SuppressWarnings("unchecked")
  public static <K extends Comparable<? super K>, V> ArrayMap<K, V> empty() {
    return (ArrayMap<K, V>) EMPTY;
  }

  @SuppressWarnings("WeakerAccess")
  public static <K extends Comparable<? super K>, V> Collector<Pair<K, V>, TreeMap<K, V>, ArrayMap<K, V>> collector() {
    return new Collector<Pair<K, V>, TreeMap<K, V>, ArrayMap<K, V>>() {
      @Override
      public Supplier<TreeMap<K, V>> supplier() {
        return TreeMap::new;
      }

      @Override
      public BiConsumer<TreeMap<K, V>, Pair<K, V>> accumulator() {
        return (treeMap, entry) -> treeMap.put(entry.getKey(), entry.getValue());
      }

      @Override
      public BinaryOperator<TreeMap<K, V>> combiner() {
        return (map1, map2) -> {
          map1.putAll(map2);
          return map1;
        };
      }

      @Override
      public Function<TreeMap<K, V>, ArrayMap<K, V>> finisher() {
        return ArrayMap::ofMap;
      }

      @Override
      public java.util.Set<Characteristics> characteristics() {
        return Collections.emptySet();
      }
    };
  }

  public static <K extends Comparable<? super K>, V> ArrayMapBuilder<K, V> builder() {
    return new ArrayMapBuilder<>();
  }
}
