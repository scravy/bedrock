package com.simplaex.bedrock;

import lombok.experimental.UtilityClass;

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

    describe("drop", () -> {
      it("should return a new seq with the first element dropped", () -> expect(seq.drop(1)).toEqual(Seq.of(2, 2, 4, 3)));
      it("should return a new seq with the first two elements dropped", () -> expect(seq.drop(2)).toEqual(Seq.of(2, 4, 3)));
      it("should return a new seq with the first three dropped", () -> expect(seq.drop(3)).toEqual(Seq.of(4, 3)));
      it("should return a new seq with the first four elements dropped", () -> expect(seq.drop(4)).toEqual(Seq.of(3)));
      it("should return the empty seq when all five elements dropped", () -> expect(seq.drop(5)).toEqual(Seq.empty()));
      it("should return an empty seq even when dropped more than five", () -> expect(seq.drop(6)).toEqual(Seq.empty()));
    });

  }

}
