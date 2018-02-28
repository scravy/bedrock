package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import lombok.val;
import org.junit.runner.RunWith;

import java.util.List;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;
import static com.simplaex.bedrock.Pair.pair;

@SuppressWarnings({"CodeBlock2Expr", "ClassInitializerMayBeStatic"})
@RunWith(Spectrum.class)
public class PairTest {

  {
    describe("compareTo", () -> {
      it("should compare pairs of null values", () -> {
        val p1 = Pair.of(null, null);
        val p2 = Pair.of(null, null);
        expect(p1.compareTo(p2)).toEqual(0);
      });
      it("should lexicographically compare pairs", () -> {
        val p1 = Pair.of(1, 2);
        val p2 = Pair.of(1, 1);
        expect(p1.compareTo(p2)).toEqual(1);
      });
      it("should lexicographically compare pairs (the other way around)", () -> {
        val p1 = Pair.of(1, 1);
        val p2 = Pair.of(1, 2);
        expect(p1.compareTo(p2)).toEqual(-1);
      });
      it("should lexicographically compare pairs (first component)", () -> {
        val p1 = Pair.of(1, 2);
        val p2 = Pair.of(2, 1);
        expect(p1.compareTo(p2)).toEqual(-1);
      });
      it("should lexicographically compare pairs (first component, the other way around)", () -> {
        val p1 = Pair.of(2, 1);
        val p2 = Pair.of(1, 2);
        expect(p1.compareTo(p2)).toEqual(1);
      });
      it("should sort a sequence", () -> {
        val s = Seq.<Pair<Integer, Integer>>of(
          pair(2, 1),
          pair(1, 2),
          pair(null, null),
          pair(null, 1),
          pair(null, 1),
          pair(null, 2),
          pair(1, 2),
          pair(null, 1),
          pair(null, 1),
          pair(null, null),
          pair(2, 2),
          pair(1, 1),
          pair(2, 1)
        );
        val r = s.sorted();
        expect(r).toEqual(Seq.of(
          pair(null, null),
          pair(null, null),
          pair(null, 1),
          pair(null, 1),
          pair(null, 1),
          pair(null, 1),
          pair(null, 2),
          pair(1, 1),
          pair(1, 2),
          pair(1, 2),
          pair(2, 1),
          pair(2, 1),
          pair(2, 2)
        ));
      });
    });
    describe("static toList", () -> {
      it("should turn a pair into a list", () -> {
        final List<Number> list = Pair.toList(Pair.of(1, 1.2));
        expect(list.get(0)).toEqual(1);
        expect(list.get(1)).toEqual(1.2);
      });
    });
    describe("toList", () -> {
      it("should turn a pair into a list", () -> {
        final List<Object> list = Pair.of(1, 1.2).toList();
        expect(list.get(0)).toEqual(1);
        expect(list.get(1)).toEqual(1.2);
      });
    });
  }
}
