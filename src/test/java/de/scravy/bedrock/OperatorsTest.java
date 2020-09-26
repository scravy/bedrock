package de.scravy.bedrock;

import com.greghaskins.spectrum.Spectrum;
import lombok.val;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.BiFunction;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@SuppressWarnings({"ClassInitializerMayBeStatic", "CodeBlock2Expr"})
@RunWith(Spectrum.class)
public class OperatorsTest {

  private <N extends Number> void check(
    final Seq<N> seq,
    final BiFunction<N, N, N> f,
    final BiFunction<Number, Number, Number> g,
    final N unit,
    final Object result
  ) {
    it("should foldl for " + unit.getClass() + " (" + unit + ")", () -> {
      expect(seq.foldl(f, unit)).toEqual(result);
    });
    it("should foldl using dynamic operator for " + unit.getClass() + " (" + unit + ")", () -> {
      expect(seq.foldl(g, unit)).toEqual(result);
    });
  }

  private <N extends Comparable<? super N>> void check(final N a, final N b, final BiFunction<N, N, Boolean> f) {
    it("should apply the comparison operator on " + a + " and " + b, () -> {
      expect(f.apply(a, b)).toEqual(Boolean.TRUE);
      expect(f.apply(b, a)).toEqual(Boolean.FALSE);
      expect(f.apply(a, null)).toEqual(null);
      expect(f.apply(null, b)).toEqual(null);
    });
  }

