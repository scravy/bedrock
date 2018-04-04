package com.simplaex.bedrock;

import lombok.experimental.UtilityClass;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * Graph algorithms.
 */
@UtilityClass
public class Graphs {

  @Nonnull
  public static <V> Optional<Seq<V>> topologicalSort(final Seq<Pair<V, V>> edges) {
    final SeqBuilder<V> resultBuilder = Seq.builder();
    final HashMap<V, Set<V>> incomingEdgesMap = new HashMap<>();
    final HashMap<V, Set<V>> outgoingEdgesMap = new HashMap<>();
    edges.forEach(edge -> {
      incomingEdgesMap.computeIfAbsent(edge.getFirst(), __ -> new HashSet<>());
      incomingEdgesMap.computeIfAbsent(edge.getSecond(), __ -> new HashSet<>());
      outgoingEdgesMap.computeIfAbsent(edge.getFirst(), __ -> new HashSet<>());
      outgoingEdgesMap.computeIfAbsent(edge.getSecond(), __ -> new HashSet<>());
    });
    edges.forEach(edge -> incomingEdgesMap.get(edge.getSecond()).add(edge.getFirst()));
    edges.forEach(edge -> outgoingEdgesMap.get(edge.getFirst()).add(edge.getSecond()));
    int count = outgoingEdgesMap.size();
    final Deque<V> nodes = new ArrayDeque<V>();
    incomingEdgesMap.forEach((to, from) -> {
      if (from.isEmpty()) {
        nodes.addFirst(to);
      }
    });
    while (!nodes.isEmpty()) {
      final V n = nodes.removeFirst();
      resultBuilder.add(n);
      outgoingEdgesMap.get(n).forEach(m -> {
        final Set<V> incoming = incomingEdgesMap.get(m);
        incoming.remove(n);
        if (incoming.isEmpty()) {
          nodes.addLast(m);
        }
      });
      count -= 1;
    }
    return count == 0 ? Optional.of(resultBuilder.build()) : Optional.empty();
  }

}
