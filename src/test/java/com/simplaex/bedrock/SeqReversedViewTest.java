package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import lombok.val;
import org.junit.runner.RunWith;

import static com.greghaskins.spectrum.Spectrum.describe;

@SuppressWarnings({"ClassInitializerMayBeStatic", "CodeBlock2Expr"})
@RunWith(Spectrum.class)
public class SeqReversedViewTest {

  {
    describe("a simple seq view reversed", () -> {
      val seq = Seq.of(1, 2, 3, 4, 2, 2, 1, 3, 4).subSequenceView(2, 7).reversed();
      SeqExemplaryChecks.checks(seq);
      SeqPropertyChecks.checks(seq);
    });
    describe("a reversed seq view", () -> {
      val seq = Seq.of(1, 2, 3, 4, 2, 2, 1, 3, 4).reversed().subSequenceView(2, 7);
      SeqExemplaryChecks.checks(seq);
      SeqPropertyChecks.checks(seq);
    });
  }

}
