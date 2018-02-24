package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import lombok.val;
import org.junit.runner.RunWith;

import java.time.Duration;
import java.util.concurrent.Executors;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@RunWith(Spectrum.class)
public class PromiseTest {

  {
    describe("Promise", () -> {
      it("fulfill", () -> {
        val x = Executors.newSingleThreadExecutor();
        val p = Promise.<Integer>promise();
        x.submit(() -> {
          Control.sleep(Duration.ofMillis(200));
          p.fulfill(7);
        });
        expect(p.get()).toEqual(7);
      });
      it("fail", () -> {
        val x = Executors.newSingleThreadExecutor();
        val p = Promise.<Integer>promise();
        x.submit(() -> {
          Control.sleep(Duration.ofMillis(200));
          p.fail(new IndexOutOfBoundsException());
        });
        expect(p::get).toThrow(IndexOutOfBoundsException.class);
      });
    });
  }

}
