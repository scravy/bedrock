package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import lombok.val;
import org.junit.runner.RunWith;

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@SuppressWarnings({"CodeBlock2Expr", "ClassInitializerMayBeStatic"})
@RunWith(Spectrum.class)
public class PromiseTest {
  {
    describe(Promise.class.getSimpleName(), () -> {
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
        expect(p::get).toThrow(AsyncExecutionException.class);
      });
      it("isSuccess", () -> {
        expect(Promise.fulfilled(3).isSuccess()).toBeTrue();
        expect(Promise.failed(new NoSuchAlgorithmException()).isSuccess()).toBeFalse();
      });
      it("isFailure", () -> {
        expect(Promise.fulfilled(3).isFailure()).toBeFalse();
        expect(Promise.failed(new NoSuchAlgorithmException()).isFailure()).toBeTrue();
      });
      it("isPending", () -> {
        expect(Promise.promise().isPending()).toBeTrue();
        expect(Promise.fulfilled(3).isPending()).toBeFalse();
        expect(Promise.failed(new NoSuchAlgorithmException()).isPending()).toBeFalse();
      });
      it("toTry", () -> {
        expect(Promise.fulfilled(7).toTry().isSuccess()).toBeTrue();
        expect(Promise.failed(new IllegalArgumentException()).toTry().isFailure()).toBeTrue();
      });
      it("toOptional", () -> {
        expect(Promise.fulfilled(7).toOptional().isPresent()).toBeTrue();
        expect(Promise.failed(new IllegalArgumentException()).toOptional().isPresent()).toBeFalse();
      });
      it("fulfill when already fulfilled", () -> {
        expect(() -> Promise.fulfilled("").fulfill("")).toThrow(IllegalStateException.class);
      });
      it("fail when already fulfilled", () -> {
        expect(() -> Promise.fulfilled("").fail(new IllegalArgumentException())).toThrow(IllegalStateException.class);
      });
      it("fulfill when already failed", () -> {
        expect(() -> Promise.failed(new IllegalArgumentException()).fulfill("")).toThrow(IllegalStateException.class);
      });
      it("fail when already failed", () -> {
        expect(() -> Promise.failed(new IllegalArgumentException()).fail(new IllegalArgumentException())).toThrow(IllegalStateException.class);
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
          expect(p2::get).toThrow(AsyncExecutionException.class);
        });
        it("should return a FailedPromise when mapped on a FailedPromise", () -> {
          val p = Promise.<Integer>promise();
          p.fulfill(10);
          val p2 = p.map(i -> i - 10).map(i -> 2 / i).map(i -> i + 1);
          expect(p2::get).toThrow(AsyncExecutionException.class);
        });
      });
    });

    describe("fulfill", () -> {
      describe("with FULFILLED promise", () -> {
        it("should throw an IllegalStateException", () -> {
          expect(() -> Promise.fulfilled("").fulfill("")).toThrow(IllegalStateException.class);
        });
      });
      describe("with FAILED promise", () -> {
        it("should throw an IllegalStateException", () -> {
          expect(() -> Promise.failed(new IllegalArgumentException()).fulfill("")).toThrow(IllegalStateException.class);
        });
      });
    });

    describe("fail", () -> {
      describe("with FULFILLED promise", () -> {
        it("should throw an IllegalStateException", () -> {
          expect(() -> Promise.fulfilled("").fail(new IllegalArgumentException())).toThrow(IllegalStateException.class);
        });
      });
      describe("with FAILED promise", () -> {
        it("should throw an IllegalStateException", () -> {
          expect(() -> Promise.failed(new IllegalArgumentException()).fail(new IllegalArgumentException())).toThrow(IllegalStateException.class);
        });
      });
    });

    describe("filter", () -> {
      describe("with PENDING promise", () -> {
        it("should keep FULFILLED state for filter(true)", () -> {
          val p = Promise.<Integer>promise().filter(x -> x >= 3);
          p.fulfill(3);
          expect(p.getState()).toEqual(Promise.State.FULFILLED);
        });
        it("should keep FAILED state for filter(false)", () -> {
          val p = Promise.<Integer>promise().filter(x -> x > 3);
          p.fulfill(3);
          expect(p.getState()).toEqual(Promise.State.FAILED);
        });
        it("should keep FAILED state when already failed", () -> {
          val p = Promise.<Integer>promise();
          p.fail(new IllegalArgumentException());
          expect(p.getState()).toEqual(Promise.State.FAILED);
        });
      });
      describe("with FULFILLED promise", () -> {
        it("should keep FULFILLED state for filter(true)", () -> {
          val p = Promise.fulfilled(3).filter(x -> x >= 3);
          expect(p.getState()).toEqual(Promise.State.FULFILLED);
        });
        it("should keep FAILED state for filter(false)", () -> {
          val p = Promise.fulfilled(3).filter(x -> x > 3);
          expect(p.getState()).toEqual(Promise.State.FAILED);
        });
      });
      describe("with FULFILLED promise", () -> {
        it("should keep FAILED state when already failed", () -> {
          val p = Promise.<Integer>failed(new IllegalArgumentException()).filter(x -> x > 3);
          expect(p.getState()).toEqual(Promise.State.FAILED);
        });
      });
    });
  }

}
