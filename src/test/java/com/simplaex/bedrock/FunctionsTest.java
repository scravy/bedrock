package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import org.junit.runner.RunWith;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@SuppressWarnings("ClassInitializerMayBeStatic")
@RunWith(Spectrum.class)
public class FunctionsTest {
  {
    describe("Functions", () -> {
      it("intConstant should return the given argument", () -> {
        expect(Functions.intConstant("x").apply(3)).toEqual("x");
      });
      it("longConstant should return the given argument", () -> {
        expect(Functions.longConstant("x").apply(3)).toEqual("x");
      });
      it("doubleConstant should return the given argument", () -> {
        expect(Functions.doubleConstant("x").apply(3)).toEqual("x");
      });
      it("constantInt should return the given argument", () -> {
        expect(Functions.<String>constantInt(9).applyAsInt("x")).toEqual(9);
      });
      it("constantLong should return the given argument", () -> {
        expect(Functions.<String>constantLong(9).applyAsLong("x")).toEqual(9);
      });
      it("constantDouble should return the given argument", () -> {
        expect(Functions.<String>constantDouble(9).applyAsDouble("x")).toEqual(9);
      });
      describe("and", () -> {
        it("should apply predicates combined using and", () -> {
          expect(
            Functions.<String>and(
              str -> str.contains("a"),
              str -> str.contains("b"),
              str -> str.contains("c")
            ).test("abc")
          ).toBeTrue();
          expect(
            Functions.<String>and(
              str -> str.contains("a"),
              str -> str.contains("d")
            ).test("abc")
          ).toBeFalse();
        });
      });
    });
    describe("Functions2", () -> {
      final Function2<String, Integer, String> repeat = (str, times) -> {
        final StringBuilder b = new StringBuilder();
        for (int i = 0; i < times; i += 1) {
          b.append(str);
        }
        return b.toString();
      };
      it("should flip the arguments to a function", () -> {
        expect(repeat.flipped().apply(3, "ab")).toEqual("ababab");
      });
      it("should return the original function when flipping twice", () -> {
        expect(repeat == repeat.flipped().flipped()).toBeTrue();
      });
    });
  }
}
