package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import lombok.val;
import org.junit.runner.RunWith;

import static com.greghaskins.spectrum.Spectrum.describe;

@SuppressWarnings({"ClassInitializerMayBeStatic", "CodeBlock2Expr"})
@RunWith(Spectrum.class)
public class SeqSimpleTest {

  {
    describe("a simple seq", () -> {
      val seq = Seq.of(1, 2, 2, 4, 3);
      SeqExemplaryChecks.checks(seq);
      SeqPropertyChecks.checks(seq);
    });
  }

}
