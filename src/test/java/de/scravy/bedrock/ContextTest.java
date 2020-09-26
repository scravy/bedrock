package de.scravy.bedrock;

import com.greghaskins.spectrum.Spectrum;
import org.junit.runner.RunWith;

import java.util.Optional;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;
import static de.scravy.bedrock.Context.withContext;
import static de.scravy.bedrock.Pair.pair;

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
      it("should throw if there is no context currently", () -> {
        expect(() -> Context.get("something")).toThrow(IllegalStateException.class);
        expect(Context::getInstance).toThrow(IllegalStateException.class);
      });
      it("should return null if context does not have key", () -> {
        Context.withContext(ArrayMap.empty(), () -> expect(Context.get("something")).toBeNull());
      });
      it("should return object iff class matches", () -> {
        Context.withContext(ArrayMap.of(pair("foo", 3)), () -> {
          expect(Context.get("foo", Integer.class)).toEqual(3);
          expect(() -> Context.get("foo", Long.class)).toThrow(IllegalArgumentException.class);
          expect(() -> Context.get("bar", Long.class)).toThrow(IllegalStateException.class);
          expect(Context.getOptionally("foo", Integer.class)).toEqual(Optional.of(3));
          expect(Context.getOptionally("foo", Long.class)).toEqual(Optional.empty());
          expect(Context.getOptionally("bar", Long.class)).toEqual(Optional.empty());
        });
      });
    });
  }
}
