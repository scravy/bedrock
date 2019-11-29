package com.simplaex.bedrock;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;

public final class ArrayMapBuilder<K extends Comparable<? super K>, V>
  extends AbstractBuilder<Pair<K, V>, ArrayMap<K, V>, ArrayMapBuilder<K, V>>
  implements Iterable<Pair<K, V>> {

  private final TreeMap<K, V> underlyingMap = new TreeMap<>();

  ArrayMapBuilder() {

  }

  @Nonnull
  public ArrayMapBuilder<K, V> add(final K key, final V value) {
    underlyingMap.put(key, value);
    return this;
  }

  public ArrayMapBuilder<K, V> addKeyValue(final K key, final V value) {
    return add(key, value);
  }

  public ArrayMapBuilder<K, V> addPair(final Pair<K, V> elem) {
    return add(elem);
  }

  @Override
  public ArrayMapBuilder<K, V> add(final Pair<K, V> elem) {
    return add(elem.fst(), elem.snd());
  }

  @Nonnull
  @Override
  public ArrayMap<K, V> result() {
    if (underlyingMap.isEmpty()) {
      return ArrayMap.empty();
    }
    return ArrayMap.ofMap(underlyingMap);
  }

  public ArrayMapBuilder<K, V> clear() {
    underlyingMap.clear();
    return this;
  }

  public boolean isEmpty() {
    return underlyingMap.isEmpty();
  }

  public void forEach(final BiConsumer<K, V> consumer) {
    forEach(pair -> consumer.accept(pair.fst(), pair.snd()));
  }

  @Override
  public Iterator<Pair<K, V>> iterator() {
    return new Iterator<Pair<K, V>>() {

      private final Iterator<Map.Entry<K, V>> underlyingIterator = underlyingMap.entrySet().iterator();

      @Override
      public boolean hasNext() {
        return underlyingIterator.hasNext();
      }

      @Override
      public Pair<K, V> next() {
        final Map.Entry<K, V> entry = underlyingIterator.next();
        return Pair.of(entry.getKey(), entry.getValue());
      }
    };
  }
}
