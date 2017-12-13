package com.simplaex.bedrock;

import lombok.experimental.UtilityClass;

import java.util.function.Consumer;
import java.util.function.IntFunction;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@SuppressWarnings("CodeBlock2Expr")
@UtilityClass
class SeqChecks {

  /**
   * Expects a sequence that resembles (1, 2, 2, 4, 3)
   *
   * @param seq An implementation of a sequence, containing (1, 2, 2, 4, 3)
   */
  void checks(final Seq<Integer> seq) {

    describe("length", () -> {
      it("should be 5", () -> expect(seq.length()).toEqual(5));
    });

    describe("get", () -> {
      it("should return 1 for index 0", () -> expect(seq.get(0)).toEqual(1));
      it("should return 2 for index 1", () -> expect(seq.get(1)).toEqual(2));
      it("should return 2 for index 2", () -> expect(seq.get(2)).toEqual(2));
      it("should return 4 for index 3", () -> expect(seq.get(3)).toEqual(4));
      it("should return 3 for index 4", () -> expect(seq.get(4)).toEqual(3));
      it("should throw for index 5", () -> expect(() -> seq.get(5)).toThrow(IndexOutOfBoundsException.class));
    });

    describe("equal", () -> {
      it("should equal (1, 2, 2, 4, 3)", () -> expect(seq).toEqual(Seq.of(1, 2, 2, 4, 3)));
    });

    describe("trimmedToSize", () -> {
      it("should equal itself trimmedToSize", () -> expect(seq.trimmedToSize()).toEqual(seq));
    });

    describe("count", () -> {
      it("should count 0 zero times", () -> expect(seq.count(0)).toEqual(0));
      it("should count 1 once", () -> expect(seq.count(1)).toEqual(1));
      it("should count 2 twice", () -> expect(seq.count(2)).toEqual(2));
      it("should count 3 once", () -> expect(seq.count(3)).toEqual(1));
      it("should count 4 once", () -> expect(seq.count(4)).toEqual(1));
      it("should count 5 zero times", () -> expect(seq.count(5)).toEqual(0));
    });

    describe("countBy", () -> {
      it("should count 3 elements divisble by 2", () -> expect(seq.countBy(i -> i % 2 == 0)).toEqual(3));
    });

    final Consumer<IntFunction<Seq<Integer>>> dropChecks = f -> {
      it("should return a new seq with the first element dropped", () -> expect(f.apply(1)).toEqual(Seq.of(2, 2, 4, 3)));
      it("should return a new seq with the first two elements dropped", () -> expect(f.apply(2)).toEqual(Seq.of(2, 4, 3)));
      it("should return a new seq with the first three dropped", () -> expect(f.apply(3)).toEqual(Seq.of(4, 3)));
      it("should return a new seq with the first four elements dropped", () -> expect(f.apply(4)).toEqual(Seq.of(3)));
      it("should return the empty seq when all five elements dropped", () -> expect(f.apply(5)).toEqual(Seq.empty()));
      it("should return an empty seq even when dropped more than five", () -> expect(f.apply(6)).toEqual(Seq.empty()));
    };
    describe("drop", () -> dropChecks.accept(seq::drop));
    describe("dropView", () -> dropChecks.accept(seq::dropView));

    final Consumer<IntFunction<Seq<Integer>>> takeChecks = f -> {
      it("should return an empty seq when taking zero elements", () -> expect(f.apply(0)).toEqual(Seq.empty()));
      it("should take one element", () -> expect(f.apply(1)).toEqual(Seq.of(1)));
      it("should take two elements", () -> expect(f.apply(2)).toEqual(Seq.of(1, 2)));
      it("should take three elements", () -> expect(f.apply(3)).toEqual(Seq.of(1, 2, 2)));
      it("should take four elements", () -> expect(f.apply(4)).toEqual(Seq.of(1, 2, 2, 4)));
      it("should take five elements", () -> expect(f.apply(5)).toEqual(Seq.of(1, 2, 2, 4, 3)));
      it("should take just five elements if there are no more", () -> expect(f.apply(6)).toEqual(Seq.of(1, 2, 2, 4, 3)));
    };
    describe("take", () -> takeChecks.accept(seq::take));
    describe("takeView", () -> takeChecks.accept(seq::takeView));

  }

}
