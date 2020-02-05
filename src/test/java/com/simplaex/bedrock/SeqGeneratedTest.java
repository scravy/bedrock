package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import lombok.val;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.function.IntFunction;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@SuppressWarnings({"ClassInitializerMayBeStatic", "CodeBlock2Expr"})
@RunWith(Spectrum.class)
public class SeqGeneratedTest {

  {
    describe("a simple seq", () -> {
      final Integer[] data = new Integer[]{1, 2, 2, 4, 3};
      final IntFunction<Integer> generator = i -> {
        return data[i];
      };
      val seq = Seq.ofGenerator(generator, data.length);
      describe("without any modifications", () -> {
        SeqExemplaryChecks.checks(seq);
        SeqPropertyChecks.checks(seq);
      });
      describe("memoizing generator", () -> {
        val memoizingSeq = Seq.ofGeneratorMemoizing(generator, data.length);
        SeqExemplaryChecks.checks(memoizingSeq);
        SeqPropertyChecks.checks(memoizingSeq);
      });
      describe("reversed twice", () -> {
        val rev = seq.reversed().reversed();
        SeqExemplaryChecks.checks(rev);
        SeqPropertyChecks.checks(rev);
      });
    });

    describe("a seq made from an int[] array", () -> {
      SeqExemplaryChecks.checks(Seq.wrap(new int[]{1, 2, 2, 4, 3}));
      SeqPropertyChecks.checks(Seq.wrap(new int[]{1, 2, 2, 4, 3}));
    });

    describe("a seq made from an Integer[] array", () -> {
      SeqExemplaryChecks.checks(Seq.wrap(new Integer[]{1, 2, 2, 4, 3}));
      SeqPropertyChecks.checks(Seq.wrap(new Integer[]{1, 2, 2, 4, 3}));
    });

    describe("a seq made from a list", () -> {
      SeqExemplaryChecks.checks(Seq.wrap(Arrays.asList(1, 2, 2, 4, 3)));
      SeqPropertyChecks.checks(Seq.wrap(Arrays.asList(1, 2, 2, 4, 3)));
    });

    describe("wrap empty arrays and collections", () -> {
      it("should wrap an empty char array as the empty Seq", () -> {
        expect(Seq.wrap(new char[0]) == Seq.<Character>empty()).toBeTrue();
      });
      it("should wrap an empty byte array as the empty Seq", () -> {
        expect(Seq.wrap(new byte[0]) == Seq.<Byte>empty()).toBeTrue();
      });
      it("should wrap an empty short array as the empty Seq", () -> {
        expect(Seq.wrap(new short[0]) == Seq.<Short>empty()).toBeTrue();
      });
      it("should wrap an empty int array as the empty Seq", () -> {
        expect(Seq.wrap(new int[0]) == Seq.<Integer>empty()).toBeTrue();
      });
      it("should wrap an empty long array as the empty Seq", () -> {
        expect(Seq.wrap(new long[0]) == Seq.<Long>empty()).toBeTrue();
      });
      it("should wrap an empty double array as the empty Seq", () -> {
        expect(Seq.wrap(new double[0]) == Seq.<Double>empty()).toBeTrue();
      });
      it("should wrap an empty float array as the empty Seq", () -> {
        expect(Seq.wrap(new float[0]) == Seq.<Float>empty()).toBeTrue();
      });
      it("should wrap an empty boolean array as the empty Seq", () -> {
        expect(Seq.wrap(new boolean[0]) == Seq.<Boolean>empty()).toBeTrue();
      });
      it("should wrap an empty Character array as the empty Seq", () -> {
        expect(Seq.wrap(new Character[0]) == Seq.<Character>empty()).toBeTrue();
      });
      it("should wrap an empty Byte array as the empty Seq", () -> {
        expect(Seq.wrap(new Byte[0]) == Seq.<Byte>empty()).toBeTrue();
      });
      it("should wrap an empty Short array as the empty Seq", () -> {
        expect(Seq.wrap(new Short[0]) == Seq.<Short>empty()).toBeTrue();
      });
      it("should wrap an empty Integer array as the empty Seq", () -> {
        expect(Seq.wrap(new Integer[0]) == Seq.<Integer>empty()).toBeTrue();
      });
      it("should wrap an empty Long array as the empty Seq", () -> {
        expect(Seq.wrap(new Long[0]) == Seq.<Long>empty()).toBeTrue();
      });
      it("should wrap an empty Double array as the empty Seq", () -> {
        expect(Seq.wrap(new Double[0]) == Seq.<Double>empty()).toBeTrue();
      });
      it("should wrap an empty Float array as the empty Seq", () -> {
        expect(Seq.wrap(new Float[0]) == Seq.<Float>empty()).toBeTrue();
      });
      it("should wrap an empty Boolean array as the empty Seq", () -> {
        expect(Seq.wrap(new Boolean[0]) == Seq.<Boolean>empty()).toBeTrue();
      });
    });

