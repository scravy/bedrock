package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import org.junit.runner.RunWith;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@RunWith(Spectrum.class)
public class ConversionsTest {

  {
    describe("Conversions", () -> {
      it("fromTo", () -> {
        expect(Conversions.fromTo(Integer.class, String.class).apply(7)).toEqual("7");
        expect(Conversions.fromTo(String.class, Integer.class).apply("7")).toEqual(7);
      });
    });
  }

}
