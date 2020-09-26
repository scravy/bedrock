package de.scravy.bedrock;

import com.greghaskins.spectrum.Spectrum;
import org.junit.runner.RunWith;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;
import static de.scravy.bedrock.Pair.pair;
import static de.scravy.bedrock.Seq.seq;

@SuppressWarnings({"ClassInitializerMayBeStatic", "CodeBlock2Expr"})
@RunWith(Spectrum.class)
public class DirectedGraphTest {

  {
    describe("DirectedGraph", () -> {
      it("should count the incoming and outgoing edges", () -> {
        final DirectedGraph<Integer> dg = DirectedGraph.fromEdges(seq(
          pair(1, 2),
          pair(2, 3),
          pair(3, 4),
          pair(4, 5),
          pair(5, 1)
        ));
        expect(dg.countIncomingEdges(1)).toEqual(1);
        expect(dg.countIncomingEdges(2)).toEqual(1);
        expect(dg.countIncomingEdges(3)).toEqual(1);
        expect(dg.countIncomingEdges(4)).toEqual(1);
        expect(dg.countIncomingEdges(5)).toEqual(1);
        expect(dg.countOutgoingEdges(1)).toEqual(1);
        expect(dg.countOutgoingEdges(2)).toEqual(1);
        expect(dg.countOutgoingEdges(3)).toEqual(1);
        expect(dg.countOutgoingEdges(4)).toEqual(1);
        expect(dg.countOutgoingEdges(5)).toEqual(1);
      });
      final DirectedGraph<String> dg = DirectedGraph.fromEdges(seq(
        pair("a", "b"),
        pair("b", "c")
      ));
      it("should count incoming and outgoing edges correctly", () -> {
        expect(dg.countIncomingEdges("a")).toEqual(0);
        expect(dg.countOutgoingEdges("a")).toEqual(1);
        expect(dg.hasOutgoingEdges("a")).toBeTrue();
        expect(dg.hasIncomingEdges("a")).toBeFalse();

        expect(dg.countIncomingEdges("b")).toEqual(1);
        expect(dg.countOutgoingEdges("b")).toEqual(1);
        expect(dg.hasOutgoingEdges("b")).toBeTrue();
        expect(dg.hasIncomingEdges("b")).toBeTrue();

        expect(dg.countIncomingEdges("c")).toEqual(1);
        expect(dg.countOutgoingEdges("c")).toEqual(0);
        expect(dg.hasOutgoingEdges("c")).toBeFalse();
        expect(dg.hasIncomingEdges("c")).toBeTrue();
      });
      it("should compute indexes for vertices", () -> {
        expect(Set.of(
          dg.index("a"),
          dg.index("b"),
          dg.index("c")
        )).toEqual(Set.of(0, 1, 2));

        expect(dg.vertex(dg.index("a"))).toEqual("a");
        expect(dg.vertex(dg.index("b"))).toEqual("b");
        expect(dg.vertex(dg.index("c"))).toEqual("c");
      });
      it("should enumerate all incoming edges correctly using forEachIncomingEdge", () -> {
        final SetBuilder<String> aEdges = Set.builder();
        dg.forEachIncomingEdge("a", aEdges::add);
        expect(aEdges.result()).toEqual(Set.empty());

        final SetBuilder<String> bEdges = Set.builder();
        dg.forEachIncomingEdge("b", bEdges::add);
        expect(bEdges.result()).toEqual(Set.of("a"));

        final SetBuilder<String> cEdges = Set.builder();
        dg.forEachIncomingEdge("c", cEdges::add);
        expect(cEdges.result()).toEqual(Set.of("b"));
      });
    });
  }

}
