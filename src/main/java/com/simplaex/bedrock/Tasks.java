package com.simplaex.bedrock;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.UtilityClass;
import lombok.val;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Function;

import static com.simplaex.bedrock.Control.*;

@UtilityClass
public class Tasks {

  @FunctionalInterface
  public interface Task<R> {
    void run(final Callback<R> done, final Function<String, Object> args) throws Exception;
  }

  @FunctionalInterface
  public interface Task0<R> {
    void run(final Callback<R> done) throws Exception;
  }

  @FunctionalInterface
  public interface Task1<A1, R> {
    void run(final Callback<R> done, final A1 arg1) throws Exception;
  }

  @FunctionalInterface
  public interface Task2<A1, A2, R> {
    void run(final Callback<R> done, final A1 arg1, final A2 arg2) throws Exception;
  }

  @FunctionalInterface
  public interface Task3<A1, A2, A3, R> {
    void run(final Callback<R> done, final A1 arg1, final A2 arg2, final A3 arg3) throws Exception;
  }

  @FunctionalInterface
  public interface Task4<A1, A2, A3, A4, R> {
    void run(final Callback<R> done, final A1 arg1, final A2 arg2, final A3 arg3, final A4 arg4) throws Exception;
  }

  @FunctionalInterface
  public interface Task5<A1, A2, A3, A4, A5, R> {
    void run(
      final Callback<R> done,
      final A1 arg1,
      final A2 arg2,
      final A3 arg3,
      final A4 arg4,
      final A5 arg5
    ) throws Exception;
  }

  @FunctionalInterface
  public interface Task6<A1, A2, A3, A4, A5, A6, R> {
    void run(
      final Callback<R> done,
      final A1 arg1,
      final A2 arg2,
      final A3 arg3,
      final A4 arg4,
      final A5 arg5,
      final A6 arg6
    ) throws Exception;
  }

  @FunctionalInterface
  public interface Task7<A1, A2, A3, A4, A5, A6, A7, R> {
    void run(
      final Callback<R> done,
      final A1 arg1,
      final A2 arg2,
      final A3 arg3,
      final A4 arg4,
      final A5 arg5,
      final A6 arg6,
      final A7 arg7
    ) throws Exception;
  }

  @FunctionalInterface
  public interface Task8<A1, A2, A3, A4, A5, A6, A7, A8, R> {
    void run(
      final Callback<R> done,
      final A1 arg1,
      final A2 arg2,
      final A3 arg3,
      final A4 arg4,
      final A5 arg5,
      final A6 arg6,
      final A7 arg7,
      final A8 arg8
    ) throws Exception;
  }

  @FunctionalInterface
  public interface Task9<A1, A2, A3, A4, A5, A6, A7, A8, A9, R> {
    void run(
      final Callback<R> done,
      final A1 arg1,
      final A2 arg2,
      final A3 arg3,
      final A4 arg4,
      final A5 arg5,
      final A6 arg6,
      final A7 arg7,
      final A8 arg8,
      final A9 arg9
    ) throws Exception;
  }

  @FunctionalInterface
  public interface Task10<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, R> {
    void run(
      final Callback<R> done,
      final A1 arg1,
      final A2 arg2,
      final A3 arg3,
      final A4 arg4,
      final A5 arg5,
      final A6 arg6,
      final A7 arg7,
      final A8 arg8,
      final A9 arg9,
      final A10 arg10
    ) throws Exception;
  }

  @Value
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static final class TaskSpec {
    private final String id;
    private final Seq<String> arguments;
    private final Object task;
  }

  public static <R> TaskSpec task(
    final String id,
    @Nonnull final Task0<R> task
  ) {
    Objects.requireNonNull(task, "task must not be null");
    return new TaskSpec(id, Seq.empty(), task);
  }

  public static <A1, R> TaskSpec task(
    final String id,
    final String arg1,
    @Nonnull final Task1<A1, R> task
  ) {
    Objects.requireNonNull(task, "task must not be null");
    return new TaskSpec(id, Seq.of(arg1), task);
  }

  public static <A1, A2, R> TaskSpec task(
    final String id,
    final String arg1,
    final String arg2,
    @Nonnull final Task2<A1, A2, R> task
  ) {
    Objects.requireNonNull(task, "task must not be null");
    return new TaskSpec(id, Seq.of(arg1, arg2), task);
  }

  public static <A1, A2, A3, R> TaskSpec task(
    final String id,
    final String arg1,
    final String arg2,
    final String arg3,
    @Nonnull final Task3<A1, A2, A3, R> task
  ) {
    Objects.requireNonNull(task, "task must not be null");
    return new TaskSpec(id, Seq.of(arg1, arg2, arg3), task);
  }

  public static <A1, A2, A3, A4, R> TaskSpec task(
    final String id,
    final String arg1,
    final String arg2,
    final String arg3,
    final String arg4,
    @Nonnull final Task4<A1, A2, A3, A4, R> task
  ) {
    Objects.requireNonNull(task, "task must not be null");
    return new TaskSpec(id, Seq.of(arg1, arg2, arg3, arg4), task);
  }

