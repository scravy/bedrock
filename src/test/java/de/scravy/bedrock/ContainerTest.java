package de.scravy.bedrock;

import com.greghaskins.spectrum.Spectrum;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;
import static de.scravy.bedrock.Control.times;

@RunWith(Spectrum.class)
@SuppressWarnings({"ClassInitializerMayBeStatic", "CodeBlock2Expr"})
public class ContainerTest {
  {
    describe("Container", () -> {
      final Container<Integer> container = Container.fromIterable(Seq.of(1, 1, 2, 3, 5, 8));
      it("asString(delimiter)", () -> {
        expect(container.asString("|")).toEqual("1|1|2|3|5|8");
      });
      it("toList()", () -> {
        final List<Integer> list = container.toList();
        expect(list).toEqual(Arrays.asList(1, 1, 2, 3, 5, 8));
      });
      it("stream()", () -> {
        final Stream<Integer> stream = container.stream();
        expect(stream.count()).toEqual(6);
      });
      it("draw()", () -> {
        times(12, ignore -> {
          expect(container.contains(container.draw())).toBeTrue();
        });
        expect(Container.fromIterable(Collections.emptyList())::draw).toThrow(NoSuchElementException.class);
      });
      it("forAll", () -> {
        expect(container.forAll(e -> e > 0)).toBeTrue();
        expect(container.forAll(e -> e % 2 == 0)).toBeFalse();
        expect(container.forAll(e -> e % 2 != 0)).toBeFalse();
      });
      it("exists", () -> {
        expect(container.exists(e -> e > 3)).toBeTrue();
        expect(container.exists(e -> e % 2 == 0)).toBeTrue();
        expect(container.exists(e -> e % 2 != 0)).toBeTrue();
        expect(container.exists(e -> e > 10)).toBeFalse();
      });
      it("isEmpty", () -> {
        expect(container.isEmpty()).toBeFalse();
        expect(Container.fromIterable(Collections.emptyList()).isEmpty()).toBeTrue();
      });
      it("nonEmpty", () -> {
        expect(container.nonEmpty()).toBeTrue();
        expect(Container.fromIterable(Collections.emptyList()).nonEmpty()).toBeFalse();
      });
    });
  }

}
