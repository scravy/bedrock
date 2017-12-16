package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import lombok.val;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@SuppressWarnings({"CodeBlock2Expr", "ClassInitializerMayBeStatic"})
@RunWith(Spectrum.class)
public class SeqTest {

  {
    describe("package private utility methods", () -> {
      describe("swap", () -> {
        it("should swap two elements in an array", () -> {
          val arr = new Integer[]{1, 2, 3};
          Seq.swap(arr, 0, 1);
          expect(arr[0]).toEqual(2);
          expect(arr[1]).toEqual(1);
          expect(arr[2]).toEqual(3);
        });
      });

      describe("reverse", () -> {
        it("should reverse the elements of an array", () -> {
          val arr = new Integer[]{1, 2, 3};
          Seq.reverse(arr);
          expect(arr[0]).toEqual(3);
          expect(arr[1]).toEqual(2);
          expect(arr[2]).toEqual(1);
        });
      });
    });

    describe("static methods", () -> {
      describe("empty", () -> {
        it("should produce a sequence of size 0", () -> {
          expect(Seq.empty().size()).toEqual(0);
        });
        it("should produce a sequence which yields isEmpty = true", () -> {
          expect(Seq.empty().isEmpty()).toBeTrue();
        });
      });

      describe("of", () -> {
        it("should create a sequence of size 0 when invoked with no arguments", () -> {
          expect(Seq.of().size()).toEqual(0);
        });
        it("should create a sequence that equals empty() when invoked with no arguments", () -> {
          expect(Seq.of()).toEqual(Seq.empty());
        });
      });

      describe("ofArray", () -> {
        it("should create a sequence of size 0 when invoked with an empty array", () -> {
          expect(Seq.ofArray(new Object[0]).size()).toEqual(0);
        });
        it("should create a sequence that equals empty() when invoked with an empty array", () -> {
          expect(Seq.ofArray(new Object[0])).toEqual(Seq.empty());
        });
        it("should create a sequence of an integer array", () -> {
          expect(Seq.ofArray(new Integer[]{1, 2, 3})).toEqual(Seq.of(1, 2, 3));
        });
      });

      describe("ofCollection", () -> {
        it("should create a sequence of size 0 when invoked with an empty collection", () -> {
          expect(Seq.ofCollection(Collections.emptySet()).size()).toEqual(0);
        });
        it("should create a sequence that equals empty() when invoked with an empty collection", () -> {
          expect(Seq.ofCollection(Collections.emptySet())).toEqual(Seq.empty());
        });
        it("should create a sequence from a collection", () -> {
          val seq = Seq.ofCollection(Arrays.asList(1, 2, 3));
          expect(seq.get(0)).toEqual(1);
          expect(seq.get(1)).toEqual(2);
          expect(seq.get(2)).toEqual(3);
        });
      });

      describe("ofIterable", () -> {
        it("should create a sequence of size 0 when invoked with an empty iterable", () -> {
          expect(Seq.ofIterable(Collections.emptySet()).size()).toEqual(0);
        });
        it("should create a sequence that equals empty() when invoked with an empty iterable", () -> {
          expect(Seq.ofIterable(Collections.emptySet())).toEqual(Seq.empty());
        });
        it("should create a sequence from an iterable", () -> {
          val set = new LinkedHashSet<Integer>();
          set.add(1);
          set.add(2);
          set.add(3);
          val seq = Seq.ofIterable(set);
          expect(seq.get(0)).toEqual(1);
          expect(seq.get(1)).toEqual(2);
          expect(seq.get(2)).toEqual(3);
        });
      });

      describe("ofString", () -> {
        it("should create a sequence of size 0 when invoked with an empty string", () -> {
          expect(Seq.ofString("").size()).toEqual(0);
        });
        it("should create a sequence that equals empty() when invoked with an empty string", () -> {
          expect(Seq.ofString("")).toEqual(Seq.empty());
        });
      });

      describe("codepointsOfString", () -> {
        it("should produce a sequence of Integer codepoints from a string", () -> {
          val seq = Seq.codepointsOfString("\ud83d\udca9x\ud83d\udca9");
          val expected = Seq.of(0x1F4A9, (int) 'x', 0x1F4A9);
          expect(seq).toEqual(expected);
        });
      });

      describe("concat", () -> {
        it("should concat two empty sequences", () ->
          expect(Seq.concat(Seq.empty(), Seq.empty())).toEqual(Seq.empty())
        );
      });

      describe("builder", () -> {
        it("should return a builder", () ->
          expect(Seq.builder()).toBeInstanceOf(SeqBuilder.class)
        );
        it("should return a new builder instance every time", () ->
          expect(Seq.builder() != Seq.builder()).toBeTrue()
        );
        it("should construct an empty sequence of a builder", () -> {
          expect(Seq.builder().build()).toEqual(Seq.empty());
        });
        it("should construct a sequence containing the elements added to the builder", () -> {
          val seq = Seq.<Integer>builder().add(1).add(2).add(3).result();
          expect(seq.get(0)).toEqual(1);
          expect(seq.get(1)).toEqual(2);
          expect(seq.get(2)).toEqual(3);
        });
      });

      describe("ofPair", () -> {
        it("should consutrct a list", () -> {
          val seq = Seq.<Number, Long, Double>fromPair(Pair.pair(231L, 3.4));
          expect(seq.get(0)).toEqual(231L);
          expect(seq.get(1)).toEqual(3.4);
          expect(seq.length()).toEqual(2);
        });
      });

    });
  }

}
