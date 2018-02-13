package com.simplaex.bedrock;

import lombok.experimental.UtilityClass;
import lombok.val;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class Graphs {

  public static <V> Seq<V> topologicalSort(final Seq<Pair<V, V>> edges) {
    val result = Seq.<V>builder();
    val incomingEdgesMap = new HashMap<V, Set<V>>();
    val outgoingEdgesMap = new HashMap<V, Set<V>>();
    edges.forEach(edge -> {
      incomingEdgesMap.computeIfAbsent(edge.getFst(), __ -> new HashSet<>());
      incomingEdgesMap.computeIfAbsent(edge.getSnd(), __ -> new HashSet<>());
      outgoingEdgesMap.computeIfAbsent(edge.getFst(), __ -> new HashSet<>());
      outgoingEdgesMap.computeIfAbsent(edge.getSnd(), __ -> new HashSet<>());
    });
    edges.forEach(edge -> incomingEdgesMap.get(edge.getSnd()).add(edge.getFst()));
    edges.forEach(edge -> outgoingEdgesMap.get(edge.getFst()).add(edge.getSnd()));
    val nodes = new ArrayDeque<V>();
    incomingEdgesMap.forEach((to, from) -> {
      if (from.isEmpty()) {
        nodes.push(to);
      }
    });
    while (!nodes.isEmpty()) {
      val n = nodes.pop();
      result.add(n);
      outgoingEdgesMap.get(n).forEach(m -> {
        val incoming = incomingEdgesMap.get(m);
        incoming.remove(n);
        if (incoming.isEmpty()) {
          nodes.push(m);
        }
      });
      outgoingEdgesMap.remove(n);
    }
    if (outgoingEdgesMap.isEmpty()) {
      return result.build();
    } else {
      return null;
    }
  }

}
