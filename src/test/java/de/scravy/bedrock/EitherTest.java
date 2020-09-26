package de.scravy.bedrock;

import com.greghaskins.spectrum.Spectrum;
import lombok.val;
import org.junit.runner.RunWith;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@SuppressWarnings("ClassInitializerMayBeStatic")
@RunWith(Spectrum.class)
public class EitherTest {

  {
    describe("basic methods", () -> {
      it("isLeft", () -> {
        final Either<String, String> e = Either.left("left");
        expect(e.isRight()).toBeFalse();
        expect(e.isLeft()).toBeTrue();
      });
      it("isRight", () -> {
        final Either<String, String> e = Either.right("right");
        expect(e.isRight()).toBeTrue();
        expect(e.isLeft()).toBeFalse();
      });
      it("compare", () -> {
        val seq = Seq.of(
          Either.right(10),
          Either.left(20),
          Either.left(10),
          Either.right(20)
        );
        val sorted = seq.sorted();
        expect(sorted).toEqual(Seq.of(
          Either.left(10),
          Either.left(20),
          Either.right(10),
          Either.right(20)
        ));
      });
      it("equals", () -> {
        expect(Either.left("hello")).toEqual(Either.left("hello"));
        expect(Either.right("world")).toEqual(Either.right("world"));
        expect(Either.left("hello").equals(Either.right("world"))).toBeFalse();
      });
    });
  }
}
