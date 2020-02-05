package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import org.junit.runner.RunWith;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@SuppressWarnings({"ClassInitializerMayBeStatic", "CodeBlock2Expr"})
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

    describe("Function interfaces", () -> {
      final Box.IntBox invocations = Box.intBox(0);
      final Function0<String> underlyingComputation = () -> {
        invocations.inc();
        return "expensive";
      };
      final Function0<String> f0 = underlyingComputation::get;
      final Function1<String, String> f1 =
        a -> String.format("one(%s)", a);
      final Function2<String, String, String> f2 =
        (a, b) -> String.format("two(%s,%s)", a, b);
      final Function3<String, String, String, String> f3 =
        (a, b, c) -> String.format("three(%s,%s,%s)", a, b, c);
      final Function4<String, String, String, String, String> f4 =
        (a, b, c, d) -> String.format("four(%s,%s,%s,%s)", a, b, c, d);

      describe("Function0", () -> {
        it("should not invoke the underlying computation more than once when memoizing", () -> {
          final Function0<String> fm = f0.memoizing();
          expect(invocations.getValue()).toEqual(0);
          expect(fm.get()).toEqual("expensive");
          expect(invocations.getValue()).toEqual(1);
          expect(fm.get()).toEqual("expensive");
          expect(invocations.getValue()).toEqual(1);
        });
      });
      describe("Function1", () -> {
        it("bind", () -> {
          expect(f1.bind("alpha").get()).toEqual("one(alpha)");
        });
      });
      describe("Function2", () -> {
        it("bind", () -> {
          expect(f2.bind("alpha").bind("bravo").get()).toEqual("two(alpha,bravo)");
          expect(f2.bind("alpha", "bravo").get()).toEqual("two(alpha,bravo)");
        });
      });
      describe("Function3", () -> {
        it("bind", () -> {
          expect(f3.bind("alpha").bind("bravo").bind("charlie").get()).toEqual("three(alpha,bravo,charlie)");
          expect(f3.bind("alpha", "bravo").bind("charlie").get()).toEqual("three(alpha,bravo,charlie)");
          expect(f3.bind("alpha", "bravo", "charlie").get()).toEqual("three(alpha,bravo,charlie)");
          expect(f3.bind("alpha", "bravo", "charlie").get()).toEqual("three(alpha,bravo,charlie)");
        });
      });
      describe("Function4", () -> {
        it("bind", () -> {
          expect(f4.bind("alpha").bind("bravo").bind("charlie").bind("delta").get()).toEqual("four(alpha,bravo,charlie,delta)");
          expect(f4.bind("alpha", "bravo").bind("charlie").bind("delta").get()).toEqual("four(alpha,bravo,charlie,delta)");
          expect(f4.bind("alpha").bind("bravo", "charlie").bind("delta").get()).toEqual("four(alpha,bravo,charlie,delta)");
          expect(f4.bind("alpha").bind("bravo").bind("charlie", "delta").get()).toEqual("four(alpha,bravo,charlie,delta)");
          expect(f4.bind("alpha", "bravo", "charlie").bind("delta").get()).toEqual("four(alpha,bravo,charlie,delta)");
          expect(f4.bind("alpha").bind("bravo", "charlie", "delta").get()).toEqual("four(alpha,bravo,charlie,delta)");
          expect(f4.bind("alpha", "bravo", "charlie", "delta").get()).toEqual("four(alpha,bravo,charlie,delta)");
        });
      });
    });
  }
}
