package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import org.junit.runner.RunWith;

import static com.greghaskins.spectrum.Spectrum.describe;

@SuppressWarnings({"ClassInitializerMayBeStatic", "CodeBlock2Expr"})
@RunWith(Spectrum.class)
public class SeqReversedTest {

  {
    describe("a simple seq reversed", () -> {
      SeqChecks.checks(Seq.of(3, 4, 2, 2, 1).reversed());
    });
    describe("a simple seq reversed twice", () -> {
      SeqChecks.checks(Seq.of(1, 2, 2, 4, 3).reversed().reversed());
    });
  }

}
