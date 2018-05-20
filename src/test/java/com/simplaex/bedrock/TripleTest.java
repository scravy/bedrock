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
public class TripleTest {

  {
    describe("Comparable", () -> {
      it("should be sortable", () -> {

        val sorted = Seq.of(
          Triple.of(0, 0, 0),
          Triple.of(0, 0, 1),
          Triple.of(0, 1, 0),
          Triple.of(1, 0, 0),
          Triple.of(0, 0, 1),
          Triple.of(0, 1, 1),
          Triple.of(1, 1, 0),
          Triple.of(1, 0, 1),
          Triple.of(0, 1, 0),
          Triple.of(1, 0, 0),
          Triple.of(0, 0, -1),
          Triple.of(0, -1, 0),
          Triple.of(-1, 0, 0),
          Triple.of(0, 0, -1),
          Triple.of(0, -1, -1),
          Triple.of(-1, -1, 0),
          Triple.of(-1, 0, -1),
          Triple.of(0, -1, 0),
          Triple.of(-1, 0, 0)
        ).sorted();
        
        val expected = Seq.of(
          Triple.of(-1, -1, 0),
          Triple.of(-1, 0, -1),
          Triple.of(-1, 0, 0),
          Triple.of(-1, 0, 0),
          Triple.of(0, -1, -1),
          Triple.of(0, -1, 0),
          Triple.of(0, -1, 0),
          Triple.of(0, 0, -1),
          Triple.of(0, 0, -1),
          Triple.of(0, 0, 0),
          Triple.of(0, 0, 1),
          Triple.of(0, 0, 1),
          Triple.of(0, 1, 0),
          Triple.of(0, 1, 0),
          Triple.of(0, 1, 1),
          Triple.of(1, 0, 0),
          Triple.of(1, 0, 0),
          Triple.of(1, 0, 1),
          Triple.of(1, 1, 0)
        );

        expect(sorted).toEqual(expected);
      });
      it("should be sortable (with null values)", () -> {

        val sorted = Seq.of(
          Triple.of(0, 0, 0),
          Triple.of(0, 0, 1),
          Triple.of(0, 1, 0),
          Triple.of(1, 0, 0),
          Triple.of(0, 0, 1),
          Triple.of(0, 1, 1),
          Triple.of(1, 1, 0),
          Triple.of(1, 0, 1),
          Triple.of(0, 1, 0),
          Triple.of(1, 0, 0),
          Triple.of(0, 0, null),
          Triple.of(0, null, 0),
          Triple.of(null, 0, 0),
          Triple.of(0, 0, null),
          Triple.of(0, null, null),
          Triple.of(null, null, 0),
          Triple.of(null, 0, null),
          Triple.of(0, null, 0),
          Triple.of(null, 0, 0)
        ).sorted();

        val expected = Seq.of(
          Triple.of(null, null, 0),
          Triple.of(null, 0, null),
          Triple.of(null, 0, 0),
          Triple.of(null, 0, 0),
          Triple.of(0, null, null),
          Triple.of(0, null, 0),
          Triple.of(0, null, 0),
          Triple.of(0, 0, null),
          Triple.of(0, 0, null),
          Triple.of(0, 0, 0),
          Triple.of(0, 0, 1),
          Triple.of(0, 0, 1),
          Triple.of(0, 1, 0),
          Triple.of(0, 1, 0),
          Triple.of(0, 1, 1),
          Triple.of(1, 0, 0),
          Triple.of(1, 0, 0),
          Triple.of(1, 0, 1),
          Triple.of(1, 1, 0)
        );

        expect(sorted).toEqual(expected);
      });
    });
    describe("Wither", () -> {
      it("withFirst", () -> {
        expect(Triple.of(1, 2, 3).withFirst("one"))
          .toEqual(Triple.of("one", 2, 3));
      });
      it("withSecond", () -> {
        expect(Triple.of(1, 2, 3).withSecond("two"))
          .toEqual(Triple.of(1, "two", 3));
      });
      it("withThird", () -> {
        expect(Triple.of(1, 2, 3).withThird("three"))
          .toEqual(Triple.of(1, 2, "three"));
      });
    });
    describe("map", () -> {
      it("mapFirst", () -> {
        expect(Triple.of(1, 2, 3).mapFirst(x -> "one"))
          .toEqual(Triple.of("one", 2, 3));
      });
      it("mapSecond", () -> {
        expect(Triple.of(1, 2, 3).mapSecond(x -> "two"))
          .toEqual(Triple.of(1, "two", 3));
      });
      it("mapThird", () -> {
        expect(Triple.of(1, 2, 3).mapThird(x -> "three"))
          .toEqual(Triple.of(1, 2, "three"));
      });
    });
    describe("toList", () -> {
      final Triple<Integer, Double, Long> q = Triple.of(1, 2.0, 1L);
      final List<Object> xs = q.toList();
      final List<Number> ns = Triple.toList(q);
      it("should create a list with 3 elements", () -> {
        expect(xs.size()).toEqual(3);
        expect(ns.size()).toEqual(3);
      });
      it("should create a list of the given object", () -> {
        expect(xs).toEqual(Arrays.<Object>asList(1, 2.0, 1L));
        expect(ns).toEqual(Arrays.<Number>asList(1, 2.0, 1L));
      });
    });
  }

}
