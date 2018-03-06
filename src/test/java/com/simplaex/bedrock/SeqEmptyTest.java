package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import org.junit.runner.RunWith;

import java.util.Optional;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@SuppressWarnings({"CodeBlock2Expr", "ClassInitializerMayBeStatic"})
@RunWith(Spectrum.class)
public class SeqEmptyTest {

  {
    describe("an empty seq (property checks)", () -> {
      SeqPropertyChecks.checks(Seq.empty());
    });

    describe("get", () -> {
      it("should produce an empty array", () -> {
        expect(() -> Seq.empty().get(0)).toThrow(IndexOutOfBoundsException.class);
      });
    });

    describe("toArray", () -> {
      it("should produce an empty array", () -> {
        expect(Seq.empty().toArray().length).toEqual(0);
      });
    });

    describe("toArrayMap", () -> {
      it("should produce an empty ArrayMap", () -> {
        expect(Seq.empty().toArrayMap(Object::hashCode)).toEqual(ArrayMap.empty());
      });
    });

    describe("toMap", () -> {
      it("should produce an empty array", () -> {
        expect(Seq.empty().toMap(Object::hashCode)).toEqual(Mapping.empty());
      });
    });

    describe("sortedBy", () -> {
      it("should return an empty seq", () -> {
        expect(Seq.<Long>empty().sortedBy(Long::compare)).toEqual(Seq.empty());
      });
    });

    describe("exists", () -> {
      it("should return false", () -> {
        expect(Seq.<Integer>empty().exists(i -> i % 2 == 0)).toBeFalse();
      });
    });

    describe("forAll", () -> {
      it("should return true", () -> {
        expect(Seq.<Integer>empty().forAll(i -> i % 2 == 0)).toBeTrue();
      });
    });

    describe("trimmedToSize", () -> {
      it("should return itself", () -> {
        expect(Seq.empty().trimmedToSize() == Seq.empty()).toBeTrue();
      });
    });

    describe("inits", () -> {
      it("should return itself", () -> {
        //noinspection RedundantCast
        expect(((Object)Seq.empty().inits()) == ((Object)Seq.empty())).toBeTrue();
      });
    });

    describe("tails", () -> {
      it("should return itself", () -> {
        //noinspection RedundantCast
        expect(((Object)Seq.empty().tails()) == ((Object)Seq.empty())).toBeTrue();
      });
    });

    describe("count", () -> {
      it("should return zero", () -> {
        expect(Seq.<Integer>empty().count(0)).toEqual(0);
      });
    });

    describe("countBy", () -> {
      it("should return zero", () -> {
        expect(Seq.<Integer>empty().countBy(i -> i % 2 == 0)).toEqual(0);
      });
    });

    describe("headOptional", () -> {
      it("should return empty", () -> {
        expect(Seq.empty().headOptional()).toEqual(Optional.empty());
      });
    });

    describe("lastOptional", () -> {
      it("should return empty", () -> {
        expect(Seq.empty().lastOptional()).toEqual(Optional.empty());
      });
    });
  }

}
