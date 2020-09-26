package de.scravy.bedrock;

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
    final HashMap<V, java.util.Set<V>> incomingEdgesMap = new HashMap<>();
    final HashMap<V, java.util.Set<V>> outgoingEdgesMap = new HashMap<>();
    edges.forEach(edge -> {
      incomingEdgesMap.computeIfAbsent(edge.fst(), __ -> new HashSet<>());
      incomingEdgesMap.computeIfAbsent(edge.snd(), __ -> new HashSet<>());
      outgoingEdgesMap.computeIfAbsent(edge.fst(), __ -> new HashSet<>());
      outgoingEdgesMap.computeIfAbsent(edge.snd(), __ -> new HashSet<>());
    });
    edges.forEach(edge -> incomingEdgesMap.get(edge.getSecond()).add(edge.getFirst()));
    edges.forEach(edge -> outgoingEdgesMap.get(edge.getFirst()).add(edge.getSecond()));
    int count = outgoingEdgesMap.size();
    final Deque<V> nodes = new ArrayDeque<>();
    incomingEdgesMap.forEach((to, from) -> {
      if (from.isEmpty()) {
        nodes.addFirst(to);
      }
    });
    while (!nodes.isEmpty()) {
      final V n = nodes.removeFirst();
      resultBuilder.add(n);
      outgoingEdgesMap.get(n).forEach(m -> {
        final java.util.Set<V> incoming = incomingEdgesMap.get(m);
        incoming.remove(n);
        if (incoming.isEmpty()) {
          nodes.addLast(m);
        }
      });
      count -= 1;
    }
    return count == 0 ? Optional.of(resultBuilder.build()) : Optional.empty();
  }

  @Nonnull
  public static <V> Seq<Seq<V>> stronglyConnectedComponents(@Nonnull final Seq<Pair<V, V>> edges) {
    final DirectedGraph<V> graph = DirectedGraph.fromEdges(edges);
    return stronglyConnectedComponents(graph);
  }

  @Nonnull
  public static <V> Seq<Seq<V>> stronglyConnectedComponents(@Nonnull final DirectedGraph<V> graph) {
    @SuppressWarnings("WeakerAccess")
    class Algo {

      final int[] lowlink = new int[graph.getNumberOfVertices()];
      final boolean[] onStack = new boolean[graph.getNumberOfVertices()];
      final int[] indices = new int[graph.getNumberOfVertices()];

      {
        Arrays.fill(indices, -1);
      }

      int index = 0;

      final Deque<Integer> stack = new ArrayDeque<>();
      final SeqBuilder<Seq<V>> result = Seq.builder();

      void strongConnect(final int v) {
        lowlink[v] = index;
        indices[v] = index;
        index += 1;
        stack.push(v);
        onStack[v] = true;

        graph.forEachOutgoing(v, w -> {
          if (indices[w] == -1) {
            strongConnect(w);
            lowlink[v] = Math.min(lowlink[v], lowlink[w]);
          } else if (onStack[w]) {
            lowlink[v] = Math.min(lowlink[v], indices[w]);
          }
        });

        if (lowlink[v] == indices[v]) {
          final SeqBuilder<V> sccBuilder = Seq.builder();
          int x;
          do {
            x = stack.pop();
            onStack[x] = false;
            sccBuilder.add(graph.vertex(x));
          } while (x != v);
          result.add(sccBuilder.build());
        }
      }

      Seq<Seq<V>> result() {
        return result.result();
      }
    }
    final Algo algo = new Algo();
    for (int v = 0; v < graph.getNumberOfVertices(); v += 1) {
      if (algo.indices[v] == -1) {
        algo.strongConnect(v);
      }
    }
    return algo.result();
  }

}
