package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import lombok.val;
import org.junit.runner.RunWith;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@SuppressWarnings("ClassInitializerMayBeStatic")
@RunWith(Spectrum.class)
public class SeqSimpleViewTest {

  {
    describe("a view of a simple seq", () -> {
      val seq = Seq.of(0, 1, 2, 2, 4, 3, 10).subSequenceView(1, 6);
      SeqExemplaryChecks.checks(seq);
      SeqPropertyChecks.checks(seq);
    });
    describe("some seqs containing null values", () -> {
      val seq = Seq.of(3, null, 4, 12, null, null);
      SeqPropertyChecks.checks(seq);
    });
    describe("some seqs containing null values", () -> {
      val seq = Seq.of(3, null, 4, 12, null, null).dropRightView(1);
      it("should be a view", () -> {
        expect(seq).toBeInstanceOf(SeqSimpleView.class);
      });
      SeqPropertyChecks.checks(seq);
    });
  }

}
