package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@SuppressWarnings({"CodeBlock2Expr", "ClassInitializerMayBeStatic"})
@RunWith(Spectrum.class)
public class NumbersTest {

  {
    Seq.of(
      BigDecimal.class,
      BigInteger.class,
      byte.class, Byte.class,
      short.class, Short.class,
      int.class, Integer.class,
      long.class, Long.class,
      float.class, Float.class,
      double.class, Double.class,
      AtomicInteger.class,
      AtomicLong.class
    ).forEach(clazz -> {
      describe(clazz.toString(), () -> {
        it("zero", () -> {
          expect(Numbers.zero(clazz).longValue()).toEqual(0);
        });
        it("one", () -> {
          expect(Numbers.one(clazz).longValue()).toEqual(1);
        });
      });
    });

    describe("Miscellaneous", () -> {
      it("intFromBytes", () -> {
        expect(
          Numbers.intFromBytes((byte) 1, (byte) 0, (byte) 0, (byte) 0)
        ).toEqual(1 << 24);
        expect(
          Numbers.intFromBytes((byte) 2, (byte) 0, (byte) 0, (byte) 0)
        ).toEqual(1 << 25);
      });
      it("longFromBytes", () -> {
        expect(
          Numbers.longFromBytes((byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 1, (byte) 0, (byte) 0, (byte) 0)
        ).toEqual(1L << 24);
        expect(
          Numbers.longFromBytes((byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 2, (byte) 0, (byte) 0, (byte) 0)
        ).toEqual(1L << 25);
        expect(
          Numbers.longFromBytes((byte) 1, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0)
        ).toEqual(1L << 56);
      });
    });
  }
}
