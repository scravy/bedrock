package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import lombok.val;
import org.junit.runner.RunWith;

import java.time.Duration;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@SuppressWarnings({"ClassInitializerMayBeStatic", "CodeBlock2Expr"})
@RunWith(Spectrum.class)
public class ControlTest {

  {
    describe("sleep", () -> {
      it("should sleep the specified amount of time", () -> {
        val started = System.nanoTime();
        Control.sleep(Duration.ofMillis(200));
        val finished = System.nanoTime();
        val duration = Duration.ofNanos(finished - started);
        expect(duration.toMillis()).toBeGreaterThan(200);
      });
    });
    describe("forever(ThrowingRunnable)", () -> {
      it("should be interruptible", () -> {
        val sem1 = new Semaphore(0);
        val sem2 = new Semaphore(0);
        val counter = new AtomicInteger(0);
        val finished = new AtomicInteger(0);
        val thread = new Thread(() -> {
          Control.forever(() -> {
            sem1.acquire();
            counter.incrementAndGet();
            sem2.release();
          });
          finished.incrementAndGet();
        });
        thread.start();

        sem1.release();
        sem2.acquire();

        sem1.release();
        sem2.acquire();

        sem1.release();
        sem2.acquire();

        thread.interrupt();
        thread.join();

        expect(counter.get()).toEqual(3);
        expect(thread.getState()).toEqual(Thread.State.TERMINATED);
        expect(finished.get()).toEqual(1);
      });
    });
    describe("forever(ExceptionHandler,ThrowingRunnable)", () -> {
      it("should be interruptible", () -> {
        val sem1 = new Semaphore(0);
        val sem2 = new Semaphore(0);
        val counter = new AtomicInteger();
        val finished = new AtomicInteger(0);
        val thread = new Thread(() -> {
          Control.forever(exc -> {
          }, () -> {
            sem1.acquire();
            counter.incrementAndGet();
            sem2.release();
          });
          finished.incrementAndGet();
        });
        thread.start();

        sem1.release();
        sem2.acquire();

        sem1.release();
        sem2.acquire();

        sem1.release();
        sem2.acquire();

        thread.interrupt();
        thread.join();

        expect(counter.get()).toEqual(3);
        expect(thread.getState()).toEqual(Thread.State.TERMINATED);
        expect(finished.get()).toEqual(1);
      });
    });
  }

}
