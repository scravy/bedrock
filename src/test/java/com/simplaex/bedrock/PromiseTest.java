package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import lombok.val;
import org.junit.runner.RunWith;

import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@SuppressWarnings({"CodeBlock2Expr", "ClassInitializerMayBeStatic"})
@RunWith(Spectrum.class)
public class PromiseTest {

  {
    describe("Promise", () -> {
      it("fulfill", () -> {
        val x = Executors.newSingleThreadExecutor();
        val p = Promise.<Integer>promise();
        val s = new Semaphore(0);
        x.submit(() -> {
          s.acquireUninterruptibly();
          p.fulfill(7);
        });
        s.release();
        expect(p.get()).toEqual(7);
      });
      it("fail", () -> {
        val x = Executors.newSingleThreadExecutor();
        val p = Promise.<Integer>promise();
        val s = new Semaphore(0);
        x.submit(() -> {
          s.acquireUninterruptibly();
          p.fail(new IndexOutOfBoundsException());
        });
        s.release();
        expect(p::get).toThrow(IndexOutOfBoundsException.class);
      });
    });

    describe("mapping promises", () -> {
      describe("when not fulfilled yet", () -> {
        it("should apply the mapped value", () -> {
          val x = Executors.newSingleThreadExecutor();
          val p = Promise.<Integer>promise();
          val p2 = p.map(i -> i * 2);
          val s = new Semaphore(0);
          x.submit(() -> {
            s.acquireUninterruptibly();
            p.fulfill(13);
          });
          s.release();
          expect(p2.get()).toEqual(26);
        });
      });
      describe("when already fulfilled", () -> {
        it("should return a FulfilledPromise", () -> {
          val p = Promise.<Integer>promise();
          p.fulfill(10);
          val p2 = p.map(i -> i * 2).map(i -> i * 2);
          expect(p2.get()).toEqual(40);
        });
        it("should return a FailedPromise if the mapping function fails", () -> {
          val p = Promise.<Integer>promise();
          p.fulfill(10);
          val p2 = p.map(i -> i - 10).map(i -> 2 / i);
          expect(p2::get).toThrow(ArithmeticException.class);
        });
        it("should return a FailedPromise when mapped on a FailedPromise", () -> {
          val p = Promise.<Integer>promise();
          p.fulfill(10);
          val p2 = p.map(i -> i - 10).map(i -> 2 / i).map(i -> i + 1);
          expect(p2::get).toThrow(ArithmeticException.class);
        });
      });
    });
  }

}