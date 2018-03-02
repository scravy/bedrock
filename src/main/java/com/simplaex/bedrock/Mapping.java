package com.simplaex.bedrock;

import lombok.val;

import javax.annotation.Nonnull;
import java.lang.ref.SoftReference;
import java.util.*;
import java.util.function.Function;

public interface Mapping<From, To> extends Function<From, To>, Iterable<Pair<From, To>> {

  @Nonnull
  Optional<To> get(From key);

  @Override
  default To apply(final From key) {
    val result = get(key);
    if (result.isPresent()) {
      return result.get();
    } else {
      throw new NoSuchElementException(Objects.toString(key));
    }
  }

  Seq<From> keys();

  default Seq<To> values() {
    val builder = Seq.<To>builder();
    for (val key : keys()) {
      builder.add(apply(key));
    }
    return builder.result();
  }

  default Map<From, To> toMap() {
    return new AbstractMap<From, To>() {

      private SoftReference<Set<Entry<From, To>>> entrySet = new SoftReference<>(null);

      @Override
      @Nonnull
      public Set<Entry<From, To>> entrySet() {
        val entrySet = this.entrySet.get();
        if (entrySet == null) {
          val set = new HashSet<Entry<From, To>>();
          for (val key : keys()) {
            set.add(Pair.of(key, apply(key)));
          }
          this.entrySet = new SoftReference<>(set);
          return set;
        }
        return entrySet;
      }
    };
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
        val keys = this.keys.get();
        if (keys == null) {
          val newKeys = Seq.ofCollection(map.keySet());
          this.keys = new SoftReference<>(newKeys);
          return newKeys;
        }
        return keys;
      }

      @Override
      public Seq<To> values() {
        val values = this.values.get();
        if (values == null) {
          val newValues = Seq.ofCollection(map.values());
          this.values = new SoftReference<>(newValues);
          return newValues;
        }
        return values;
      }
    };
  }

}
