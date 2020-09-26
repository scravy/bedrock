package de.scravy.bedrock;

import com.greghaskins.spectrum.Spectrum;
import lombok.val;
import org.junit.runner.RunWith;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

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
    describe("some seqs containing null values", () -> {
      val seq = Seq.of(3, null, 4, 12, null, null).dropRightView(1).reversed();
      it("should be a view", () -> {
        expect(seq).toBeInstanceOf(SeqReversedView.class);
      });
      SeqPropertyChecks.checks(seq);
    });
  }

}
