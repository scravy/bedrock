package de.scravy.bedrock;

import com.greghaskins.spectrum.Spectrum;
import org.junit.runner.RunWith;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@RunWith(Spectrum.class)
public class HasLengthTest {

  {
    describe("HasLength", () -> {
      it("lengthAtLeast default", () -> {
        final HasLength len = () -> 7;
        expect(len.lengthAtLeast(6)).toBeFalse();
        expect(len.lengthAtLeast(7)).toBeTrue();
        expect(len.lengthAtLeast(8)).toBeTrue();
      });
    });
  }

}
