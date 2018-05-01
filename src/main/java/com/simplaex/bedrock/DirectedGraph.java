package com.simplaex.bedrock;

import java.util.Map;
import java.util.function.Consumer;

public class DirectedGraph<V> extends SimpleDirectedGraph {

  private final Object[] vertices;
  private final Map<V, Integer> verticesToIndicesMap;

  DirectedGraph(
    final Object[] vertices,
    final Map<V, Integer> verticesToIndicesMap,
    final int numberOfEdges,
    final int[][] outgoingEdges) {

    super(vertices.length, numberOfEdges, outgoingEdges, null);

    this.vertices = vertices;
    this.verticesToIndicesMap = verticesToIndicesMap;
  }

  @SuppressWarnings("unchecked")
  public V vertex(final int index) {
    return (V) vertices[index];
  }

  public int index(final V vertex) {
    return verticesToIndicesMap.getOrDefault(vertex, -1);
  }

  @SuppressWarnings("unchecked")
  public void forEachOutgoing(final V vertex, final Consumer<V> consumer) {
    final int index = verticesToIndicesMap.get(vertex);
    forEachOutgoing(index, to -> {
      consumer.accept((V) vertices[to]);
    });
  }

  public int countOutgoing(final V vertex) {
    return countOutgoing(verticesToIndicesMap.get(vertex));
  }

  public boolean hasOutgoing(final V vertex) {
    return countIncoming(verticesToIndicesMap.get(vertex)) > 0;
  }

  @SuppressWarnings("unchecked")
  public void forEachIncoming(final V vertex, final Consumer<V> consumer) {
    final int index = verticesToIndicesMap.get(vertex);
    forEachIncoming(index, from -> consumer.accept((V) vertices[from]));
  }

  public int countIncoming(final V vertex) {
    return countIncoming(verticesToIndicesMap.get(vertex));
  }

  public boolean hasIncoming(final V vertex) {
    return countIncoming(verticesToIndicesMap.get(vertex)) > 0;
  }
}
