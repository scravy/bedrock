package com.simplaex.bedrock;

import lombok.experimental.UtilityClass;
import lombok.val;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@SuppressWarnings("CodeBlock2Expr")
@UtilityClass
class SeqExemplaryChecks {

  /**
   * Expects a sequence that resembles (1, 2, 2, 4, 3)
   *
   * @param seq An implementation of a sequence, containing (1, 2, 2, 4, 3)
   */
  void checks(final Seq<Integer> seq) {

    describe("length", () -> {
      it("should be 5", () -> expect(seq.length()).toEqual(5));
    });

    describe("get", () -> {
      it("should return 1 for index 0", () -> expect(seq.get(0)).toEqual(1));
      it("should return 2 for index 1", () -> expect(seq.get(1)).toEqual(2));
      it("should return 2 for index 2", () -> expect(seq.get(2)).toEqual(2));
      it("should return 4 for index 3", () -> expect(seq.get(3)).toEqual(4));
      it("should return 3 for index 4", () -> expect(seq.get(4)).toEqual(3));
      it("should throw for index 5", () -> expect(() -> seq.get(5)).toThrow(IndexOutOfBoundsException.class));
    });

    describe("equal", () -> {
      it("should equal (1, 2, 2, 4, 3)", () -> expect(seq).toEqual(Seq.of(1, 2, 2, 4, 3)));
    });

    describe("trimmedToSize", () -> {
      it("should equal itself trimmedToSize", () -> expect(seq.trimmedToSize()).toEqual(seq));
    });

    describe("count", () -> {
      it("should count 0 zero times", () -> expect(seq.count(0)).toEqual(0));
      it("should count 1 once", () -> expect(seq.count(1)).toEqual(1));
      it("should count 2 twice", () -> expect(seq.count(2)).toEqual(2));
      it("should count 3 once", () -> expect(seq.count(3)).toEqual(1));
      it("should count 4 once", () -> expect(seq.count(4)).toEqual(1));
      it("should count 5 zero times", () -> expect(seq.count(5)).toEqual(0));
    });

    describe("countBy", () -> {
      it("should count 3 elements divisble by 2", () -> expect(seq.countBy(i -> i % 2 == 0)).toEqual(3));
    });

    final Consumer<IntFunction<Seq<Integer>>> dropChecks = f -> {
      it("should return a new seq with the first element dropped", () -> expect(f.apply(1)).toEqual(Seq.of(2, 2, 4, 3)));
      it("should return a new seq with the first two elements dropped", () -> expect(f.apply(2)).toEqual(Seq.of(2, 4, 3)));
      it("should return a new seq with the first three dropped", () -> expect(f.apply(3)).toEqual(Seq.of(4, 3)));
      it("should return a new seq with the first four elements dropped", () -> expect(f.apply(4)).toEqual(Seq.of(3)));
      it("should return the empty seq when all five elements dropped", () -> expect(f.apply(5)).toEqual(Seq.empty()));
      it("should return an empty seq even when dropped more than five", () -> expect(f.apply(6)).toEqual(Seq.empty()));
    };
    describe("drop", () -> dropChecks.accept(seq::drop));
    describe("dropView", () -> dropChecks.accept(seq::dropView));

    final Consumer<IntFunction<Seq<Integer>>> takeChecks = f -> {
      it("should return an empty seq when taking zero elements", () -> expect(f.apply(0)).toEqual(Seq.empty()));
      it("should take one element", () -> expect(f.apply(1)).toEqual(Seq.of(1)));
      it("should take two elements", () -> expect(f.apply(2)).toEqual(Seq.of(1, 2)));
      it("should take three elements", () -> expect(f.apply(3)).toEqual(Seq.of(1, 2, 2)));
      it("should take four elements", () -> expect(f.apply(4)).toEqual(Seq.of(1, 2, 2, 4)));
      it("should take five elements", () -> expect(f.apply(5)).toEqual(Seq.of(1, 2, 2, 4, 3)));
      it("should take just five elements if there are no more", () -> expect(f.apply(6)).toEqual(Seq.of(1, 2, 2, 4, 3)));
    };
    describe("take", () -> takeChecks.accept(seq::take));
    describe("takeView", () -> takeChecks.accept(seq::takeView));

    describe("map", () -> {
      it("should return the same seq when passing the identify function", () -> expect(seq.map(x -> x)).toEqual(seq));
    });

    describe("flatMap", () -> {
      it("should return the same seq when passing the monadic identify function", () -> expect(seq.flatMap(Seq::of)).toEqual(seq));
    });

    describe("sorted", () -> {
      it("should sort the seq", () -> expect(seq.sorted()).toEqual(Seq.of(1, 2, 2, 3, 4)));
    });

    describe("sortedBy", () -> {
      it("should sort the seq", () -> expect(seq.sortedBy(Comparator.reverseOrder())).toEqual(Seq.of(4, 3, 2, 2, 1)));
    });

    describe("toArray", () -> {
      it("should create an array of length 5", () -> expect(seq.toArray().length).toEqual(5));
      it("should create an array with the same elements as the seq", () -> {
        val arr = seq.toArray();
        expect(arr[0]).toEqual(1);
        expect(arr[1]).toEqual(2);
        expect(arr[2]).toEqual(2);
        expect(arr[3]).toEqual(4);
        expect(arr[4]).toEqual(3);
      });
    });

    describe("toArray(Clazz)", () -> {
      it("should create an array of length 5", () -> expect(seq.toArray(Integer.class).length).toEqual(5));
      it("should create an array with the same elements as the seq", () -> {
        val arr = seq.toArray(Integer.class);
        expect(arr[0]).toEqual(1);
        expect(arr[1]).toEqual(2);
        expect(arr[2]).toEqual(2);
        expect(arr[3]).toEqual(4);
        expect(arr[4]).toEqual(3);
      });
    });

    describe("hashCode", () -> {
      it("should be usable as key in a HashMap", () -> {
        val map = new HashMap<Seq<Integer>, String>();
        map.put(seq, "quux");
        expect(map.get(seq)).toEqual("quux");
      });
    });

    describe("head", () -> {
      it("should return the first element", () -> expect(seq.head()).toEqual(1));
    });

    describe("last", () -> {
      it("should return the last element", () -> expect(seq.last()).toEqual(3));
    });

    final Consumer<Supplier<Seq<Integer>>> tailChecks = f -> {
      it("should return the last four elements", () -> expect(f.get()).toEqual(Seq.of(2, 2, 4, 3)));
    };
    describe("tail", () -> tailChecks.accept(seq::tail));
    describe("tailView", () -> tailChecks.accept(seq::tailView));

    final Consumer<Supplier<Seq<Integer>>> initChecks = f -> {
      it("should return the first four elements", () -> expect(f.get()).toEqual(Seq.of(1, 2, 2, 4)));
    };
    describe("init", () -> initChecks.accept(seq::init));
    describe("initView", () -> initChecks.accept(seq::initView));

    final Consumer<Supplier<Seq<Seq<Integer>>>> initsChecks = f -> {
      it("should return all the inits", () -> {
        expect(f.get()).toEqual(Seq.of(
          Seq.of(1),
          Seq.of(1, 2),
          Seq.of(1, 2, 2),
          Seq.of(1, 2, 2, 4),
          Seq.of(1, 2, 2, 4, 3)
        ));
      });
    };
    describe("inits", () -> initsChecks.accept(seq::inits));
    describe("initsView", () -> initsChecks.accept(seq::initsView));

    final Consumer<Supplier<Seq<Seq<Integer>>>> tailsChecks = f -> {
      it("should return all the tails", () -> {
        expect(f.get()).toEqual(Seq.of(
          Seq.of(3),
          Seq.of(4, 3),
          Seq.of(2, 4, 3),
          Seq.of(2, 2, 4, 3),
          Seq.of(1, 2, 2, 4, 3)
        ));
      });
    };
    describe("tails", () -> tailsChecks.accept(seq::tails));
    describe("tailsView", () -> tailsChecks.accept(seq::tailsView));

    describe("contains", () -> {
      it("should confirm that 1 is part of the seq", () -> expect(seq.contains(1)).toBeTrue());
      it("should confirm that 2 is part of the seq", () -> expect(seq.contains(2)).toBeTrue());
      it("should confirm that 3 is part of the seq", () -> expect(seq.contains(3)).toBeTrue());
      it("should confirm that 4 is part of the seq", () -> expect(seq.contains(4)).toBeTrue());
      it("should confirm that 0 is not part of the seq", () -> expect(seq.contains(0)).toBeFalse());
    });

    describe("exists", () -> {
      it("should confirm that there are numbers greater than 0", () -> expect(seq.exists(x -> x > 0)).toBeTrue());
      it("should confirm that there are no numbers less than 0", () -> expect(seq.exists(x -> x < 0)).toBeFalse());
    });

    describe("shuffled", () -> {
      it("should shuffle given a random generator", () -> expect(seq.shuffled(new Random(1337))).toEqual(Seq.of(2, 3, 4, 1, 2)));
    });
  }

}
