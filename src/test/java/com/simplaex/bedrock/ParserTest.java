package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import lombok.val;
import org.junit.runner.RunWith;

import java.util.Optional;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@SuppressWarnings("CodeBlock2Expr")
@RunWith(Spectrum.class)
public class ParserTest {

  final Seq<Integer> seq = Seq.rangeInclusive(1, 10);

  private Parser<Integer> eq(final int x) {
    return Parser.satisfies(Integer.class, z -> z == x);
  }

  private Parser<Integer> lt(final int x) {
    return Parser.satisfies(Integer.class, z -> z < x);
  }

  private Parser<Integer> gt(final int x) {
    return Parser.satisfies(Integer.class, z -> z > x);
  }

  private Parser<Integer> even() {
    return Parser.satisfies(Integer.class, z -> z % 2 == 0);
  }

  private Parser<Integer> odd() {
    return Parser.satisfies(Integer.class, z -> z % 2 != 0);
  }

  {
    describe("left", () -> {
      it("success", () -> {
        val p = Parser.left(lt(5), lt(5));
        val r = p.parse(seq);
        expect(r.isSuccess()).toBeTrue();
        expect(r.getValue()).toEqual(1);
      });
      it("no parse", () -> {
        val p = Parser.left(gt(7), lt(5));
        val r = p.parse(seq);
        expect(r.isSuccess()).toBeFalse();
      });
    });
    describe("right", () -> {
      it("success", () -> {
        val p = Parser.right(lt(5), lt(5));
        val r = p.parse(seq);
        expect(r.isSuccess()).toBeTrue();
        expect(r.getValue()).toEqual(2);
      });
      it("no parse", () -> {
        val p = Parser.right(gt(5), lt(5));
        val r = p.parse(seq);
        expect(r.isSuccess()).toBeFalse();
      });
    });
    describe("optional", () -> {
      it("success", () -> {
        val p = Parser.optional(lt(5));
        val r = p.parse(seq);
        expect(r.isSuccess()).toBeTrue();
        expect(r.getValue()).toEqual(Optional.of(1));
      });
      it("no parse, but success", () -> {
        val p = Parser.optional(gt(5));
        val r = p.parse(seq);
        expect(r.isSuccess()).toBeTrue();
        expect(r.getValue()).toEqual(Optional.empty());
      });
    });
    describe("choice", () -> {
      it("success", () -> {
        val p = Parser.choice(
          even().map(x -> "not odd"),
          odd().map(x -> "not even")
        );
        val r = p.parse(seq);
        expect(r.isSuccess()).toBeTrue();
        expect(r.getValue()).toEqual("not even");
      });
      it("success (switched order of parser)", () -> {
        val p = Parser.choice(
          odd().map(x -> "not even"),
          even().map(x -> "not odd")
        );
        val r = p.parse(seq);
        expect(r.isSuccess()).toBeTrue();
        expect(r.getValue()).toEqual("not even");
      });
      it("no parse", () -> {
        val p = Parser.choice(
          eq(2),
          eq(3)
        );
        val r = p.parse(seq);
        expect(r.isSuccess()).toBeFalse();
      });
    });
    describe("sequence", () -> {
      it("success", () -> {
        val p = Parser.sequence(eq(1), eq(2), eq(3), eq(4), eq(5));
        val r = p.parse(seq);
        expect(r.isSuccess()).toBeTrue();
        expect(r.getValue()).toEqual(Seq.of(1, 2, 3, 4, 5));
      });
      it("no parse", () -> {
        val p = Parser.sequence(eq(1), eq(2), eq(30), eq(4), eq(5));
        val r = p.parse(seq);
        expect(r.isSuccess()).toBeFalse();
      });
    });
    describe("seq2", () -> {
      it("success", () -> {
        val p = Parser.seq(eq(1), eq(2));
        val r = p.parse(seq);
        expect(r.isSuccess()).toBeTrue();
        expect(r.getValue()).toEqual(Pair.of(1, 2));
      });
      it("no parse", () -> {
        val p = Parser.seq(eq(2), eq(2));
        val r = p.parse(seq);
        expect(r.isSuccess()).toBeFalse();
      });
      it("no parse (2)", () -> {
        val p = Parser.seq(eq(1), eq(3));
        val r = p.parse(seq);
        expect(r.isSuccess()).toBeFalse();
      });
    });
    describe("seq3", () -> {
      it("success", () -> {
        val p = Parser.seq(eq(1), eq(2), eq(3));
        val r = p.parse(seq);
        expect(r.isSuccess()).toBeTrue();
        expect(r.getValue()).toEqual(Triple.of(1, 2, 3));
      });
      it("no parse", () -> {
        val p = Parser.seq(eq(2), eq(2), eq(2));
        val r = p.parse(seq);
        expect(r.isSuccess()).toBeFalse();
      });
      it("no parse (2)", () -> {
        val p = Parser.seq(eq(1), eq(3), eq(2));
        val r = p.parse(seq);
        expect(r.isSuccess()).toBeFalse();
      });
      it("no parse (3)", () -> {
        val p = Parser.seq(eq(1), eq(2), eq(2));
        val r = p.parse(seq);
        expect(r.isSuccess()).toBeFalse();
      });
    });
    describe("seq4", () -> {
      it("success", () -> {
        val p = Parser.seq(eq(1), eq(2), eq(3), eq(4));
        val r = p.parse(seq);
        expect(r.isSuccess()).toBeTrue();
        expect(r.getValue()).toEqual(Quadruple.of(1, 2, 3, 4));
      });
    });
    describe("many", () -> {
      it("success", () -> {
        val p = Parser.many(lt(5));
        val r = p.parse(seq);
        expect(r.isSuccess()).toBeTrue();
        expect(r.getValue()).toEqual(Seq.rangeInclusive(1, 4));
      });
      it("success (2)", () -> {
        val p = Parser.many(lt(20));
        val r = p.parse(seq);
        expect(r.isSuccess()).toBeTrue();
        expect(r.getValue()).toEqual(Seq.rangeInclusive(1, 10));
      });
      it("no parse, but success", () -> {
        val p = Parser.many(gt(5));
        val r = p.parse(seq);
        expect(r.isSuccess()).toBeTrue();
        expect(r.getValue()).toEqual(Seq.empty());
      });
    });
    describe("many1", () -> {
      it("success", () -> {
        val p = Parser.many1(lt(5));
        val r = p.parse(seq);
        expect(r.isSuccess()).toBeTrue();
        expect(r.getValue()).toEqual(Seq.rangeInclusive(1, 4));
      });
      it("no parse", () -> {
        val p = Parser.many1(gt(5));
        val r = p.parse(seq);
        expect(r.isSuccess()).toBeFalse();
      });
    });
    describe("sepBy", () -> {
      it("success", () -> {
        val p = Parser.sepBy(lt(6), even());
        val r = p.parse(seq);
        expect(r.isSuccess()).toBeTrue();
        expect(r.getValue()).toEqual(Seq.of(1, 3, 5));
      });
    });
    describe("sepBy1", () -> {
      it("success", () -> {
        val p = Parser.sepBy1(lt(6), even());
        val r = p.parse(seq);
        expect(r.isSuccess()).toBeTrue();
        expect(r.getValue()).toEqual(Seq.of(1, 3, 5));
      });
    });
    describe("recurse", () -> {
      it("should parse a tree structure", () -> {
        class X {
          final Parser<Integer> p() {
            return Parser.many(
              Parser.choice(
                Parser.satisfies(Integer.class, x -> true),
                Parser.recurse(Seq.class, x -> true, x -> x, Parser.recursive(this::p))
              )
            ).map(Seq::intSum);
          }
        }
        val x = new X();
        val seq = Seq.of(5, Seq.of(7, 13, Seq.of(5), Seq.of(5, 5)));
        val res = x.p().parse(seq);
        expect(res.isSuccess()).toBeTrue();
        expect(res.getValue()).toEqual(40);
      });
    });
    describe("recurse2", () -> {
      it("should parse a tree structure", () -> {
        class X {
          final Parser<Integer> p() {
            return Parser.many(
              Parser.choice(
                Parser.satisfies(Integer.class, x -> true),
                Parser.recurse2(Seq.class, x -> x, x -> Parser.recursive(this::p).map(z -> z + x.length()))
              )
            ).map(Seq::intSum);
          }
        }
        val x = new X();
        val seq = Seq.of(5, Seq.of(7, 13, Seq.of(5), Seq.of(5, 5)));
        val res = x.p().parse(seq);
        expect(res.isSuccess()).toBeTrue();
        expect(res.getValue()).toEqual(47);
      });
    });
  }
}