  public static <A1, A2, A3, A4, A5, R> TaskSpec task(
    final String id,
    final String arg1,
    final String arg2,
    final String arg3,
    final String arg4,
    final String arg5,
    @Nonnull final Task5<A1, A2, A3, A4, A5, R> task
  ) {
    Objects.requireNonNull(task, "task must not be null");
    return new TaskSpec(id, Seq.of(arg1, arg2, arg3, arg4, arg5), task);
  }

  public static <A1, A2, A3, A4, A5, A6, R> TaskSpec task(
    final String id,
    final String arg1,
    final String arg2,
    final String arg3,
    final String arg4,
    final String arg5,
    final String arg6,
    @Nonnull final Task6<A1, A2, A3, A4, A5, A6, R> task
  ) {
    Objects.requireNonNull(task, "task must not be null");
    return new TaskSpec(id, Seq.of(arg1, arg2, arg3, arg4, arg5, arg6), task);
  }

  public static <A1, A2, A3, A4, A5, A6, A7, R> TaskSpec task(
    final String id,
    final String arg1,
    final String arg2,
    final String arg3,
    final String arg4,
    final String arg5,
    final String arg6,
    final String arg7,
    @Nonnull final Task7<A1, A2, A3, A4, A5, A6, A7, R> task
  ) {
    Objects.requireNonNull(task, "task must not be null");
    return new TaskSpec(id, Seq.of(arg1, arg2, arg3, arg4, arg5, arg6, arg7), task);
  }

  public static <A1, A2, A3, A4, A5, A6, A7, A8, R> TaskSpec task(
    final String id,
    final String arg1,
    final String arg2,
    final String arg3,
    final String arg4,
    final String arg5,
    final String arg6,
    final String arg7,
    final String arg8,
    @Nonnull final Task8<A1, A2, A3, A4, A5, A6, A7, A8, R> task
  ) {
    Objects.requireNonNull(task, "task must not be null");
    return new TaskSpec(id, Seq.of(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8), task);
  }

  public static <A1, A2, A3, A4, A5, A6, A7, A8, A9, R> TaskSpec task(
    final String id,
    final String arg1,
    final String arg2,
    final String arg3,
    final String arg4,
    final String arg5,
    final String arg6,
    final String arg7,
    final String arg8,
    final String arg9,
    @Nonnull final Task9<A1, A2, A3, A4, A5, A6, A7, A8, A9, R> task
  ) {
    Objects.requireNonNull(task, "task must not be null");
    return new TaskSpec(id, Seq.of(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9), task);
  }

  public static <A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, R> TaskSpec task(
    final String id,
    final String arg1,
    final String arg2,
    final String arg3,
    final String arg4,
    final String arg5,
    final String arg6,
    final String arg7,
    final String arg8,
    final String arg9,
    final String arg10,
    @Nonnull final Task10<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, R> task
  ) {
    Objects.requireNonNull(task, "task must not be null");
    return new TaskSpec(id, Seq.of(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10), task);
  }

  static <R> void runTask(
    final @Nonnull TaskSpec taskSpec,
    final @Nonnull Function<String, Object> getArgumentFunction,
    final @Nonnull Callback<R> callback
  ) {
    val task = taskSpec.getTask();
    class Runner {
      private Object a(final int i) {
        return getArgumentFunction.apply(taskSpec.getArguments().get(i));
      }

      @SuppressWarnings("unchecked")
      void run() {
        typeOf(
          task,
          type_(Task.class, t -> t.run(callback, getArgumentFunction)),
          type_(Task0.class, t -> t.run(callback)),
          type_(Task1.class, t -> t.run(callback, a(0))),
          type_(Task2.class, t -> t.run(callback, a(0), a(1))),
          type_(Task3.class, t -> t.run(callback, a(0), a(1), a(2))),
          type_(Task4.class, t -> t.run(callback, a(0), a(1), a(2), a(3))),
          type_(Task5.class, t -> t.run(callback, a(0), a(1), a(2), a(3), a(4))),
          type_(Task6.class, t -> t.run(callback, a(0), a(1), a(2), a(3), a(4), a(5))),
          type_(Task7.class, t -> t.run(callback, a(0), a(1), a(2), a(3), a(4), a(5), a(6))),
          type_(Task8.class, t -> t.run(callback, a(0), a(1), a(2), a(3), a(4), a(5), a(6), a(7))),
          type_(Task9.class, t -> t.run(callback, a(0), a(1), a(2), a(3), a(4), a(5), a(6), a(7), a(8))),
          type_(Task10.class, t -> t.run(callback, a(0), a(1), a(2), a(3), a(4), a(5), a(6), a(7), a(8), a(9)))
        );
      }
    }
    new Runner().run();
  }

}
