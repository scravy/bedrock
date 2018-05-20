package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import lombok.val;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@SuppressWarnings("ClassInitializerMayBeStatic")
@RunWith(Spectrum.class)
public class QuadrupleTest {

  {
    describe("Comparable", () -> {
      it("should be sortable", () -> {

        val sorted = Seq.of(
          Quadruple.of(0, 0, 0, 0),
          Quadruple.of(0, 0, 1, 0),
          Quadruple.of(0, 1, 0, 0),
          Quadruple.of(1, 0, 0, 0),
          Quadruple.of(0, 0, 1, 1),
          Quadruple.of(0, 1, 1, 0),
          Quadruple.of(1, 1, 0, 0),
          Quadruple.of(1, 0, 1, 0),
          Quadruple.of(0, 1, 0, 1),
          Quadruple.of(1, 0, 0, 1),
          Quadruple.of(0, 0, -1, 0),
          Quadruple.of(0, -1, 0, 0),
          Quadruple.of(-1, 0, 0, 0),
          Quadruple.of(0, 0, -1, -1),
          Quadruple.of(0, -1, -1, 0),
          Quadruple.of(-1, -1, 0, 0),
          Quadruple.of(-1, 0, -1, 0),
          Quadruple.of(0, -1, 0, -1),
          Quadruple.of(-1, 0, 0, -1)
        ).sorted();

        val expected = Seq.of(
          Quadruple.of(-1, -1, 0, 0),
          Quadruple.of(-1, 0, -1, 0),
          Quadruple.of(-1, 0, 0, -1),
          Quadruple.of(-1, 0, 0, 0),
          Quadruple.of(0, -1, -1, 0),
          Quadruple.of(0, -1, 0, -1),
          Quadruple.of(0, -1, 0, 0),
          Quadruple.of(0, 0, -1, -1),
          Quadruple.of(0, 0, -1, 0),
          Quadruple.of(0, 0, 0, 0),
          Quadruple.of(0, 0, 1, 0),
          Quadruple.of(0, 0, 1, 1),
          Quadruple.of(0, 1, 0, 0),
          Quadruple.of(0, 1, 0, 1),
          Quadruple.of(0, 1, 1, 0),
          Quadruple.of(1, 0, 0, 0),
          Quadruple.of(1, 0, 0, 1),
          Quadruple.of(1, 0, 1, 0),
          Quadruple.of(1, 1, 0, 0)
        );

        expect(sorted).toEqual(expected);
      });
      it("should be sortable (with null values)", () -> {

        val sorted = Seq.of(
          Quadruple.of(0, 0, 0, 0),
          Quadruple.of(0, 0, 1, 0),
          Quadruple.of(0, 1, 0, 0),
          Quadruple.of(1, 0, 0, 0),
          Quadruple.of(0, 0, 1, 1),
          Quadruple.of(0, 1, 1, 0),
          Quadruple.of(1, 1, 0, 0),
          Quadruple.of(1, 0, 1, 0),
          Quadruple.of(0, 1, 0, 1),
          Quadruple.of(1, 0, 0, 1),
          Quadruple.of(0, 0, null, 0),
          Quadruple.of(0, null, 0, 0),
          Quadruple.of(null, 0, 0, 0),
          Quadruple.of(0, 0, null, null),
          Quadruple.of(0, null, null, 0),
          Quadruple.of(null, null, 0, 0),
          Quadruple.of(null, 0, null, 0),
          Quadruple.of(0, null, 0, null),
          Quadruple.of(null, 0, 0, null)
        ).sorted();

        val expected = Seq.of(
          Quadruple.of(null, null, 0, 0),
          Quadruple.of(null, 0, null, 0),
          Quadruple.of(null, 0, 0, null),
          Quadruple.of(null, 0, 0, 0),
          Quadruple.of(0, null, null, 0),
          Quadruple.of(0, null, 0, null),
          Quadruple.of(0, null, 0, 0),
          Quadruple.of(0, 0, null, null),
          Quadruple.of(0, 0, null, 0),
          Quadruple.of(0, 0, 0, 0),
          Quadruple.of(0, 0, 1, 0),
          Quadruple.of(0, 0, 1, 1),
          Quadruple.of(0, 1, 0, 0),
          Quadruple.of(0, 1, 0, 1),
          Quadruple.of(0, 1, 1, 0),
          Quadruple.of(1, 0, 0, 0),
          Quadruple.of(1, 0, 0, 1),
          Quadruple.of(1, 0, 1, 0),
          Quadruple.of(1, 1, 0, 0)
        );

        expect(sorted).toEqual(expected);
      });
    });
    describe("Wither", () -> {
      it("withFirst", () -> {
        expect(Quadruple.of(1, 2, 3, 4).withFirst("one"))
          .toEqual(Quadruple.of("one", 2, 3, 4));
      });
      it("withSecond", () -> {
        expect(Quadruple.of(1, 2, 3, 4).withSecond("two"))
          .toEqual(Quadruple.of(1, "two", 3, 4));
      });
      it("withThird", () -> {
        expect(Quadruple.of(1, 2, 3, 4).withThird("three"))
          .toEqual(Quadruple.of(1, 2, "three", 4));
      });
      it("withFourth", () -> {
        expect(Quadruple.of(1, 2, 3, 4).withFourth("four"))
          .toEqual(Quadruple.of(1, 2, 3, "four"));
      });
    });
    describe("map", () -> {
      it("mapFirst", () -> {
        expect(Quadruple.of(1, 2, 3, 4).mapFirst(x -> "one"))
          .toEqual(Quadruple.of("one", 2, 3, 4));
      });
      it("mapSecond", () -> {
        expect(Quadruple.of(1, 2, 3, 4).mapSecond(x -> "two"))
          .toEqual(Quadruple.of(1, "two", 3, 4));
      });
      it("mapThird", () -> {
        expect(Quadruple.of(1, 2, 3, 4).mapThird(x -> "three"))
          .toEqual(Quadruple.of(1, 2, "three", 4));
      });
      it("mapFourth", () -> {
        expect(Quadruple.of(1, 2, 3, 4).mapFourth(x -> "four"))
          .toEqual(Quadruple.of(1, 2, 3, "four"));
      });
    });
    describe("toList", () -> {
      final Quadruple<Integer, Double, Long, Float> q = Quadruple.of(1, 2.0, 1L, 2f);
      final List<Object> xs = q.toList();
      final List<Number> ns = Quadruple.toList(q);
      it("should create a list with 4 elements", () -> {
        expect(xs.size()).toEqual(4);
        expect(ns.size()).toEqual(4);
      });
      it("should create a list of the given object", () -> {
        expect(xs).toEqual(Arrays.<Object>asList(1, 2.0, 1L, 2f));
        expect(ns).toEqual(Arrays.<Number>asList(1, 2.0, 1L, 2f));
      });
    });
  }

}
