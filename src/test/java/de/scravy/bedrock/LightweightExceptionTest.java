package de.scravy.bedrock;


import com.greghaskins.spectrum.Spectrum;
import org.junit.runner.RunWith;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@RunWith(Spectrum.class)
public class LightweightExceptionTest {

  {
    describe("throwing a LightweightException", () -> {
      it("should not have a stack trace", () -> {
        try {
          throw new LightweightException();
        } catch (final LightweightException exc) {
          expect(exc.getStackTrace().length).toEqual(0);
        }
      });
    });
  }

}
