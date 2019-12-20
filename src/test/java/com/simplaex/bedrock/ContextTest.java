package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import org.junit.runner.RunWith;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;
import static com.simplaex.bedrock.Context.withContext;
import static com.simplaex.bedrock.Pair.pair;

@SuppressWarnings("ClassInitializerMayBeStatic")
@RunWith(Spectrum.class)
public class ContextTest {
  {
    describe(Context.class.getSimpleName(), () -> {
      it("should retrieve the correct context", () -> {
        withContext(ArrayMap.of(pair("foo", "yes"), pair("bar", "no")), () -> {
          expect(Context.get("foo")).toEqual("yes");
          expect(Context.get("bar")).toEqual("no");
          withContext(ArrayMap.of(pair("bar", "yes")), () -> {
            expect(Context.get("foo")).toEqual("yes");
            expect(Context.get("bar")).toEqual("yes");
          });
          expect(Context.get("foo")).toEqual("yes");
          expect(Context.get("bar")).toEqual("no");
          withContext(ArrayMap.of(pair("foo", "no")), () -> {
            expect(Context.get("foo")).toEqual("no");
            expect(Context.get("bar")).toEqual("no");
          });
          expect(Context.get("foo")).toEqual("yes");
          expect(Context.get("bar")).toEqual("no");
        });
      });
    });
  }
}
