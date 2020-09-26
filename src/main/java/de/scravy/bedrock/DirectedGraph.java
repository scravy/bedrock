package de.scravy.bedrock;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;

import static de.scravy.bedrock.Pair.pair;

public class DirectedGraph<V> extends SimpleDirectedGraph {

  private final Object[] vertices;
  private final Map<V, Integer> verticesToIndicesMap;

  private DirectedGraph(
    final Object[] vertices,
    final Map<V, Integer> verticesToIndicesMap,
    final int numberOfEdges,
    final int[][] outgoingEdges) {

    super(vertices.length, numberOfEdges, outgoingEdges, null);

    this.vertices = vertices;
    this.verticesToIndicesMap = verticesToIndicesMap;
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  public V vertex(final int index) {
    return (V) vertices[index];
  }

  public int index(@Nonnull final V vertex) {
    return verticesToIndicesMap.getOrDefault(vertex, -1);
  }

  public Seq<V> vertices() {
    return Seq.ofArrayZeroCopyInternal(vertices);
  }

  @SuppressWarnings("unchecked")
  public void forEachOutgoingEdge(@Nonnull final V vertex, @Nonnull final Consumer<V> consumer) {
    final int index = verticesToIndicesMap.get(vertex);
    forEachOutgoing(index, to -> {
      consumer.accept((V) vertices[to]);
    });
  }

  @Nonnegative
  public int countOutgoingEdges(@Nonnull final V vertex) {
    return countOutgoing(verticesToIndicesMap.get(vertex));
  }

  public boolean hasOutgoingEdges(@Nonnull final V vertex) {
    return countOutgoingEdges(vertex) > 0;
  }

  @SuppressWarnings("unchecked")
  public void forEachIncomingEdge(@Nonnull final V vertex, @Nonnull final Consumer<V> consumer) {
    final int index = verticesToIndicesMap.get(vertex);
    forEachIncoming(index, from -> consumer.accept((V) vertices[from]));
  }

  @Nonnegative
  public int countIncomingEdges(@Nonnull final V vertex) {
    return countIncoming(verticesToIndicesMap.get(vertex));
  }

  public boolean hasIncomingEdges(@Nonnull final V vertex) {
    return countIncomingEdges(vertex) > 0;
  }

  public Seq<Pair<V, V>> edges() {
    final SeqBuilder<Pair<V, V>> edgesBuilder = Seq.builder(numberOfEdges);
    vertices().forEach(v -> forEachOutgoingEdge(v, v2 -> edgesBuilder.add(pair(v, v2))));
    return edgesBuilder.result();
  }

  @Nonnull
  public static <V> DirectedGraph<V> fromEdges(@Nonnull final Seq<Pair<V, V>> edges) {
    Objects.requireNonNull(edges, "'edges' must not be null.");
    final Map<V, Integer> verticesToIndicesMap = new HashMap<>();
    final Map<Integer, TreeSet<Integer>> outgoingEdgesMap = new HashMap<>();
    final Box.IntBox index = Box.intBox(0);
    final ToIntFunction<V> add = vertex -> {
      final int ix;
      final Integer ixMaybe = verticesToIndicesMap.get(vertex);
      if (ixMaybe == null) {
        ix = index.getValue();
        outgoingEdgesMap.put(ix, new TreeSet<>());
        verticesToIndicesMap.put(vertex, ix);
        index.inc();
      } else {
        ix = ixMaybe;
      }
      return ix;
    };
    edges.forEach(edge -> {
      final int from = add.applyAsInt(edge.fst());
      final int to = add.applyAsInt(edge.snd());
      outgoingEdgesMap.get(from).add(to);
    });
    final int numberOfVertices = index.getValue();
    final Object[] vertices = new Object[numberOfVertices];
    verticesToIndicesMap.forEach((vertex, ix) -> vertices[ix] = vertex);
    final int[][] outgoingEdges = new int[numberOfVertices][];
    final Box.IntBox numberOfEdges = Box.intBox(0);
    outgoingEdgesMap.forEach((from, tos) -> {
      final int[] out = new int[tos.size()];
      int i = 0;
      for (final int to : outgoingEdgesMap.get(from)) {
        out[i++] = to;
      }
      numberOfEdges.add(i);
      outgoingEdges[from] = out;
    });
    return new DirectedGraph<>(
      vertices,
      verticesToIndicesMap,
      numberOfEdges.getValue(),
      outgoingEdges
    );
  }

}
