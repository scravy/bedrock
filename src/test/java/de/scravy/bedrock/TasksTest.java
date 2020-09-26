package de.scravy.bedrock;

import com.greghaskins.spectrum.Spectrum;
import lombok.val;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicLong;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@SuppressWarnings({"CodeBlock2Expr", "ClassInitializerMayBeStatic"})
@RunWith(Spectrum.class)
public class TasksTest {

  {
    val primes = new long[]{1, 2, 3, 5, 7, 11, 13, 17, 19, 23, 29};

    describe("runTask", () -> {

      it("Task0", () -> {
        val x = new AtomicLong(0);
        Tasks.runTask(
          Tasks.task(
            "Task0",
            (callback) -> callback.call(null, 1L)
          ),
          __ -> null,
          (err, res) -> x.set((Long) res)
        );
        expect(x.get()).toEqual(1);
      });

      it("Task1", () -> {
        val x = new AtomicLong(0);
        Tasks.runTask(
          Tasks.task(
            "Task1",
            "1",
            (callback, a1) -> callback.call(null, a1)
          ),
          a -> primes[Integer.parseInt(a)],
          (err, res) -> x.set((Long) res)
        );
        expect(x.get()).toEqual(2);
      });

      it("Task2", () -> {
        val x = new AtomicLong(0);
        Tasks.runTask(
          Tasks.<Long, Long, Long>task(
            "Task2",
            "1",
            "2",
            (callback, a1, a2) -> callback.call(null, a1 * a2)
          ),
          a -> primes[Integer.parseInt(a)],
          (err, res) -> x.set((Long) res)
        );
        expect(x.get()).toEqual(2 * 3);
      });

      it("Task3", () -> {
        val x = new AtomicLong(0);
        Tasks.runTask(
          Tasks.<Long, Long, Long, Long>task(
            "Task3",
            "1",
            "2",
            "3",
            (callback, a1, a2, a3) -> callback.call(null, a1 * a2 * a3)
          ),
          a -> primes[Integer.parseInt(a)],
          (err, res) -> x.set((Long) res)
        );
        expect(x.get()).toEqual(2 * 3 * 5);
      });

      it("Task4", () -> {
        val x = new AtomicLong(0);
        Tasks.runTask(
          Tasks.<Long, Long, Long, Long, Long>task(
            "Task4",
            "1",
            "2",
            "3",
            "4",
            (callback, a1, a2, a3, a4) -> callback.call(null, a1 * a2 * a3 * a4)
          ),
          a -> primes[Integer.parseInt(a)],
          (err, res) -> x.set((Long) res)
        );
        expect(x.get()).toEqual(2 * 3 * 5 * 7);
      });

      it("Task5", () -> {
        val x = new AtomicLong(0);
        Tasks.runTask(
          Tasks.<Long, Long, Long, Long, Long, Long>task(
            "Task5",
            "1",
            "2",
            "3",
            "4",
            "5",
            (callback, a1, a2, a3, a4, a5) -> callback.call(null, a1 * a2 * a3 * a4 * a5)
          ),
          a -> primes[Integer.parseInt(a)],
          (err, res) -> x.set((Long) res)
        );
        expect(x.get()).toEqual(2 * 3 * 5 * 7 * 11);
      });

      it("Task6", () -> {
        val x = new AtomicLong(0);
        Tasks.runTask(
          Tasks.<Long, Long, Long, Long, Long, Long, Long>task(
            "Task6",
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            (callback, a1, a2, a3, a4, a5, a6) -> callback.call(null, a1 * a2 * a3 * a4 * a5 * a6)
          ),
          a -> primes[Integer.parseInt(a)],
          (err, res) -> x.set((Long) res)
        );
        expect(x.get()).toEqual(2 * 3 * 5 * 7 * 11 * 13);
      });

      it("Task7", () -> {
        val x = new AtomicLong(0);
        Tasks.runTask(
          Tasks.<Long, Long, Long, Long, Long, Long, Long, Long>task(
            "Task7",
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            (callback, a1, a2, a3, a4, a5, a6, a7) -> callback.call(null, a1 * a2 * a3 * a4 * a5 * a6 * a7)
          ),
          a -> primes[Integer.parseInt(a)],
          (err, res) -> x.set((Long) res)
        );
        expect(x.get()).toEqual(2 * 3 * 5 * 7 * 11 * 13 * 17);
      });

      it("Task8", () -> {
        val x = new AtomicLong(0);
        Tasks.runTask(
          Tasks.<Long, Long, Long, Long, Long, Long, Long, Long, Long>task(
            "Task8",
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            (callback, a1, a2, a3, a4, a5, a6, a7, a8) -> callback.call(null, a1 * a2 * a3 * a4 * a5 * a6 * a7 * a8)
          ),
          a -> primes[Integer.parseInt(a)],
          (err, res) -> x.set((Long) res)
        );
        expect(x.get()).toEqual(2 * 3 * 5 * 7 * 11 * 13 * 17 * 19);
      });

      it("Task9", () -> {
        val x = new AtomicLong(0);
        Tasks.runTask(
          Tasks.<Long, Long, Long, Long, Long, Long, Long, Long, Long, Long>task(
            "Task9",
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9",
            (callback, a1, a2, a3, a4, a5, a6, a7, a8, a9) -> callback.call(null, a1 * a2 * a3 * a4 * a5 * a6 * a7 * a8 * a9)
          ),
          a -> primes[Integer.parseInt(a)],
          (err, res) -> x.set((Long) res)
        );
        expect(x.get()).toEqual(2 * 3 * 5 * 7 * 11 * 13 * 17 * 19 * 23);
      });

      it("Task10", () -> {
        val x = new AtomicLong(0);
        Tasks.runTask(
          Tasks.<Long, Long, Long, Long, Long, Long, Long, Long, Long, Long, Long>task(
            "Task10",
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9",
            "10",
            (callback, a1, a2, a3, a4, a5, a6, a7, a8, a9, a10) -> callback.call(null, a1 * a2 * a3 * a4 * a5 * a6 * a7 * a8 * a9 / a10)
          ),
          a -> primes[Integer.parseInt(a)],
          (err, res) -> x.set((Long) res)
        );
        expect(x.get()).toEqual(2 * 3 * 5 * 7 * 11 * 13 * 17 * 19 * 23 / 29);
      });

    });
  }

}
