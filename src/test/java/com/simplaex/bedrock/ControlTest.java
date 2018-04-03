package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import lombok.val;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.math.BigInteger;
import java.time.Duration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static com.greghaskins.spectrum.Spectrum.*;
import static com.mscharhag.oleaster.matcher.Matchers.expect;
import static com.simplaex.bedrock.Pair.pair;

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
        expect(duration.toMillis()).toBeGreaterThan(199);
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
    describe("parallel(ThrowingConsumer...)", () -> {
      it("should orchestrate the parallel application of async functions", () -> {
        final Semaphore semaphore = new Semaphore(0);
        final ExecutorService executorService = Executors.newWorkStealingPool();
        final Consumer<Callback<Seq<Integer>>> asyncFunc = Control.parallel(
          callback -> executorService.submit(() -> callback.success(18)),
          callback -> executorService.submit(() -> callback.success(91)),
          callback -> executorService.submit(() -> callback.success(37))
        );
        final AtomicReference<Seq<Integer>> promise = new AtomicReference<>();
        asyncFunc.accept((err, res) -> {
          promise.set(res);
          semaphore.release();
        });
        expect(semaphore.tryAcquire(2, TimeUnit.SECONDS)).toBeTrue();
        expect(promise.get()).toEqual(Seq.of(18, 91, 37));
      });
    });
    describe("parallel(Executor,ThrowingRunnable...)", () -> {
      it("using an Executor on the same thread", () -> {
        val counter = new AtomicInteger(0);
        Control.parallel(
          Runnable::run,
          (ThrowingRunnable) counter::incrementAndGet,
          counter::incrementAndGet,
          counter::incrementAndGet
        );
        expect(counter.get()).toEqual(3);
      });
      it("using a SingleThreadExecutor", () -> {
        val counter = new AtomicInteger(0);
        Control.parallel(
          Executors.newSingleThreadExecutor(),
          (ThrowingRunnable) counter::incrementAndGet,
          counter::incrementAndGet,
          counter::incrementAndGet
        );
        expect(counter.get()).toEqual(3);
      });
      it("using a WorkStealingPool", () -> {
        val counter = new AtomicInteger(0);
        Control.parallel(
          Executors.newWorkStealingPool(),
          (ThrowingRunnable) counter::incrementAndGet,
          counter::incrementAndGet,
          counter::incrementAndGet
        );
        expect(counter.get()).toEqual(3);
      });
    });
    describe("parallel(Executor,Callable...)", () -> {
      it("using an Executor on the same thread", () -> {
        val counter = new AtomicInteger(0);
        val result = Control.parallel(
          Runnable::run,
          counter::incrementAndGet,
          counter::incrementAndGet,
          counter::incrementAndGet
        );
        expect(result.stream().mapToInt(Integer::intValue).sum()).toEqual(6);
        expect(counter.get()).toEqual(3);
      });
      it("using a SingleThreadExecutor", () -> {
        val counter = new AtomicInteger(0);
        val result = Control.parallel(
          Executors.newSingleThreadExecutor(),
          counter::incrementAndGet,
          counter::incrementAndGet,
          counter::incrementAndGet
        );
        expect(result.stream().mapToInt(Integer::intValue).sum()).toEqual(6);
        expect(counter.get()).toEqual(3);
      });
      it("using a WorkStealingPool", () -> {
        val counter = new AtomicInteger(0);
        val result = Control.parallel(
          Executors.newWorkStealingPool(),
          counter::incrementAndGet,
          counter::incrementAndGet,
          counter::incrementAndGet
        );
        expect(result.stream().mapToInt(Integer::intValue).sum()).toEqual(6);
        expect(counter.get()).toEqual(3);
      });
      it("should capture the exceptions when these are thrown", () -> {
        val counter = new AtomicInteger(0);
        expect(() -> Control.parallel(
          Executors.newCachedThreadPool(),
          counter::incrementAndGet,
          () -> {
            throw new IndexOutOfBoundsException();
          },
          () -> {
            throw new IOException();
          }
        )).toThrow(ParallelExecutionException.class);
      });
    });
    final Executor manyThreads = Executors.newFixedThreadPool(4);
    final Executor singleThread = Executors.newSingleThreadExecutor();
    final Executor noThreads = Runnable::run;
    val tracer = let(Seq::<String>builder);
    Seq.<Pair<Executor, ThrowingRunnable>>of(
      pair(noThreads, () -> {
        val journal = tracer.get().result();
        expect(journal.forAll(threadName -> Thread.currentThread().getName().equals(threadName))).toBeTrue();
      }),
      pair(singleThread, () -> {
        val journal = tracer.get().result();
        val distinct = journal.distinct();
        expect(distinct.size()).toEqual(1);
        expect(distinct.head().equals(Thread.currentThread().getName())).toBeFalse();
      }),
      pair(manyThreads, () -> {
        val journal = tracer.get().result();
        expect(journal.forAll(threadName -> !Thread.currentThread().getName().equals(threadName))).toBeTrue();
      })
    ).forEach(spec -> {
      val executor = spec.fst();
      val checks = spec.snd();
      describe("async with " + executor.getClass().getName(), () -> {
        describe("then(ThrowingBiConsumer)", () -> {
          final Control.Async<Object, Integer> async = Control
            .<Object, String>async((a, callback) -> {
              tracer.get().add(Thread.currentThread().getName());
              callback.call(null, a.toString());
            })
            .<Integer>then((a, callback) -> {
              tracer.get().add(Thread.currentThread().getName());
              callback.call(null, Integer.parseInt(a));
            })
            .then((a, callback) -> {
              tracer.get().add(Thread.currentThread().getName());
              callback.call(null, a + 1);
            });
          it("should invoke the async computations and pass results through", () -> {
            final Promise<Integer> promise = async.runPromised(executor, BigInteger.TEN);
            promise.waitFor();
            promise.onComplete((error, result) -> {
              expect(error).toBeNull();
              expect(result).toEqual(11);
            });
            checks.run();
          });
          it("should return the error if an exception occurs", () -> {
            final Promise<Integer> promise = async.runPromised(executor, "can not be parsed as integer");
            promise.waitFor();
            promise.onComplete((error, result) -> {
              expect(error).toBeNotNull();
            });
            checks.run();
          });
        });
        describe("then(ThrowingBiConsumer,ThrowingBiConsumer...)", () -> {
          final Control.Async<Object, Integer> async = Control
            .<Object, String>async((a, callback) -> {
              tracer.get().add(Thread.currentThread().getName());
              callback.call(null, a.toString());
            })
            .<Integer>then(
              (a, callback) -> {
                tracer.get().add(Thread.currentThread().getName());
                callback.call(null, Integer.parseInt(a));
              },
              (a, callback) -> {
                tracer.get().add(Thread.currentThread().getName());
                callback.call(null, Integer.parseInt(a));
              }
            )
            .then((a, callback) -> {
              tracer.get().add(Thread.currentThread().getName());
              callback.call(null, a.foldl((a0, a1) -> a0 + a1, 5));
            });
          it("should invoke the async computations and pass results through", () -> {
            final Promise<Integer> promise = async.runPromised(executor, BigInteger.TEN);
            promise.waitFor();
            promise.onComplete((error, result) -> {
              expect(error).toBeNull();
              expect(result).toEqual(25);
            });
            checks.run();
          });
          it("should return the error if an exception occurs", () -> {
            final Promise<Integer> promise = async.runPromised(executor, "can not be parsed as integer");
            promise.waitFor();
            promise.onComplete((error, result) -> {
              expect(error).toBeNotNull();
            });
            checks.run();
          });
          it("should return the error if an exception occurs (in first step)", () -> {
            final Promise<Integer> promise = async.runPromised(executor, new Object() {
              @Override
              public String toString() {
                throw new RuntimeException("I blow up");
              }
            });
            promise.waitFor();
            promise.onComplete((error, result) -> {
              expect(error).toBeNotNull();
              expect(error).toBeInstanceOf(RuntimeException.class);
            });
            checks.run();
          });
        });
      });
    });
  }

}
