package com.simplaex.bedrock;

import javax.annotation.Nonnull;
import java.lang.ref.SoftReference;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A Mapping from keys to values. A Mapping is very much like {@link java.util.Map}, but it precludes mutable operations.
 * <p>
 * Every mapping is a function from keys to values that does know about the keys it maps. Hence a minimal
 * complete definition of a Mapping consists of {@link #get(Object)} and {@link #keys()}.
 *
 * @param <From> The type of the keys.
 * @param <To>   The type of the values.
 */
public interface Mapping<From, To> extends Function1<From, To>, Iterable<Pair<From, To>> {

  /**
   * Retrieves the value associated with the given key or Optional.empty() if the key is not mapped to any value.
   *
   * @param key The key.
   * @return The value which the given key is mapped to.
   */
  @Nonnull
  Optional<To> get(From key);

  Seq<From> keys();

  default To getOrElse(final From key, final To fallback) {
    return get(key).orElse(fallback);
  }

  @Override
  default To apply(final From key) {
    final Optional<To> result = get(key);
    if (result.isPresent()) {
      return result.get();
    } else {
      throw new NoSuchElementException(Objects.toString(key));
    }
  }

  default int size() {
    return keys().length();
  }

  default boolean isEmpty() {
    return keys().isEmpty();
  }

  default Seq<To> values() {
    final SeqBuilder<To> builder = Seq.builder();
    for (final From key : keys()) {
      builder.add(apply(key));
    }
    return builder.result();
  }

  @Override
  @Nonnull
  default Iterator<Pair<From, To>> iterator() {
    return new Iterator<Pair<From, To>>() {
      private final Iterator<From> underlying = keys().iterator();

      @Override
      public boolean hasNext() {
        return underlying.hasNext();
      }

      @Override
      public Pair<From, To> next() {
        final From nextKey = underlying.next();
        return Pair.of(nextKey, apply(nextKey));
      }
    };
  }

  default Stream<Pair<From, To>> stream() {
    return StreamSupport.stream(spliterator(), false);
  }

  default Map<From, To> toMap() {
    return new AbstractMap<From, To>() {

      private SoftReference<java.util.Set<Entry<From, To>>> entrySet = new SoftReference<>(null);

      @Override
      @Nonnull
      public java.util.Set<Entry<From, To>> entrySet() {
        final java.util.Set<Entry<From, To>> entrySet = this.entrySet.get();
        if (entrySet == null) {
          final HashSet<Entry<From, To>> set = new HashSet<>();
          for (final From key : keys()) {
            set.add(Pair.of(key, apply(key)));
          }
          this.entrySet = new SoftReference<>(set);
          return set;
        }
        return entrySet;
      }
    };
  }

  default void forEach(final BiConsumer<? super From, ? super To> action) {
    Objects.requireNonNull(action, "'action' must not be null");
    for (final Pair<From, To> t : this) {
      action.accept(t.fst(), t.snd());
    }
  }

  static <From, To> Mapping<From, To> wrap(final Map<From, To> map) {
    return new Mapping<From, To>() {

      @Override
      @Nonnull
      public Map<From, To> toMap() {
        return map;
      }

      @Override
      @Nonnull
      public Iterator<Pair<From, To>> iterator() {
        return new Iterator<Pair<From, To>>() {

          private final Iterator<Map.Entry<From, To>> underlying = map.entrySet().iterator();

          @Override
          public boolean hasNext() {
            return underlying.hasNext();
          }

          @Override
          public Pair<From, To> next() {
            return Pair.of(underlying.next());
          }
        };
      }

      private SoftReference<Seq<From>> keys = new SoftReference<>(null);
      private SoftReference<Seq<To>> values = new SoftReference<>(null);

      @Nonnull
      @Override
      public Optional<To> get(final From key) {
        if (map.containsKey(key)) {
          return Optional.ofNullable(map.get(key));
        }
        return Optional.empty();
      }

      @Override
      public Seq<From> keys() {
        final Seq<From> keys = this.keys.get();
        if (keys == null) {
          final Seq<From> newKeys = Seq.ofCollection(map.keySet());
          this.keys = new SoftReference<>(newKeys);
          return newKeys;
        }
        return keys;
      }

      @Override
      public Seq<To> values() {
        final Seq<To> values = this.values.get();
        if (values == null) {
          final Seq<To> newValues = Seq.ofCollection(map.values());
          this.values = new SoftReference<>(newValues);
          return newValues;
        }
        return values;
      }
    };
  }

  @SuppressWarnings("unchecked")
  static <K, V> Mapping<K, V> empty() {
    return (Mapping<K, V>) EmptyMapping.EMPTY;
  }

  class EmptyMapping<K, V> implements Mapping<K, V> {

    private static EmptyMapping EMPTY = new EmptyMapping();

    @Nonnull
    @Override
    public Optional<V> get(final K key) {
      return Optional.empty();
    }

    @Override
    public Seq<K> keys() {
      return Seq.empty();
    }
  }

}
