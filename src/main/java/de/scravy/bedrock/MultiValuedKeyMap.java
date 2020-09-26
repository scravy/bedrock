package de.scravy.bedrock;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MultiValuedKeyMap<T> implements Function1<Seq<Object>, T> {

  private final Node<T> rootNode;

  @SuppressWarnings("unchecked")
  public <K> Optional<T> get(final Seq<K> key) {
    return Optional.ofNullable(find(rootNode, (Seq<Object>) key, null));
  }

  public <K> T get(final Seq<K> key, final T fallback) {
    return get(key, fallback, null);
  }

  public <K> T get(final Seq<K> key, final Supplier<T> fallbackSupplier) {
    return get(key, fallbackSupplier, null);
  }

  /**
   * Lookup a value in the map and trace the path taken to find it.
   *
   * @param key      The key to look for.
   * @param fallback The fallback value to use in case there is no value for the given key.
   * @param trace    A mutable list to trace the path taken into.
   * @param <K>      The type of values in the key.
   * @return The found value or the fallback value.
   */
  @SuppressWarnings("unchecked")
  public <K> T get(final Seq<K> key, @Nullable final T fallback, final List<Object> trace) {
    final T result = find(rootNode, (Seq<Object>) key, trace);
    if (result == null) {
      return fallback;
    }
    return result;
  }

  /**
   * Lookup a value in the map and trace the path taken to find it.
   *
   * @param key              The key to look for.
   * @param fallbackSupplier A supplier to generate a fallback value in case there is no value for the given key.
   * @param trace            A mutable list to trace the path taken into.
   * @param <K>              The type of values in the key.
   * @return The found value or the fallback value.
   */
  @SuppressWarnings("unchecked")
  public <K> T get(final Seq<K> key, @Nonnull final Supplier<T> fallbackSupplier, final List<Object> trace) {
    Objects.requireNonNull(fallbackSupplier);
    final T result = find(rootNode, (Seq<Object>) key, trace);
    if (result == null) {
      return fallbackSupplier.get();
    }
    return result;
  }

  @Override
  public T apply(final Seq<Object> key) {
    return find(rootNode, key, null);
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
      return new MultiValuedKeyMap<>(build(root));
    }

    private static <T> void add(final BuilderNode<T> node, final T value, final Seq<Object> key) {
      if (key.isEmpty()) {
        node.value = value;
      } else if (key.head() == null) {
        if (node.fallback == null) {
          node.fallback = new BuilderNode<>();
        }
        add(node.fallback, value, key.tail());
      } else {
        if (node.children == null) {
          node.children = new TreeMap<>();
        }
        BuilderNode<T> child = node.children.get(key.head());
        if (child == null) {
          child = new BuilderNode<>();
          node.children.put(key.head(), child);
        }
        add(child, value, key.tail());
      }
    }

    @SuppressWarnings("unchecked")
    private static <T> Node<T> build(final BuilderNode<T> node) {
      if (node == null) {
        return null;
      }
      final Object[] keys;
      final Node<T>[] children;
      if (node.children != null) {
        keys = node.children.keySet().toArray();
        children = Seq.of(keys).map(key -> build(node.children.get(key))).toArray((Class<Node<T>>) (Object) Node.class);
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

  private static <T> T find(final Node<T> node, final Seq<Object> key, @Nullable final List<Object> trace) {

    Node<T> currentNode = node;
    Node<T> nextNode;

    final Consumer<Object> tracer = trace == null ? NoOp.consumer() : trace::add;

    int i = 0;
    for (; i < key.length(); i += 1) {
      if (trace != null) {
        trace.add(key.get(i));
      }
      if (currentNode.keys == null) {
        if (currentNode.fallback == null) {
          break;
        } else {
          tracer.accept(null);
          nextNode = currentNode.fallback;
        }
      } else {
        final Object k = key.get(i);
        final int ix = k == null ? -1 : Arrays.binarySearch(currentNode.keys, k);
        if (ix >= 0) {
          tracer.accept(k);
          nextNode = currentNode.children[ix];
        } else {
          tracer.accept(null);
          nextNode = currentNode.fallback;
        }
        if (nextNode == null) {
          break;
        }
      }
      currentNode = nextNode;
    }
    return currentNode.value;
  }

}