  {
    describe("Operators", () -> {
      val intSeq = Seq.<Integer>of(1, 2, 3);
      val longSeq = Seq.<Long>of(1L, 2L, 3L);
      val doubleSeq = Seq.<Double>of(1.0, 2.0, 3.0);
      val bigIntSeq = Seq.<BigInteger>of(BigInteger.valueOf(1), BigInteger.valueOf(2), BigInteger.valueOf(3));
      val bigDecSeq = Seq.<BigDecimal>of(BigDecimal.valueOf(1), BigDecimal.valueOf(2), BigDecimal.valueOf(3));

      val intSeqN = Seq.<Integer>of(1, 2, null, 3);
      val longSeqN = Seq.<Long>of(1L, 2L, null, 3L);
      val doubleSeqN = Seq.<Double>of(1.0, 2.0, null, 3.0);
      val bigIntSeqN = Seq.<BigInteger>of(BigInteger.valueOf(1), BigInteger.valueOf(2), null, BigInteger.valueOf(3));
      val bigDecSeqN = Seq.<BigDecimal>of(BigDecimal.valueOf(1), BigDecimal.valueOf(2), null, BigDecimal.valueOf(3));

      describe("plus", () -> {
        check(intSeq, Operators::plus, Operators::plus, 0, 6);
        check(longSeq, Operators::plus, Operators::plus, 0L, 6L);
        check(doubleSeq, Operators::plus, Operators::plus, 0.0, 6.0);
        check(bigIntSeq, Operators::plus, Operators::plus, BigInteger.ZERO, BigInteger.valueOf(6));
        check(bigDecSeq, Operators::plus, Operators::plus, BigDecimal.ZERO, BigDecimal.valueOf(6));

        check(intSeqN, Operators::plus, Operators::plus, 0, null);
        check(longSeqN, Operators::plus, Operators::plus, 0L, null);
        check(doubleSeqN, Operators::plus, Operators::plus, 0.0, null);
        check(bigIntSeqN, Operators::plus, Operators::plus, BigInteger.ZERO, null);
        check(bigDecSeqN, Operators::plus, Operators::plus, BigDecimal.ZERO, null);
      });

      describe("or", () -> {
        check(intSeq, Operators::or, Operators::or, 0, 3);
        check(longSeq, Operators::or, Operators::or, 0L, 3L);
        check(bigIntSeq, Operators::or, Operators::or, BigInteger.ZERO, BigInteger.valueOf(3));

        check(intSeqN, Operators::or, Operators::or, 0, null);
        check(longSeqN, Operators::or, Operators::or, 0L, null);
        check(bigIntSeqN, Operators::or, Operators::or, BigInteger.ZERO, null);
      });

      describe("minus", () -> {
        check(intSeq, Operators::minus, Operators::minus, 0, -6);
        check(longSeq, Operators::minus, Operators::minus, 0L, -6L);
        check(doubleSeq, Operators::minus, Operators::minus, -0.0, -6.0);
        check(bigIntSeq, Operators::minus, Operators::minus, BigInteger.ZERO, BigInteger.valueOf(-6));
        check(bigDecSeq, Operators::minus, Operators::minus, BigDecimal.ZERO, BigDecimal.valueOf(-6));

        check(intSeqN, Operators::minus, Operators::minus, 0, null);
        check(longSeqN, Operators::minus, Operators::minus, 0L, null);
        check(doubleSeqN, Operators::minus, Operators::minus, -0.0, null);
        check(bigIntSeqN, Operators::minus, Operators::minus, BigInteger.ZERO, null);
        check(bigDecSeqN, Operators::minus, Operators::minus, BigDecimal.ZERO, null);
      });

      describe("times", () -> {
        check(intSeq, Operators::times, Operators::times, 1, 6);
        check(longSeq, Operators::times, Operators::times, 1L, 6L);
        check(doubleSeq, Operators::times, Operators::times, 1.0, 6.0);
        check(bigIntSeq, Operators::times, Operators::times, BigInteger.ONE, BigInteger.valueOf(6));
        check(bigDecSeq, Operators::times, Operators::times, BigDecimal.ONE, BigDecimal.valueOf(6));

        check(intSeqN, Operators::times, Operators::times, 1, null);
        check(longSeqN, Operators::times, Operators::times, 1L, null);
        check(doubleSeqN, Operators::times, Operators::times, 1.0, null);
        check(bigIntSeqN, Operators::times, Operators::times, BigInteger.ONE, null);
        check(bigDecSeqN, Operators::times, Operators::times, BigDecimal.ONE, null);
      });

      describe("and", () -> {
        check(intSeq, Operators::and, Operators::and, 0, 0);
        check(longSeq, Operators::and, Operators::and, 0L, 0L);
        check(bigIntSeq, Operators::and, Operators::and, BigInteger.ZERO, BigInteger.valueOf(0));

        check(intSeqN, Operators::and, Operators::and, 0, null);
        check(longSeqN, Operators::and, Operators::and, 0L, null);
        check(bigIntSeqN, Operators::and, Operators::and, BigInteger.ZERO, null);
      });

      describe("div", () -> {
        check(intSeq, Operators::div, Operators::div, 6, 1);
        check(longSeq, Operators::div, Operators::div, 6L, 1L);
        check(doubleSeq, Operators::div, Operators::div, 6.0, 1.0);
        check(bigIntSeq, Operators::div, Operators::div, BigInteger.valueOf(6), BigInteger.ONE);
        check(bigDecSeq, Operators::div, Operators::div, BigDecimal.valueOf(6), BigDecimal.ONE);

        check(intSeqN, Operators::div, Operators::div, 6, null);
        check(longSeqN, Operators::div, Operators::div, 6L, null);
        check(doubleSeqN, Operators::div, Operators::div, 6.0, null);
        check(bigIntSeqN, Operators::div, Operators::div, BigInteger.valueOf(6), null);
        check(bigDecSeqN, Operators::div, Operators::div, BigDecimal.valueOf(6), null);
      });

      describe("mod", () -> {
        check(intSeq, Operators::mod, Operators::mod, 6, 0);
        check(longSeq, Operators::mod, Operators::mod, 6L, 0L);
        check(doubleSeq, Operators::mod, Operators::mod, 6.0, 0.0);
        check(bigIntSeq, Operators::mod, Operators::mod, BigInteger.valueOf(6), BigInteger.ZERO);

        check(intSeqN, Operators::mod, Operators::mod, 6, null);
        check(longSeqN, Operators::mod, Operators::mod, 6L, null);
        check(doubleSeqN, Operators::mod, Operators::mod, 6.0, null);
        check(bigIntSeqN, Operators::mod, Operators::mod, BigInteger.valueOf(6), null);
        check(bigDecSeqN, Operators::mod, Operators::mod, BigDecimal.valueOf(6), null);
      });

      describe("comparison operators", () -> {
        describe(Integer.class.toString(), () -> {
          check(3, 2, Operators::gt);
          check(3, 2, Operators::gte);
          check(1, 2, Operators::lt);
          check(1, 2, Operators::lte);
        });
        describe(Long.class.toString(), () -> {
          check(3L, 2L, Operators::gt);
          check(3L, 2L, Operators::gte);
          check(1L, 2L, Operators::lt);
          check(1L, 2L, Operators::lte);
        });
        describe(Double.class.toString(), () -> {
          check(3.0, 2.0, Operators::gt);
          check(3.0, 2.0, Operators::gte);
          check(1.0, 2.0, Operators::lt);
          check(1.0, 2.0, Operators::lte);
        });
        describe(String.class.toString(), () -> {
          check("c", "b", Operators::gt);
          check("c", "b", Operators::gte);
          check("a", "b", Operators::lt);
          check("a", "b", Operators::lte);
        });
      });

    });

    describe("primitive operators", () -> {
      describe("plus", () -> {
        it("1 + 1 (int)", () -> {
          expect(Operators.plus(1, 1)).toEqual(2);
        });
        it("1 + 1 (long)", () -> {
          expect(Operators.plus(1L, 1L)).toEqual(2);
        });
        it("1 + 1 (double)", () -> {
          expect(Operators.plus(1.0, 1.0)).toEqual(2.0);
        });
      });
      describe("minus", () -> {
        it("1 - 1 (int)", () -> {
          expect(Operators.minus(1, 1)).toEqual(0);
        });
        it("1 - 1 (long)", () -> {
          expect(Operators.minus(1L, 1L)).toEqual(0);
        });
        it("1 - 1 (double)", () -> {
          expect(Operators.minus(1.0, 1.0)).toEqual(0.0);
        });
      });
      describe("times", () -> {
        it("1 * 1 (int)", () -> {
          expect(Operators.times(1, 1)).toEqual(1);
        });
        it("1 * 1 (long)", () -> {
          expect(Operators.times(1L, 1L)).toEqual(1);
        });
        it("1 * 1 (double)", () -> {
          expect(Operators.times(1.0, 1.0)).toEqual(1.0);
        });
      });
      describe("div", () -> {
        it("4 / 2 (int)", () -> {
          expect(Operators.div(4, 2)).toEqual(2);
        });
        it("4 / 2 (long)", () -> {
          expect(Operators.div(4L, 2L)).toEqual(2);
        });
        it("4 / 2 (double)", () -> {
          expect(Operators.div(4.0, 2.0)).toEqual(2.0);
        });
      });
      describe("mod", () -> {
        it("8 % 2 (int)", () -> {
          expect(Operators.div(8, 2)).toEqual(4);
        });
        it("8 % 2 (long)", () -> {
          expect(Operators.div(8L, 2L)).toEqual(4);
        });
        it("8 % 2 (double)", () -> {
          expect(Operators.div(8.0, 2.0)).toEqual(4.0);
        });
      });
    });
  }
}
