package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.TreeSet;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@SuppressWarnings({"ClassInitializerMayBeStatic", "CodeBlock2Expr"})
@RunWith(Spectrum.class)
public class SetTest {

  {
    describe("Set", () -> {
      it("intersect", () -> {
        expect(Set.of(1, 2, 3).intersect(Set.of(2, 3, 4))).toEqual(Set.of(2, 3));
        expect(Set.of(1, 2, 3).intersect(Set.of(2, 3, 4))).toEqual(Set.of(3, 2));
      });
      it("union", () -> {
        expect(Set.of(1, 2, 3).union(Set.of(2, 3, 4))).toEqual(Set.of(1, 2, 3, 4));
        expect(Set.of(1, 2, 3).union(Set.of(2, 3, 4))).toEqual(Set.of(4, 3, 2, 1));
        expect(Set.<Integer>empty().union(Set.of(2, 3, 4))).toEqual(Set.of(2, 3, 4));
        expect(Set.of(1, 3, 7).union(Set.empty())).toEqual(Set.of(1, 3, 7));
      });
      it("without", () -> {
        expect(Set.of(1, 2, 3).without(Set.of(2, 3, 4))).toEqual(Set.of(1));
        expect(Set.<Integer>empty().without(Set.of(2, 3, 4))).toEqual(Set.empty());
      });
      it("filter", () -> {
        expect(Set.empty().filter(__ -> false)).toEqual(Set.empty());
        expect(Set.empty().filter(__ -> true)).toEqual(Set.empty());
      });
      it("filterNot", () -> {
        expect(Set.empty().filterNot(__ -> false)).toEqual(Set.empty());
        expect(Set.empty().filterNot(__ -> true)).toEqual(Set.empty());
      });
      it("of", () -> {
        expect(Set.of() == Set.empty()).toBeTrue();
        expect(Set.of(1, 2, 3)).toEqual(Set.of(3, 3, 2, 1));
      });
      it("ofCollection", () -> {
        expect(Set.ofCollection(new TreeSet<>(Arrays.asList(1, 2, 3)))).toEqual(Set.of(1, 2, 3));
        expect(Set.ofCollection(new ArrayList<>(Arrays.asList(1, 2, 3)))).toEqual(Set.of(1, 2, 3));
        expect(Set.ofCollection(Arrays.asList(1, 2, 3))).toEqual(Set.of(1, 2, 3));
      });
      it("ofIterable", () -> {
        expect(Set.ofIterable(new TreeSet<>(Arrays.asList(1, 2, 3)))).toEqual(Set.of(1, 2, 3));
        expect(Set.ofIterable(new HashSet<>(Arrays.asList(1, 2, 3)))).toEqual(Set.of(1, 2, 3));
        expect(Set.ofIterable(Arrays.asList(1, 2, 3))).toEqual(Set.of(1, 2, 3));
      });
      it("containsAny", () -> {
        expect(Set.of(1, 2, 3).containsAny(Set.of(2, 3, 4))).toBeTrue();
        expect(Set.of(1, 2, 3).containsAny(Set.empty())).toBeFalse();
        expect(Set.of(1, 2, 3).containsAny(Set.of(1))).toBeTrue();
        expect(Set.of(1, 2, 3).containsAny(Set.of(5, 6))).toBeFalse();
        expect(Set.<Integer>empty().containsAny(Set.of(5, 6))).toBeFalse();
      });
      it("containsAll", () -> {
        expect(Set.of(1, 2, 3).containsAll(Set.of(2, 3, 4))).toBeFalse();
        expect(Set.of(1, 2, 3).containsAll(Set.empty())).toBeTrue();
        expect(Set.of(1, 2, 3).containsAll(Set.of(1, 2, 3, 4))).toBeFalse();
        expect(Set.<Integer>empty().containsAll(Set.of(5, 6))).toBeFalse();
      });
      it("forAll", () -> {
        expect(Set.of(1, 2, 3).forAll(x -> x % 2 == 0)).toBeFalse();
        expect(Set.of(1, 2, 3).forAll(x -> x > 0)).toBeTrue();
      });
      it("exists", () -> {
        expect(Set.of(1, 2, 3).exists(x -> x % 2 == 0)).toBeTrue();
        expect(Set.of(1, 2, 3).exists(x -> x < 0)).toBeFalse();
      });
      it("toSet", () -> {
        expect(Set.of(1, 2, 3).toSet()).toEqual(new HashSet<>(Arrays.asList(1, 2, 3)));
      });
      it("toSeq", () -> {
        expect(Set.of(1, 2, 3).toSeq()).toEqual(Seq.of(1, 2, 3));
      });
      it("draw", () -> {
        expect(Set.of(Set.of(1, 2, 3).draw()).containsAny(Seq.of(1, 2, 3))).toBeTrue();
      });
      it("asString", () -> {
        expect(Set.of(1, 2, 3).asString()).toEqual("123");
      });
      it("toString", () -> {
        expect(Set.of(1, 2, 3).toString()).toEqual("{1, 2, 3}");
        expect(Set.empty().toString()).toEqual("∅");
      });
    });
  }

}
