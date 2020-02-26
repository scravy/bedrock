package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import com.simplaex.bedrock.hlist.C;
import com.simplaex.bedrock.hlist.HList;
import com.simplaex.bedrock.hlist.Nil;
import org.junit.runner.RunWith;

import java.util.TreeMap;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;
import static com.simplaex.bedrock.hlist.HList.hlist;
import static com.simplaex.bedrock.hlist.HList.nil;

@RunWith(Spectrum.class)
@SuppressWarnings({"ClassInitializerMayBeStatic", "ConstantConditions"})
public class SpecificityTreeTest {
  {
    describe("SpecificityTree", () -> {
      final String none = null;
      final SpecificityTree<C<String, C<String, C<String, Nil>>>, Integer> tree = SpecificityTree
        .withDimension("AppOrSite", String.class)
        .withDimension("Country", String.class)
        .withDimension("DataCentre", String.class)
        .<Integer>build()
        .add(Nil.cons("bild.de").cons("DE").cons("europe"), 1)
        .add(Nil.cons("bild.de").cons(none).cons(none), 2)
        .add(Nil.cons(none).cons("DE").cons("europe"), 3)
        .add(Nil.cons(none).cons(none).cons("europe"), 4)
        .add(Nil.cons(none).cons(none).cons(none), 5);
      it("should query the right rules", () -> {
        expect(tree.get(Nil.cons("bild.de").cons("DE").cons("europe"))).toEqual(1);
        expect(tree.get(Nil.cons("bild.de").cons("FR").cons("europe"))).toEqual(2);
        expect(tree.get(Nil.cons("bild.de").cons(none).cons("europe"))).toEqual(2);
        expect(tree.get(Nil.cons("example.org").cons("DE").cons("europe"))).toEqual(3);
        expect(tree.get(Nil.cons("example.org").cons("FR").cons("europe"))).toEqual(4);
        expect(tree.get(Nil.cons(none).cons("FR").cons("europe"))).toEqual(4);
        expect(tree.get(Nil.cons(none).cons(none).cons("europe"))).toEqual(4);
        expect(tree.get(Nil.cons("example.org").cons(none).cons("europe"))).toEqual(4);
        expect(tree.get(Nil.cons("example.org").cons("JP").cons("asia"))).toEqual(5);
        expect(tree.get(Nil.cons("example.org").cons(none).cons("asia"))).toEqual(5);
        expect(tree.get(Nil.cons("example.org").cons(none).cons(none))).toEqual(5);
      });
    });
    describe("SpecificityTree (with TreeMap underlying)", () -> {
      final String none = null;
      final SpecificityTree<C<String, C<String, C<String, Nil>>>, Integer> tree = SpecificityTree
        .withDimension("AppOrSite", String.class)
        .withDimension("Country", String.class)
        .withDimension("DataCentre", String.class)
        .build(TreeMap.class, Integer.class)
        .add(Nil.cons("bild.de").cons("DE").cons("europe"), 1)
        .add(Nil.cons("bild.de").cons(none).cons(none), 2)
        .add(Nil.cons(none).cons("DE").cons("europe"), 3)
        .add(Nil.cons(none).cons(none).cons("europe"), 4)
        .add(Nil.cons(none).cons(none).cons(none), 5);
      it("should query the right rules", () -> {
        expect(tree.get(Nil.cons("bild.de").cons("DE").cons("europe"))).toEqual(1);
        expect(tree.get(Nil.cons("bild.de").cons("FR").cons("europe"))).toEqual(2);
        expect(tree.get(Nil.cons("bild.de").cons(none).cons("europe"))).toEqual(2);
        expect(tree.get(Nil.cons("example.org").cons("DE").cons("europe"))).toEqual(3);
        expect(tree.get(Nil.cons("example.org").cons("FR").cons("europe"))).toEqual(4);
        expect(tree.get(Nil.cons(none).cons("FR").cons("europe"))).toEqual(4);
        expect(tree.get(Nil.cons(none).cons(none).cons("europe"))).toEqual(4);
        expect(tree.get(Nil.cons("example.org").cons(none).cons("europe"))).toEqual(4);
        expect(tree.get(Nil.cons("example.org").cons("JP").cons("asia"))).toEqual(5);
        expect(tree.get(Nil.cons("example.org").cons(none).cons("asia"))).toEqual(5);
        expect(tree.get(Nil.cons("example.org").cons(none).cons(none))).toEqual(5);
      });
    });
    describe("computeKeySpecificity", () -> {
      expect(SpecificityTree.computeSpecificityFor(Nil.cons("").cons(3))).toEqual(3);
      expect(SpecificityTree.computeSpecificityFor(Nil.cons("").cons(null))).toEqual(2);
      expect(SpecificityTree.computeSpecificityFor(Nil.cons(null).cons(null))).toEqual(0);
      expect(SpecificityTree.computeSpecificityFor(nil())).toEqual(0);
    });
  }
}
