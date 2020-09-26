package de.scravy.bedrock;

import com.greghaskins.spectrum.Spectrum;
import org.junit.runner.RunWith;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@RunWith(Spectrum.class)
public class BuilderTest {

  {
    describe("Builder", () -> {
      class SomeBuilder implements Builder<Character, String> {
        private final List<Character> xs = new ArrayList<>();

        @Nonnull
        @Override
        public String result() {
          final StringBuilder sb = new StringBuilder();
          xs.forEach(sb::append);
          return sb.toString();
        }

        @Override
        public Builder<Character, String> add(@Nonnull final Character elem) {
          xs.add(elem);
          return this;
        }

        @Override
        public Iterator<Character> iterator() {
          return xs.iterator();
        }
      }
      ;
      it("accumulator", () -> {
        final SomeBuilder b = new SomeBuilder();
        b.accumulator().accept(b, 'c');
        expect(b.result()).toEqual("c");
      });
      it("combiner", () -> {
        final SomeBuilder b1 = new SomeBuilder();
        final SomeBuilder b2 = new SomeBuilder();
        b1.add('a');
        b2.add('b');
        final Builder<Character, String> b = b1.combiner().apply(b1, b2);
        expect(b.result()).toEqual("ab");
      });
    });
  }

}
