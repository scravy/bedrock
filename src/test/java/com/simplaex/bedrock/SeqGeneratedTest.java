package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import lombok.val;
import org.junit.runner.RunWith;

import static com.greghaskins.spectrum.Spectrum.describe;

@SuppressWarnings({"ClassInitializerMayBeStatic", "CodeBlock2Expr"})
@RunWith(Spectrum.class)
public class SeqGeneratedTest {

  {
    describe("a simple seq", () -> {
      val seq = Seq.ofGenerator(i -> {
        switch (i) {
          case 0:
            return 1;
          case 1:
            return 2;
          case 2:
            return 2;
          case 3:
            return 4;
          case 4:
            return 3;
        }
        return 6;
      }, 5);
      SeqExemplaryChecks.checks(seq);
      SeqPropertyChecks.checks(seq);
    });
  }

}
