package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import lombok.val;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@SuppressWarnings({"CodeBlock2Expr", "ClassInitializerMayBeStatic"})
@RunWith(Spectrum.class)
public class SeqTest {

  {
    describe("package private utility methods", () -> {
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

      describe("concatView", () -> {
        it("should create a view of the underlying seqs", () -> {
          val s1 = Seq.of(1, 2, 3);
          val s2 = Seq.of(4, 5, 6);
          val s3 = Seq.of(7, 8, 9);
          val s = Seq.concatView(s1, s2, s3);
          expect(s).toEqual(Seq.rangeInclusive(1, 9));
        });
      });

      describe("concat + concatView", () -> {
        it("1", () -> {
          val s1 = Seq.concat();
          val s2 = Seq.concatView();
          expect(s1).toEqual(s2);
          expect(s2).toEqual(s1);
        });
        it("2", () -> {
          val s1 = Seq.concat(Seq.empty());
          val s2 = Seq.concatView(Seq.empty());
          expect(s1).toEqual(s2);
          expect(s2).toEqual(s1);
        });
        it("3", () -> {
          val s1 = Seq.concat(Seq.empty(), Seq.empty());
          val s2 = Seq.concatView(Seq.empty(), Seq.empty());
          expect(s1).toEqual(s2);
          expect(s2).toEqual(s1);
        });
        it("4", () -> {
          val s1 = Seq.concat(Seq.of(1), Seq.empty());
          val s2 = Seq.concatView(Seq.of(1), Seq.empty());
          expect(s1).toEqual(s2);
          expect(s2).toEqual(s1);
        });
        it("5", () -> {
          val s1 = Seq.concat(Seq.of(1, 2), Seq.empty());
          val s2 = Seq.concatView(Seq.of(1, 2), Seq.empty());
          expect(s1).toEqual(s2);
          expect(s2).toEqual(s1);
        });
        it("6", () -> {
          val s1 = Seq.concat(Seq.of(1, 2), Seq.of(3));
          val s2 = Seq.concatView(Seq.of(1, 2), Seq.of(3));
          expect(s1).toEqual(s2);
          expect(s2).toEqual(s1);
        });
        it("7", () -> {
          val s1 = Seq.concat(Seq.of(1), Seq.of(3, 4));
          val s2 = Seq.concatView(Seq.of(1), Seq.of(3, 4));
          expect(s1).toEqual(s2);
          expect(s2).toEqual(s1);
        });
        it("8", () -> {
          val s1 = Seq.concat(Seq.of(1), Seq.of(3));
          val s2 = Seq.concatView(Seq.of(1), Seq.of(3));
          expect(s1).toEqual(s2);
          expect(s2).toEqual(s1);
        });
        it("9", () -> {
          val s1 = Seq.concat(Seq.of(1, 2), Seq.of(3, 4));
          val s2 = Seq.concatView(Seq.of(1, 2), Seq.of(3, 4));
          expect(s1).toEqual(s2);
          expect(s2).toEqual(s1);
        });
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
          val seq = Seq.<Number, Long, Double>ofPair(Pair.pair(231L, 3.4));
          expect(seq.get(0)).toEqual(231L);
          expect(seq.get(1)).toEqual(3.4);
          expect(seq.length()).toEqual(2);
        });
      });

    });

    describe("sequences with null values", () -> {
      it("should compare sequences with null values using equals", () -> {
        expect(Seq.of(1, null)).toEqual(Seq.of(1, null));
        expect(Seq.of(null, 1).equals(Seq.of(1, null))).toBeFalse();
        expect(Seq.of(1, null).equals(Seq.of(null, 1))).toBeFalse();
      });
      it("should calculate hashCodes for sequences involving null", () -> {
        val m = new HashSet<Seq<Integer>>();
        m.add(Seq.of(1, null));
        expect(m.contains(Seq.of(1, null))).toBeTrue();
      });
    });

    describe("rangeInclusive", () -> {
      it("should create a range", () -> {
        expect(Seq.rangeInclusive(0, 5)).toEqual(Seq.of(0, 1, 2, 3, 4, 5));
      });
      it("should create a range starting at a negative value", () -> {
        expect(Seq.rangeInclusive(-2, 3)).toEqual(Seq.of(-2, -1, 0, 1, 2, 3));
      });
      it("should create a descending range", () -> {
        expect(Seq.rangeInclusive(7, -3)).toEqual(Seq.of(7, 6, 5, 4, 3, 2, 1, 0, -1, -2, -3));
      });
    });

    describe("rangeExclusive", () -> {
      it("should create a range", () -> {
        expect(Seq.rangeExclusive(0, 5)).toEqual(Seq.of(0, 1, 2, 3, 4));
      });
      it("should create an empty range when from == to", () -> {
        expect(Seq.rangeExclusive(5, 5)).toEqual(Seq.empty());
      });
      it("should create a range starting at a negative value", () -> {
        expect(Seq.rangeExclusive(-2, 3)).toEqual(Seq.of(-2, -1, 0, 1, 2));
      });
      it("should create a descending range", () -> {
        expect(Seq.rangeExclusive(7, -3)).toEqual(Seq.of(7, 6, 5, 4, 3, 2, 1, 0, -1, -2));
      });
    });

    describe("and", () -> {
      it("should apply and", () -> {
        expect(Seq.and(Seq.of(true, true, true))).toBeTrue();
        expect(Seq.and(Seq.of(true, false, true))).toBeFalse();
        expect(Seq.and(Seq.of(false, false, true))).toBeFalse();
        expect(Seq.and(Seq.of(false, false, false))).toBeFalse();
      });
    });

    describe("or", () -> {
      it("should apply or", () -> {
        expect(Seq.or(Seq.of(true, true, true))).toBeTrue();
        expect(Seq.or(Seq.of(true, false, true))).toBeTrue();
        expect(Seq.or(Seq.of(false, false, true))).toBeTrue();
        expect(Seq.or(Seq.of(false, false, false))).toBeFalse();
      });
    });

    describe("commonPrefix", () -> {
      it("should find a common prefix", () -> {
        expect(Seq.commonPrefix(Seq.ofString("hello"), Seq.ofString("world"))).toEqual(Seq.empty());
        expect(Seq.commonPrefix(Seq.ofString("hello"), Seq.ofString("hell"))).toEqual(Seq.ofString("hell"));
        expect(Seq.commonPrefix(Seq.ofString("hello"), Seq.ofString("help"))).toEqual(Seq.ofString("hel"));
      });
    });
  }

}
