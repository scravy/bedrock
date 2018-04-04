package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.math.BigInteger;

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
      double.class, Double.class
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
  }
}
