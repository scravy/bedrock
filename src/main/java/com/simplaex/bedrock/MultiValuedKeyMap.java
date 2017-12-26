package com.simplaex.bedrock;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.Arrays;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Supplier;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MultiValuedKeyMap<T> implements Function<Seq<Object>, T> {

  private final Node<T> rootNode;

  public <K> Optional<T> get(final Seq<K> key) {
    return Optional.ofNullable(find(rootNode, (Seq<Object>) key));
  }

  public <K> T get(final Seq<K> key, final T fallback) {
    final T result = find(rootNode, (Seq<Object>) key);
    if (result == null) {
      return fallback;
    }
    return result;
  }

  public <K> T get(final Seq<K> key, final Supplier<T> fallbackSupplier) {
    final T result = find(rootNode, (Seq<Object>) key);
    if (result == null) {
      return fallbackSupplier.get();
    }
    return result;
  }

  @Override
  public T apply(final Seq<Object> key) {
    return find(rootNode, key);
  }

  public static <T> Builder<T> builder() {
    return new Builder<>();
  }

  public static class Builder<T> {

    private BuilderNode<T> root = new BuilderNode<>();

    @SuppressWarnings("unchecked")
    public <K> Builder<T> add(final Seq<K> key, final T value) {
      add(root, value, (Seq<Object>) key);
      return this;
    }

    public MultiValuedKeyMap<T> build() {
      return new MultiValuedKeyMap<T>(build(root));
    }

    private static <T> void add(final BuilderNode<T> node, final T value, final Seq<Object> key) {
      if (key.isEmpty()) {
        node.value = value;
      } else if (key.head() == null) {
        if (node.fallback == null) {
          node.fallback = new BuilderNode<T>();
        }
        add(node.fallback, value, key.tail());
      } else {
        if (node.children == null) {
          node.children = new TreeMap<>();
        }
        BuilderNode<T> child = node.children.get(key.head());
        if (child == null) {
          child = new BuilderNode<T>();
          node.children.put(key.head(), child);
        }
        add(child, value, key.tail());
      }
    }

    private static <T> Node<T> build(final BuilderNode<T> node) {
      if (node == null) {
        return null;
      }
      final Object[] keys;
      final Node<T>[] children;
      if (node.children != null) {
        keys = node.children.keySet().toArray();
        children = (Node<T>[]) Seq.of(keys).map(key -> build(node.children.get(key))).toArray((Class<Node<T>>) (Object) Node.class);
      } else {
        keys = null;
        children = null;
      }
      return new Node<>(
        node.value,
        build(node.fallback),
        keys,
        children
      );
    }

  }

  private static class BuilderNode<T> {

    T value = null;

    BuilderNode<T> fallback = null;

    TreeMap<Object, BuilderNode<T>> children = new TreeMap<>();

  }

  @Value
  private static class Node<T> {

    final T value;

    final Node<T> fallback;

    final Object[] keys;

    final Node<T>[] children;

  }

  private static <T> T find(final Node<T> node, final Seq<Object> key) {

    Node<T> currentNode = node;
    Node<T> nextNode = null;

    int i = 0;
    for (; i < key.length(); i += 1) {
      if (currentNode.keys == null) {
        if (currentNode.fallback == null) {
          break;
        } else {
          nextNode = currentNode.fallback;
        }
      } else {
        final int ix = key.get(i) == null ? -1 : Arrays.binarySearch(currentNode.keys, key.get(i));
        nextNode = ix >= 0 ? currentNode.children[ix] : currentNode.fallback;
        if (nextNode == null) {
          break;
        }
      }
      currentNode = nextNode;
    }
    return currentNode.value;
  }

}
