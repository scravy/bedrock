package de.scravy.bedrock;

import com.greghaskins.spectrum.Spectrum;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@RunWith(Spectrum.class)
@SuppressWarnings("ClassInitializerMayBeStatic")
public class ExtendedIterableTest {
  {
    describe("ExtendedIterableTest", () -> {
      final ExtendedIterable<Integer> container = ExtendedIterable.fromIterable(Arrays.asList(1, 1, 2, 3, 5, 8));
      it("should correctly check that the container has a length of at least 3", () -> {
        expect(container.lengthAtLeast(3)).toBeTrue();
      });
    });
  }

}
