package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import lombok.val;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;
import static com.simplaex.bedrock.Pair.pair;
import static com.simplaex.bedrock.hlist.HList.hlist;

@SuppressWarnings({"CodeBlock2Expr", "ClassInitializerMayBeStatic"})
@RunWith(Spectrum.class)
public class PairTest {

  {
    describe("Entry methods", () -> {
      it("should throw an UnsupportedOperationException on mutating methods", () -> {
        expect(() -> pair(1, 2).setValue(3)).toThrow(UnsupportedOperationException.class);
      });
      it("should not create a new object if the passed Entry is actually a pair", () -> {
        final Pair<Integer, Integer> p1 = pair(10, 15);
        // that cast selects the right method
        @SuppressWarnings("RedundantCast") final Pair<Integer, Integer> p2 = Pair.of((Map.Entry<Integer, Integer>) p1);
        expect(p1 == p2).toBeTrue();
      });
    });
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
      it("should have size 2", () -> {
        expect(Pair.toList(Pair.of(1, 1.2)).size()).toEqual(2);
      });
    });
    describe("toList", () -> {
      it("should turn a pair into a list", () -> {
        final List<Object> list = Pair.of(1, 1.2).toList();
        expect(list.get(0)).toEqual(1);
        expect(list.get(1)).toEqual(1.2);
      });
      it("should have size 2", () -> {
        expect(Pair.of(1, 1.2).toList().size()).toEqual(2);
      });
      it("should throw an IndexOutOfBoundsException when accessed at any index other than 0 or 1", () -> {
        //noinspection ResultOfMethodCallIgnored
        expect(() -> pair(10, 11).toList().get(2)).toThrow(IndexOutOfBoundsException.class);
      });
    });
    describe("swapped", () -> {
      it("should swap", () -> {
        expect(Pair.of(1, 2).swapped()).toEqual(Pair.of(2, 1));
      });
    });
    describe("Withers", () -> {
      it("withFirst", () -> {
        expect(Pair.of(1, 2).withFirst(0)).toEqual(Pair.of(0, 2));
      });
      it("withSecond", () -> {
        expect(Pair.of(1, 2).withSecond(0)).toEqual(Pair.of(1, 0));
      });
    });
    describe("map", () -> {
      it("should apply mapping function on each", () -> {
        expect(Pair.of(1, 2).map(Operators.plus((Integer) 7), Operators.plus((Integer) 12))).toEqual(Pair.of(8, 14));
      });
      it("should apply mapping function on first element in mapFirst", () -> {
        expect(pair(1, 2).mapFirst(Operators.plus((Integer) 5))).toEqual(pair(6, 2));
      });
      it("should apply mapping function on second element in mapSecond", () -> {
        expect(pair(1, 2).mapSecond(Operators.plus((Integer) 5))).toEqual(pair(1, 7));
      });
    });
    describe("HList conversion", () -> {
      it("should convert to an HList", () -> {
        expect(pair(1, 2).toHList()).toEqual(hlist(1, 2));
      });
      it("should convert from an HList", () -> {
        expect(Pair.fromHList(hlist(1, 2))).toEqual(pair(1, 2));
      });
    });
  }
}
