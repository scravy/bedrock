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
        val result = Graphs.topologicalSort(Seq.of(
          pair("a", "c"),
          pair("b", "c"),
          pair("c", "d"),
          pair("c", "e")
        ));
        expect(result.find("b") < result.find("c")).toBeTrue();
        expect(result.find("a") < result.find("c")).toBeTrue();
        expect(result.find("c") < result.find("d")).toBeTrue();
        expect(result.find("c") < result.find("e")).toBeTrue();
      });
      it("should topo sort a huge graph", () -> {
        val result = Graphs.topologicalSort(Seq.of(
          pair("c", "d"),
          pair("c", "e"),
          pair("b", "c"),
          pair("a", "c"),
          pair("b", "a"),
          pair("c", "f"),
          pair("c", "g"),
          pair("c", "h")
        ));
        expect(result.find("c") < result.find("d")).toBeTrue();
        expect(result.find("c") < result.find("e")).toBeTrue();
        expect(result.find("b") < result.find("c")).toBeTrue();
        expect(result.find("a") < result.find("c")).toBeTrue();
        expect(result.find("b") < result.find("a")).toBeTrue();
        expect(result.find("c") < result.find("f")).toBeTrue();
        expect(result.find("c") < result.find("g")).toBeTrue();
        expect(result.find("c") < result.find("h")).toBeTrue();
      });
      it("should detect cycle", () -> {
        val result = Graphs.topologicalSort(Seq.of(
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
        expect(result).toBeNull();
      });
      it("should detect cycle huge graph", () -> {
        val result = Graphs.topologicalSort(Seq.of(
          pair("a", "b"),
          pair("b", "c"),
          pair("c", "a")
        ));
        expect(result).toBeNull();
      });
    });
  }
}
