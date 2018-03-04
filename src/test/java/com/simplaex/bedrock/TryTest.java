package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import lombok.val;
import org.junit.runner.RunWith;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Predicate;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@SuppressWarnings({"CodeBlock2Expr", "ClassInitializerMayBeStatic"})
@RunWith(Spectrum.class)
public class TryTest {

  {
    describe("execute", () -> {
      it("should catch an exception and turn it into a Try.Failure", () -> {
        val r = Try.execute(() -> {
          throw new RuntimeException("dafuq");
        });
        expect(r).toBeInstanceOf(Try.Failure.class);
        expect(r.isFailure()).toBeTrue();
        expect(r.isSuccess()).toBeFalse();
        expect(r::orElseThrow).toThrow(Try.RethrownException.class);
      });
      it("should turn a value into a Try.Success", () -> {
        val r = Try.execute(() -> 3);
        expect(r).toBeInstanceOf(Try.Success.class);
        expect(r.isFailure()).toBeFalse();
        expect(r.isSuccess()).toBeTrue();
        expect(r.orElseThrow()).toEqual(3);
      });
    });

    describe("filter", () -> {
      it("should keep values that satisfy the predicate", () -> {
        expect(Try.success(7).filter(x -> x % 2 == 1)).toEqual(Try.success(7));
      });
      it("should drop values with an exception that do not satisfy the predicate", () -> {
        final Predicate<Integer> predicate = x -> x % 2 == 0;
        expect(Try.success(7).filter(predicate)).toEqual(Try.failure(new ValueDidNotSatisfyPredicateException(predicate, 7)));
      });
    });

    describe("Success", () -> {
      val t = Try.success(4711);
      describe("recoverWith", () -> {

      });
      describe("recover", () -> {

      });
      describe("orElse", () -> {

      });
      describe("orElseNull", () -> {

      });
      describe("orElseGet", () -> {

      });
      describe("orElseThrow", () -> {

      });
      describe("orElseThrowRuntime", () -> {

      });
      describe("fallback", () -> {
        it("should keep the value", () -> {
          expect(t.fallback(42)).toEqual(Try.success(4711));
        });
      });
      describe("isFailure", () -> {
        it("should return false", () -> {
          expect(t.isFailure()).toBeFalse();
        });
      });
      describe("isSuccess", () -> {
        it("should return true", () -> {
          expect(t.isSuccess()).toBeTrue();
        });
      });
      describe("map", () -> {

      });
      describe("flatMap", () -> {

      });
      describe("otherwise", () -> {

      });
      describe("transform", () -> {

      });
      describe("transformWith", () -> {

      });
      describe("toOptional", () -> {
        it("should yield an empty optional", () -> {
          expect(t.toOptional()).toEqual(Optional.of(4711));
        });
      });
      describe("get", () -> {
        it("should return the value", () -> {
          expect(t.get()).toEqual(4711);
        });
      });
    });

    describe("Failure", () -> {
      val t = Try.failure(new IndexOutOfBoundsException());
      describe("recoverWith", () -> {

      });
      describe("recover", () -> {

      });
      describe("orElse", () -> {

      });
      describe("orElseNull", () -> {

      });
      describe("orElseGet", () -> {

      });
      describe("orElseThrow", () -> {

      });
      describe("orElseThrowRuntime", () -> {

      });
      describe("fallback", () -> {
        it("should fallback to the fallback value", () -> {
          expect(t.fallback(42)).toEqual(Try.success(42));
        });
      });
      describe("isFailure", () -> {
        it("should return true", () -> {
          expect(t.isFailure()).toBeTrue();
        });
      });
      describe("isSuccess", () -> {
        it("should return false", () -> {
          expect(t.isSuccess()).toBeFalse();
        });
      });
      describe("map", () -> {

      });
      describe("flatMap", () -> {

      });
      describe("otherwise", () -> {

      });
      describe("transform", () -> {

      });
      describe("transformWith", () -> {

      });
      describe("toOptional", () -> {
        it("should yield an empty optional", () -> {
          expect(t.toOptional()).toEqual(Optional.empty());
        });
      });
      describe("get", () -> {
        it("should throw a NoSuchElementException", () -> {
          expect(t::get).toThrow(NoSuchElementException.class);
        });
      });
    });
  }
}
