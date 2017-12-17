package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import org.junit.runner.RunWith;

import java.util.Optional;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@SuppressWarnings({"CodeBlock2Expr", "ClassInitializerMayBeStatic"})
@RunWith(Spectrum.class)
public class SeqEmptyTest {

  {
    describe("an empty seq (property checks)", () -> {
      SeqPropertyChecks.checks(Seq.empty());
    });

    describe("toArray", () -> {
      it("should produce an empty array", () -> {
        expect(Seq.empty().toArray().length).toEqual(0);
      });
    });

    describe("sortedBy", () -> {
      it("should return an empty seq", () -> {
        expect(Seq.<Long>empty().sortedBy(Long::compare)).toEqual(Seq.empty());
      });
    });

    describe("exists", () -> {
      it("should return false", () -> {
        expect(Seq.<Integer>empty().exists(i -> i % 2 == 0)).toBeFalse();
      });
    });

    describe("count", () -> {
      it("should return zero", () -> {
        expect(Seq.<Integer>empty().count(0)).toEqual(0);
      });
    });

    describe("countBy", () -> {
      it("should return zero", () -> {
        expect(Seq.<Integer>empty().countBy(i -> i % 2 == 0)).toEqual(0);
      });
    });

    describe("headOptional", () -> {
      it("should return empty", () -> {
        expect(Seq.empty().headOptional()).toEqual(Optional.empty());
      });
    });

    describe("lastOptional", () -> {
      it("should return empty", () -> {
        expect(Seq.empty().lastOptional()).toEqual(Optional.empty());
      });
    });
  }

}