    describe("wrap arrays and collections", () -> {
      it("should wrap a char array as a Seq", () -> {
        expect(Seq.wrap(new char[]{'a', 'b'})).toEqual(Seq.of('a', 'b'));
      });
      it("should wrap a byte array as a Seq", () -> {
        expect(Seq.wrap(new byte[]{64, 101})).toEqual(Seq.of((byte) 64, (byte) 101));
      });
      it("should wrap a short array as a Seq", () -> {
        expect(Seq.wrap(new short[]{2819, 1021})).toEqual(Seq.of((short) 2819, (short) 1021));
      });
      it("should wrap a int array as a Seq", () -> {
        expect(Seq.wrap(new int[]{1, 2, 3})).toEqual(Seq.of(1, 2, 3));
      });
      it("should wrap a long array as a Seq", () -> {
        expect(Seq.wrap(new long[]{2, 5, 9, 13})).toEqual(Seq.of(2L, 5L, 9L, 13L));
      });
      it("should wrap a double array as a Seq", () -> {
        expect(Seq.wrap(new double[]{1.2, 4.3, Math.PI})).toEqual(Seq.of(1.2, 4.3, Math.PI));
      });
      it("should wrap a float array as a Seq", () -> {
        expect(Seq.wrap(new float[]{1f, 2f, 2.5f})).toEqual(Seq.of(1f, 2f, 2.5f));
      });
      it("should wrap a boolean array as a Seq", () -> {
        expect(Seq.wrap(new boolean[]{true})).toEqual(Seq.of(true));
      });
      it("should wrap a Character array as a Seq", () -> {
        expect(Seq.wrap(new Character[]{'a', 'x', '_'})).toEqual(Seq.of('a', 'x', '_'));
      });
      it("should wrap a Byte array as a Seq", () -> {
        expect(Seq.wrap(new Byte[]{(byte) 0, (byte) 2, (byte) -1})).toEqual(Seq.of((byte) 0, (byte) 2, (byte) -1));
      });
      it("should wrap a Short array as a Seq", () -> {
        expect(Seq.wrap(new Short[]{(short) 19})).toEqual(Seq.of((short) 19));
      });
      it("should wrap a Integer array as a Seq", () -> {
        expect(Seq.wrap(new Integer[]{4, -23, 0})).toEqual(Seq.of(4, -23, 0));
      });
      it("should wrap a Long array as a Seq", () -> {
        expect(Seq.wrap(new Long[]{10L, 19L, 92L, 43L})).toEqual(Seq.of(10L, 19L, 92L, 43L));
      });
      it("should wrap a Double array as a Seq", () -> {
        expect(Seq.wrap(new Double[]{1.2, 4.3, Math.PI})).toEqual(Seq.of(1.2, 4.3, Math.PI));
      });
      it("should wrap a Float array as a Seq", () -> {
        expect(Seq.wrap(new Float[]{1f, 2f, 2.5f})).toEqual(Seq.of(1f, 2f, 2.5f));
      });
      it("should wrap a Boolean array as a Seq", () -> {
        expect(Seq.wrap(new Boolean[]{false, true, false, false})).toEqual(Seq.of(false, true, false, false));
      });
    });
    describe("get", () -> {
      it("should throw an IndexOutOfBoundsException when provided with a negative index", () -> {
        expect(() -> Seq.ofGenerator(x -> x, 3).get(-1)).toThrow(IndexOutOfBoundsException.class);
      });
      it("should throw an IndexOutOfBoundsException when provided with an index >= length", () -> {
        expect(() -> Seq.ofGenerator(x -> x, 3).get(3)).toThrow(IndexOutOfBoundsException.class);
      });
    });
  }

}
