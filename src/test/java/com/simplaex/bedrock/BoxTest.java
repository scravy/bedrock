package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import lombok.val;
import org.junit.runner.RunWith;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@SuppressWarnings({"CodeBlock2Expr", "ClassInitializerMayBeStatic"})
@RunWith(Spectrum.class)
public class BoxTest {

  {
    describe("IntBox", () -> {
      it("inc / dec / get / add / sub", () -> {
        val b = Box.intBox();
        expect(b.get()).toEqual(0);
        b.inc();
        expect(b.get()).toEqual(1);
        b.dec();
        expect(b.get()).toEqual(0);
        b.update(x -> x + 10);
        expect(b.get()).toEqual(10);
        b.sub(5);
        expect(b.get()).toEqual(5);
        b.add(10);
        expect(b.get()).toEqual(15);
      });
    });
    describe("LongBox", () -> {
      it("inc / dec / get / add / sub", () -> {
        val b = Box.longBox();
        expect(b.get()).toEqual(0L);
        b.inc();
        expect(b.get()).toEqual(1L);
        b.dec();
        expect(b.get()).toEqual(0L);
        b.update(x -> x + 10);
        expect(b.get()).toEqual(10L);
        b.sub(5);
        expect(b.get()).toEqual(5L);
        b.add(10);
        expect(b.get()).toEqual(15L);
      });
    });
    describe("DoubleBox", () -> {
      it("inc / dec / get / add / sub", () -> {
        val b = Box.doubleBox();
        expect(b.get()).toEqual(0.0);
        b.inc();
        expect(b.get()).toEqual(1.0);
        b.dec();
        expect(b.get()).toEqual(0.0);
        b.update(x -> x + 10);
        expect(b.get()).toEqual(10.0);
        b.sub(5);
        expect(b.get()).toEqual(5.0);
        b.add(10);
        expect(b.get()).toEqual(15.0);
      });
    });
  }

}
