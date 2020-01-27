package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import org.junit.runner.RunWith;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;
import static com.simplaex.bedrock.Cons.cons;
import static com.simplaex.bedrock.Cons.empty;

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
    });
  }
}
