package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import org.junit.runner.RunWith;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@SuppressWarnings({"ClassInitializerMayBeStatic", "CodeBlock2Expr"})
@RunWith(Spectrum.class)
public class BloomFiltersTest {

  {
    describe("BloomFilters", () -> {
      it("should calculate the false positives probability given numBits and expected insertions", () -> {

        final double fpp = 0.03;
        final long expectedInsertions = 100000;

        final long numBits =
          BloomFilters.numBits(expectedInsertions, fpp);
        final double falsePositivesProbability =
          BloomFilters.falsePositivesProbability(numBits, expectedInsertions);

        expect(Numbers.isApproximately(fpp, 10e-6).test(falsePositivesProbability)).toBeTrue();

      });
    });
  }
}
