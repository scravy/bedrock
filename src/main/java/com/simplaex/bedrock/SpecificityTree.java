package com.simplaex.bedrock;

import com.simplaex.bedrock.hlist.C;
import com.simplaex.bedrock.hlist.HList;
import com.simplaex.bedrock.hlist.Nil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;
import java.util.*;

public class SpecificityTree<K extends HList<K>, V> extends AbstractMap<K, V> implements Container<Pair<K, V>> {

  @Nonnull
  private final Map<K, V> items;

  @SuppressWarnings("unchecked")
  @SneakyThrows
  private SpecificityTree(final Class<?> underlyingMap) {
    Objects.requireNonNull(underlyingMap, "'underlyingMap' must not be null");
    this.items = (Map<K, V>) underlyingMap.newInstance();
  }

  private SpecificityTree() {
    this(HashMap.class);
  }

  @Nonnull
  @Override
  public Iterator<Pair<K, V>> iterator() {
    return entrySet().stream().map(Pair::of).iterator();
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder<L extends HList<L>> {
    public <T> Builder<C<T, L>> withDimension(
      @SuppressWarnings("unused") final String name,
      @SuppressWarnings("unused") final Class<T> clazz
    ) {
      return new Builder<>();
    }

    @Nonnull
    public <V> SpecificityTree<L, V> build() {
      return new SpecificityTree<>();
    }

    @Nonnull
    public <V, M extends Map<?, ?>> SpecificityTree<L, V> build(@Nonnull final Class<M> underlyingMap) {
      return new SpecificityTree<>(underlyingMap);
    }

    @Nonnull
    public <V, M extends Map<?, ?>> SpecificityTree<L, V> build(
      @Nonnull final Class<M> underlyingMap,
      @SuppressWarnings("unused") final Class<V> valueType
    ) {
      return build(underlyingMap);
    }
  }

  @Nonnull
  public static <T> Builder<C<T, Nil>> withDimension(
    @SuppressWarnings("unused") @Nonnull final String name,
    @SuppressWarnings("unused") @Nonnull final Class<T> clazz
  ) {
    return new Builder<>();
  }

  public SpecificityTree<K, V> add(@Nonnull final K key, @Nonnull final V value) {
    items.put(key, value);
    return this;
  }

  @Override
  public V put(@Nonnull final K key, @Nonnull final V value) {
    return items.put(key, value);
  }

  @Nonnull
  @Override
  public Set<Entry<K, V>> entrySet() {
    return items.entrySet();
  }

  public static <K extends HList<K>> long computeSpecificityFor(final K key) {
    if (key.isEmpty()) {
      return 0;
    }
    return key.foldr((thing, spec) -> (spec << 1) + (thing == null ? 0 : 1), 0L);
  }

  @Nullable
  public V get(final K key) {
    final long keySpecificity = computeSpecificityFor(key);
    for (long specificity = keySpecificity; specificity >= 0; specificity -= 1L) {
      if ((keySpecificity & specificity) == specificity) {
        final long s = specificity;
        final K k = key.mask((ix, obj) -> (s & (1L << ix)) != 0);
        final V value = items.get(k);
        if (value != null) {
          return value;
        }
      }
    }
    return null;
  }
}
