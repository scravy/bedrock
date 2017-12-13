package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import lombok.val;
import org.junit.runner.RunWith;

import static com.greghaskins.spectrum.Spectrum.describe;

@SuppressWarnings({"ClassInitializerMayBeStatic", "CodeBlock2Expr"})
@RunWith(Spectrum.class)
public class SeqReversedTest {

  {
    describe("a simple seq reversed", () -> {
      val seq = Seq.of(3, 4, 2, 2, 1).reversed();
      SeqExemplaryChecks.checks(seq);
      SeqPropertyChecks.checks(seq);
    });
    describe("a simple seq reversed twice", () -> {
      val seq = Seq.of(1, 2, 2, 4, 3).reversed().reversed();
      SeqExemplaryChecks.checks(seq);
      SeqPropertyChecks.checks(seq);
    });
  }

}
