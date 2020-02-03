package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import org.junit.runner.RunWith;

import java.util.NoSuchElementException;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;
import static com.simplaex.bedrock.Cons.*;

@RunWith(Spectrum.class)
@SuppressWarnings("ClassInitializerMayBeStatic")
public class ConsTest {
  {
    describe("Cons", () -> {
      final Cons<Integer> cons = cons(1, cons(1, cons(2, cons(3, cons(5, cons(8, empty()))))));
      it("equals", () -> {
        //noinspection EqualsWithItself
        expect(cons.equals(cons)).toBeTrue();
      });
      it("toString", () -> {
        expect(empty().toString()).toEqual("(<empty>)");
        expect(cons.toString()).toEqual("(1,(1,(2,(3,(5,(8,(<empty>)))))))");
      });
      it("isEmpty", () -> {
        expect(empty().isEmpty()).toBeTrue();
        expect(empty().nonEmpty()).toBeFalse();
        expect(cons.isEmpty()).toBeFalse();
        expect(cons.nonEmpty()).toBeTrue();
      });
      it("iterator", () -> {
        expect(Seq.ofIterator(cons.iterator())).toEqual(Seq.of(1, 1, 2, 3, 5, 8));
      });
      it("empty iterator", () -> {
        expect(empty().iterator()::next).toThrow(EmptyIteratorException.class);
      });
      it("singleton", () -> {
        final Cons<String> s = singleton("string");
        expect(s.nonEmpty()).toBeTrue();
        expect(s.iterator().next()).toEqual("string");
        expect(s.lengthAtLeast(0)).toBeTrue();
        expect(s.lengthAtLeast(1)).toBeTrue();
        expect(s.lengthAtLeast(2)).toBeFalse();
      });
      it("head on empty cons", () -> {
        expect(empty()::head).toThrow(NoSuchElementException.class);
      });
      it("tail on empty cons", () -> {
        expect(empty()::tail).toThrow(NoSuchElementException.class);
      });
      it("equals(empty(), empty())", () -> {
        final Cons<String> s1 = empty();
        final Cons<String> s2 = empty();
        expect(s1).toEqual(s2);
      });
    });
  }
}
