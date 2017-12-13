package com.simplaex.bedrock;

import lombok.experimental.UtilityClass;
import lombok.val;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@SuppressWarnings("CodeBlock2Expr")
@UtilityClass
class SeqPropertyChecks {

  void checks(final Seq<Integer> seq) {

    describe("length + isEmpty", () -> {
      it("length() > 0 == !isEmpty()", () -> expect(seq.length() > 0 == !seq.isEmpty()).toBeTrue());
      it("length() == 0 == isEmpty()", () -> expect(seq.length() == 0 == seq.isEmpty()).toBeTrue());
    });

    describe("builder + forEach", () -> {
      it("iterating and building a new Seq should yield the same seq", () -> {
        val b = Seq.builder();
        seq.forEach(b::add);
        val s = b.result();
        expect(s).toEqual(seq);
      });
    });

    describe("shuffled", () -> {
      it("should contain the same elements as before", () -> {
        val s = seq.shuffled();
        for (val e : seq) {
          expect(s.contains(e)).toBeTrue();
        }
      });
      it("should contain the same elements the same number of times as before", () -> {
        val s = seq.shuffled();
        for (val e : seq) {
          expect(s.count(e)).toEqual(seq.count(e));
        }
      });
      it("the shuffled result should have the same length as before", () -> {
        expect(seq.shuffled().length()).toEqual(seq.length());
      });
    });

  }

}
