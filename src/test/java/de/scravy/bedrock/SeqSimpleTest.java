package de.scravy.bedrock;

import com.greghaskins.spectrum.Spectrum;
import lombok.val;
import org.junit.runner.RunWith;

import java.math.BigDecimal;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@SuppressWarnings({"ClassInitializerMayBeStatic", "CodeBlock2Expr"})
@RunWith(Spectrum.class)
public class SeqSimpleTest {

  {
    describe("a simple seq", () -> {
      val seq = Seq.of(1, 2, 2, 4, 3);
      SeqExemplaryChecks.checks(seq);
      SeqPropertyChecks.checks(seq);
    });
    describe("some seqs containing null values", () -> {
      val seq = Seq.of(3, null, 4, 12, null, null);
      SeqPropertyChecks.checks(seq);
    });
    describe("toMap", () -> {
      it("should create a Map by grouping", () -> {
        val m = Seq.of("one", "two", "three").toMap(s -> s.charAt(0));
        expect(m.apply('o')).toEqual(Seq.of("one"));
        expect(m.apply('t')).toEqual(Seq.of("two", "three"));
      });
      it("should create a Map from non-Comparable values by grouping", () -> {
        val m = Seq.<Number>of(BigDecimal.ONE, 1).toMap(x -> (Class) x.getClass());
        expect(m.apply(BigDecimal.class)).toEqual(Seq.<Number>of(BigDecimal.ONE));
        expect(m.apply(Integer.class)).toEqual(Seq.of(1));
      });
    });
    describe("toArrayMap", () -> {
      it("should create an ArrayMap by grouping", () -> {
        val m = Seq.of("one", "two", "three").toArrayMap(s -> s.charAt(0));
        expect(m.apply('o')).toEqual(Seq.of("one"));
        expect(m.apply('t')).toEqual(Seq.of("two", "three"));
      });
    });
    it("filter(Clazz)", () -> {
      final Seq<Double> seq = Seq.<Number>of(1, 2, 3, 4, 1.0, 2.0).filter(Double.class);
      expect(seq).toEqual(Seq.of(1.0, 2.0));
    });
  }

}
