package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import lombok.val;
import org.junit.runner.RunWith;

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
      });      it("should turn a value into a Try.Success", () -> {
        val r = Try.execute(() -> 3);
        expect(r).toBeInstanceOf(Try.Success.class);
        expect(r.isFailure()).toBeFalse();
        expect(r.isSuccess()).toBeTrue();
        expect(r.orElseThrow()).toEqual(3);
      });
    });
  }
}
