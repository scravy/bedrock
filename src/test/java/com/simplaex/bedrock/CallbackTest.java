package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import org.junit.runner.RunWith;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@RunWith(Spectrum.class)
public class CallbackTest {

  {
    describe("Callback", () -> {
      class Thing {
        public String result = null;
        public Exception exc = null;

        void process(final Callback<String> callback) {
          if (exc != null) {
            callback.fail(exc);
          } else {
            callback.success(result);
          }
        }
      }
      final Function3<Thing, Box<String>, Box<Object>, Callback<String>> callbackFactory = (t, r1, r2) -> (error, result) -> {
        if (error != null) {
          r2.accept(error);
          return;
        }
        r1.accept(result);
      };
      it("success", () -> {
        final Thing t = new Thing();
        t.result = "foo";
        final Box<String> r = Box.box();
        final Box<Object> e = Box.box();
        t.process(callbackFactory.apply(t, r, e));
        expect(r.contains("foo")).toBeTrue();
      });
      it("fail", () -> {
        final Thing t = new Thing();
        t.exc = new ArrayIndexOutOfBoundsException();
        final Box<String> r = Box.box();
        final Box<Object> e = Box.box();
        t.process(callbackFactory.apply(t, r, e));
        expect(e.contains(t.exc)).toBeTrue();
        expect(r.contains(null)).toBeTrue();
      });
      it("after", () -> {
        final Thing t = new Thing();
        t.result = "foo";
        final Box<String> r = Box.box();
        final Box<Object> e = Box.box();
        final Callback<String> c = callbackFactory.apply(t, r, e);
        final Box<String> track = Box.box();
        t.process(c.after(something -> {
          track.accept(something);
          return something + "!";
        }));
        expect(track.get()).toEqual("foo");
        expect(r.get()).toEqual("foo!");
      });
      it("after (throwing exception)", () -> {
        final Thing t = new Thing();
        t.result = "foo";
        final Box<String> r = Box.box();
        final Box<Object> e = Box.box();
        final Callback<String> c = callbackFactory.apply(t, r, e);
        final Box<String> track = Box.box();
        t.process(c.after(something -> {
          throw new RuntimeException();
        }));
        expect(e.get().getClass()).toEqual(RuntimeException.class);
      });
    });
  }

}
