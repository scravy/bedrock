package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import lombok.val;
import org.junit.runner.RunWith;

import java.util.Optional;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@RunWith(Spectrum.class)
public class MultiValuedKeyMapTest {

  {
    describe("building + querying", () -> {
      it("query values directly", () -> {
        val tree = MultiValuedKeyMap.<String>builder()
          .add(Seq.of("USA", 'm'), "trump")
          .add(Seq.of("USA", 'f'), "clinton")
          .add(Seq.of("DEU", 'm'), "schulz")
          .add(Seq.of("DEU", 'f'), "merkel")
          .build();
        expect(tree.get(Seq.of("USA", 'm'))).toEqual(Optional.of("trump"));
        expect(tree.get(Seq.of("USA", 'f'))).toEqual(Optional.of("clinton"));
        expect(tree.get(Seq.of("DEU", 'm'))).toEqual(Optional.of("schulz"));
        expect(tree.get(Seq.of("DEU", 'f'))).toEqual(Optional.of("merkel"));
      });
      it("fallback for first dimension", () -> {
        val tree = MultiValuedKeyMap.<String>builder()
          .add(Seq.of("USA", 'm'), "trump")
          .add(Seq.of("USA", 'f'), "clinton")
          .add(Seq.of(null, null), "our-new-ai-overlords")
          .build();
        expect(tree.get(Seq.of("FRA", 'm'))).toEqual(Optional.of("our-new-ai-overlords"));
      });
      it("fallback for second dimension", () -> {
        val tree = MultiValuedKeyMap.<String>builder()
          .add(Seq.of("USA", 'm'), "trump")
          .add(Seq.of("USA", 'f'), "clinton")
          .add(Seq.of("USA", null), "anarchy")
          .build();
        expect(tree.get(Seq.of("USA", 'o'))).toEqual(Optional.of("anarchy"));
      });
      it("fallback for first + second dimension", () -> {
        val tree = MultiValuedKeyMap.<String>builder()
          .add(Seq.of("USA", 'm'), "trump")
          .add(Seq.of("USA", 'f'), "clinton")
          .add(Seq.of("USA", null), "anarchy")
          .add(Seq.of(null, null), "our-new-ai-overlords")
          .build();
        expect(tree.get(Seq.of("USA", 'o'))).toEqual(Optional.of("anarchy"));
        expect(tree.get(Seq.of("FRA", 'o'))).toEqual(Optional.of("our-new-ai-overlords"));
        expect(tree.get(Seq.of("USA", null))).toEqual(Optional.of("anarchy"));
        expect(tree.get(Seq.of(null, null))).toEqual(Optional.of("our-new-ai-overlords"));
      });
    });
  }

}
