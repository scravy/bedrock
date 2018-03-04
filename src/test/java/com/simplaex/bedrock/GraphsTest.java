package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import lombok.val;
import org.junit.runner.RunWith;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;
import static com.simplaex.bedrock.Pair.pair;

@SuppressWarnings({"CodeBlock2Expr", "ClassInitializerMayBeStatic"})
@RunWith(Spectrum.class)
public class GraphsTest {

  {
    describe("topologicalSort", () -> {
      it("should topo sort", () -> {
        val maybeResult = Graphs.topologicalSort(Seq.of(
          pair("a", "c"),
          pair("b", "c"),
          pair("c", "d"),
          pair("c", "e")
        ));
        expect(maybeResult.isPresent()).toBeTrue();
        @SuppressWarnings("ConstantConditions") val result = maybeResult.get();
        expect(result.find("b") < result.find("c")).toBeTrue();
        expect(result.find("a") < result.find("c")).toBeTrue();
        expect(result.find("c") < result.find("d")).toBeTrue();
        expect(result.find("c") < result.find("e")).toBeTrue();
      });
      it("should topo sort a huge graph", () -> {
        val maybeResult = Graphs.topologicalSort(Seq.of(
          pair("c", "d"),
          pair("c", "e"),
          pair("b", "c"),
          pair("a", "c"),
          pair("b", "a"),
          pair("c", "f"),
          pair("c", "g"),
          pair("c", "h")
        ));
        expect(maybeResult.isPresent()).toBeTrue();
        @SuppressWarnings("ConstantConditions") val result = maybeResult.get();
        expect(result.find("c") < result.find("d")).toBeTrue();
        expect(result.find("c") < result.find("e")).toBeTrue();
        expect(result.find("b") < result.find("c")).toBeTrue();
        expect(result.find("a") < result.find("c")).toBeTrue();
        expect(result.find("b") < result.find("a")).toBeTrue();
        expect(result.find("c") < result.find("f")).toBeTrue();
        expect(result.find("c") < result.find("g")).toBeTrue();
        expect(result.find("c") < result.find("h")).toBeTrue();
      });
      it("should topo sort another huge graph", () -> {
        val edges = Seq.of(
          pair("a", "f"),
          pair("c", "f"),
          pair("e", "q"),
          pair("p", "q"),
          pair("m", "p"),
          pair("n", "p"),
          pair("a", "d"),
          pair("b", "d"),
          pair("b", "e"),
          pair("c", "e")
        );
        val maybeResult = Graphs.topologicalSort(edges);
        expect(maybeResult.isPresent()).toBeTrue();
        @SuppressWarnings("ConstantConditions") val result = maybeResult.get();
        edges.forEach(edge -> expect(result.find(edge.getFirst()) < result.find(edge.getSecond())).toBeTrue());
      });
      it("should detect cycle", () -> {
        val maybeResult = Graphs.topologicalSort(Seq.of(
          pair("c", "d"),
          pair("c", "e"),
          pair("b", "c"),
          pair("a", "c"),
          pair("b", "a"),
          pair("c", "f"),
          pair("c", "g"),
          pair("c", "h"),
          pair("a", "b"),
          pair("b", "c"),
          pair("c", "a")
        ));
        expect(maybeResult.isPresent()).toBeFalse();
      });
      it("should detect cycle huge graph", () -> {
        val maybeResult = Graphs.topologicalSort(Seq.of(
          pair("a", "b"),
          pair("b", "c"),
          pair("c", "a")
        ));
        expect(maybeResult.isPresent()).toBeFalse();
      });
    });
  }
}
