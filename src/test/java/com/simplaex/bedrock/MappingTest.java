package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import lombok.val;
import org.junit.runner.RunWith;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Optional;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@SuppressWarnings({"ClassInitializerMayBeStatic", "CodeBlock2Expr"})
@RunWith(Spectrum.class)
public class MappingTest {

  {
    describe("default methods", () -> {

      val seq = Seq.of("zero", "one", "two");
      val mapping = new Mapping<String, Character>() {

        @Nonnull
        @Override
        public Optional<Character> get(final String key) {
          return seq.findFirst(s -> s.equals(key)).map(s -> s.charAt(0));
        }

        @Override
        public Seq<String> keys() {
          return seq;
        }
      };

      describe("values", () -> {
        it("should return a Seq of values", () -> {
          expect(mapping.values()).toEqual(Seq.ofString("zot"));
        });
      });

      describe("getOrElse", () -> {
        it("should get the value if key exists", () -> {
          expect(mapping.getOrElse("zero", 'q')).toEqual('z');
        });
        it("should fall back to second argument if key does not exist", () -> {
          expect(mapping.getOrElse("three", 'q')).toEqual('q');
        });
      });

      describe("iterator()", () -> {
        it("should create a Seq from the Iterable", () -> {
          expect(Seq.ofIterable(mapping).sortedOn(Pair::fst)).toEqual(Seq.of(
            Pair.of("one", 'o'),
            Pair.of("two", 't'),
            Pair.of("zero", 'z')
          ));
        });
      });
    });

    describe("wrap", () -> {

      val map = new HashMap<String, Number>();
      map.put("zero", BigDecimal.ZERO);
      map.put("one", BigDecimal.ONE);
      val mapping = Mapping.wrap(map);

      it("should create a Map for which get() works", () -> {
        expect(mapping.get("zero")).toEqual(Optional.of(BigDecimal.ZERO));
        expect(mapping.get("one")).toEqual(Optional.of(BigDecimal.ONE));
        expect(mapping.get("two")).toEqual(Optional.empty());
      });

      it("should create a Map for which keys() works", () -> {
        expect(mapping.keys().sorted()).toEqual(Seq.of("one", "zero"));
      });

      it("should create a Map for which values() works", () -> {
        expect(mapping.values().sorted()).toEqual(Seq.of(BigDecimal.ZERO, BigDecimal.ONE));
      });

      it("should create a Map for which keys() works when called twice", () -> {
        expect(mapping.keys().sorted()).toEqual(Seq.of("one", "zero"));
        expect(mapping.keys().sorted()).toEqual(Seq.of("one", "zero"));
      });

      it("should create a Map for which values() works when called twice", () -> {
        expect(mapping.values().sorted()).toEqual(Seq.of(BigDecimal.ZERO, BigDecimal.ONE));
        expect(mapping.values().sorted()).toEqual(Seq.of(BigDecimal.ZERO, BigDecimal.ONE));
      });

      it("should create a Map for which toMap return the original Map", () -> {
        expect(mapping.toMap() == map).toBeTrue();
      });

      it("should create a Seq from the iterator", () -> {
        expect(Seq.ofIterable(mapping).sortedOn(Pair::fst)).toEqual(Seq.of(
          Pair.of("one", BigDecimal.ONE),
          Pair.of("zero", BigDecimal.ZERO)
        ));
      });
    });
  }
}
