package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import org.junit.runner.RunWith;

import static com.greghaskins.spectrum.Spectrum.describe;

@SuppressWarnings("ClassInitializerMayBeStatic")
@RunWith(Spectrum.class)
public class SeqSimpleViewTest {

  {
    describe("a view of a simple seq", () -> SeqExemplaryChecks.checks(Seq.of(0, 1, 2, 2, 4, 3, 10).subSequenceView(1, 6)));
  }

}
