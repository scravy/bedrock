package com.simplaex.bedrock;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.Value;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@SuppressWarnings("WeakerAccess")
public abstract class Try<E> implements Iterable<E> {

  private Try() {

  }

  public boolean isSuccess() {
    return false;
  }

  public boolean isFailure() {
    return false;
  }

  @EqualsAndHashCode(callSuper = true)
  public static class RethrownException extends Exception {
    public RethrownException(final Exception cause) {
      super(cause);
    }
  }

  @EqualsAndHashCode(callSuper = false)
  public static class ValueDidNotSatisfyPredicateException extends Exception {
    @Getter
    private final Predicate<?> predicate;

    @Getter
    private final Object value;

    public ValueDidNotSatisfyPredicateException(@Nonnull final Predicate<?> predicate, final Object value) {
      this.predicate = predicate;
      this.value = value;
    }
  }

  public static class FailedRecoveringException extends Exception {
    @Getter
    private final Exception originalException;

    public FailedRecoveringException(@Nonnull final Exception originalException, @Nonnull final Exception exc) {
      super(exc);
      this.originalException = originalException;
    }
  }

  @Nonnull
  public abstract <F> Try<F> map(@Nonnull final ThrowingFunction<? super E, ? extends F> f);

  @Nonnull
  public abstract <F> Try<F> flatMap(@Nonnull final ThrowingFunction<? super E, Try<F>> f);

  @Nonnull
  public abstract Try<E> filter(@Nonnull final Predicate<E> predicate);

  @Nonnull
  public abstract <F> Try<F> recover(@Nonnull final ThrowingFunction<Exception, F> value);

  @Nonnull
  public abstract <F> Try<F> recoverWith(@Nonnull final ThrowingFunction<Exception, Try<F>> value);

  public abstract E orElse(final E value);

  public abstract E orElseGet(@Nonnull final Supplier<? extends E> supplier);

  @Nullable
  public abstract E orElseNull();

  public abstract E orElseThrow() throws RethrownException;

  public abstract E orElseThrowRuntime();

  public abstract E get();

  @Nonnull
  public abstract Try<E> otherwise(@Nonnull final Try<E> alternative);

  @Nonnull
  public abstract <F> Try<F> transform(@Nonnull final ThrowingFunction<Exception, F> f, final ThrowingFunction<E, F> g);

  @Nonnull
  public abstract <F> Try<F> transformWith(@Nonnull final ThrowingFunction<Exception, Try<F>> f, final ThrowingFunction<E, Try<F>> g);

  @Nonnull
  public abstract Optional<E> toOptional();

  @Value
  @EqualsAndHashCode(callSuper = false)
  public static final class Success<E> extends Try<E> {

    private final E value;

    Success(final E value) {
      this.value = value;
    }

    @Override
    public final boolean isSuccess() {
      return true;
    }

    @Nonnull
    @Override
    public <F> Try<F> map(@Nonnull final ThrowingFunction<? super E, ? extends F> function) {
      Objects.requireNonNull(function, "function must not be null");
      try {
        return success(function.execute(value));
      } catch (final Exception exc) {
        return failure(exc);
      }
    }

    @Nonnull
    @Override
    public <F> Try<F> flatMap(@Nonnull final ThrowingFunction<? super E, Try<F>> function) {
      Objects.requireNonNull(function, "function must not be null");
      try {
        return function.execute(value);
      } catch (final Exception exc) {
        return failure(exc);
      }
    }

    @Nonnull
    @Override
    public Try<E> filter(@Nonnull final Predicate<E> predicate) {
      Objects.requireNonNull(predicate, "predicate must not be null");
      if (predicate.test(value)) {
        return this;
      }
      return failure(new ValueDidNotSatisfyPredicateException(predicate, value));
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public <F> Try<F> recover(@Nonnull final ThrowingFunction<Exception, F> exceptionTransformer) {
      Objects.requireNonNull(exceptionTransformer, "exceptionTransformer must not be null");
      return (Try<F>) this;
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public <F> Try<F> recoverWith(@Nonnull final ThrowingFunction<Exception, Try<F>> exceptionTransformer) {
      Objects.requireNonNull(exceptionTransformer, "exceptionTransformer must not be null");
      return (Try<F>) this;
    }

    @Override
    public E orElse(E value) {
      return this.value;
    }

    @Override
    public E orElseGet(@Nonnull Supplier<? extends E> supplier) {
      Objects.requireNonNull(supplier, "supplier must not be null");
      return this.value;
    }

    @Override
    public E orElseNull() {
      return this.value;
    }

    @Override
    public E orElseThrow() {
      return this.value;
    }

    @Override
    public E orElseThrowRuntime() {
      return this.value;
    }

    @Override
    public E get() {
      return this.value;
    }

    @Nonnull
    @Override
    public Try<E> otherwise(@Nonnull final Try<E> alternative) {
      Objects.requireNonNull(alternative, "alternative must not be null");
      return this;
    }

    @Nonnull
    @Override
    public <F> Try<F> transform(@Nonnull final ThrowingFunction<Exception, F> f, final ThrowingFunction<E, F> g) {
      Objects.requireNonNull(f, "f must not be null");
      Objects.requireNonNull(g, "g must not be null");
      try {
        return success(g.execute(value));
      } catch (final Exception exc) {
        return failure(exc);
      }
    }

    @Nonnull
    @Override
    public <F> Try<F> transformWith(@Nonnull final ThrowingFunction<Exception, Try<F>> f, final ThrowingFunction<E, Try<F>> g) {
      Objects.requireNonNull(f, "f must not be null");
      Objects.requireNonNull(g, "g must not be null");
      try {
        return g.execute(value);
      } catch (final Exception exc) {
        return failure(exc);
      }
    }

    @Nonnull
    @Override
    public Optional<E> toOptional() {
      return Optional.ofNullable(value);
    }

    @Override
    public Iterator<E> iterator() {
      return new Iterator<E>() {
        private boolean consumed = false;

        @Override
        public boolean hasNext() {
          return !consumed;
        }

        @Override
        public E next() {
          try {
            return value;
          } finally {
            consumed = true;
          }
        }
      };
    }
  }

  @Value
  @EqualsAndHashCode(callSuper = false)
  public static final class Failure<E> extends Try<E> {

    private final Exception exception;

    Failure(@Nonnull final Exception exception) {
      this.exception = exception;
    }

    @Override
    public final boolean isFailure() {
      return true;
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public <F> Try<F> map(@Nonnull final ThrowingFunction<? super E, ? extends F> f) {
      Objects.requireNonNull(f, "f must not be null");
      return (Try<F>) this;
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public <F> Try<F> flatMap(@Nonnull final ThrowingFunction<? super E, Try<F>> f) {
      Objects.requireNonNull(f, "f must not be null");
      return (Try<F>) this;
    }

    @Nonnull
    @Override
    public Try<E> filter(@Nonnull final Predicate<E> predicate) {
      Objects.requireNonNull(predicate, "predicate must not be null");
      return this;
    }

    @Nonnull
    @Override
    public <F> Try<F> recover(@Nonnull final ThrowingFunction<Exception, F> f) {
      Objects.requireNonNull(f, "f must not be null");
      try {
        return success(f.execute(exception));
      } catch (final Exception exc) {
        return failure(new FailedRecoveringException(exception, exc));
      }
    }

    @Nonnull
    @Override
    public <F> Try<F> recoverWith(@Nonnull final ThrowingFunction<Exception, Try<F>> f) {
      Objects.requireNonNull(f, "f must not be null");
      try {
        return f.execute(exception);
      } catch (final Exception exc) {
        return failure(new FailedRecoveringException(exception, exc));
      }
    }

    @Override
    public E orElse(final E value) {
      return value;
    }

    @Override
    public E orElseGet(@Nonnull final Supplier<? extends E> supplier) {
      Objects.requireNonNull(supplier, "supplier must not be null");
      return supplier.get();
    }

    @Nullable
    @Override
    public E orElseNull() {
      return null;
    }

    @Override
    public E orElseThrow() throws RethrownException {
      throw new RethrownException(exception);
    }

    @Override
    public E orElseThrowRuntime() {
      throw new RuntimeException(exception);
    }

    @Override
    public E get() {
      throw new NoSuchElementException();
    }

    @Nonnull
    @Override
    public Try<E> otherwise(@Nonnull final Try<E> alternative) {
      Objects.requireNonNull(alternative, "alternative must not be null");
      return alternative;
    }

    @Nonnull
    @Override
    public <F> Try<F> transform(final @Nonnull ThrowingFunction<Exception, F> f, final @Nonnull ThrowingFunction<E, F> g) {
      Objects.requireNonNull(f, "f must not be null");
      Objects.requireNonNull(g, "g must not be null");
      try {
        return success(f.execute(exception));
      } catch (final Exception exc) {
        return failure(new FailedRecoveringException(exception, exc));
      }
    }

    @Nonnull
    @Override
    public <F> Try<F> transformWith(final @Nonnull ThrowingFunction<Exception, Try<F>> f, final @Nonnull ThrowingFunction<E, Try<F>> g) {
      Objects.requireNonNull(f, "f must not be null");
      Objects.requireNonNull(g, "g must not be null");
      try {
        return f.execute(exception);
      } catch (final Exception exc) {
        return failure(new FailedRecoveringException(exception, exc));
      }
    }

    @Nonnull
    @Override
    public Optional<E> toOptional() {
      return Optional.empty();
    }

    @Override
    public Iterator<E> iterator() {
      return new Iterator<E>() {
        @Override
        public boolean hasNext() {
          return false;
        }

        @Override
        public E next() {
          return null;
        }
      };
    }
  }

  @Nonnull
  public static <E> Try<E> success(final E element) {
    return new Success<>(element);
  }

  @Nonnull
  public static <E> Try<E> failure(final @Nonnull Exception exception) {
    Objects.requireNonNull(exception, "exception must not be null");
    return new Failure<>(exception);
  }

  @Nonnull
  public static <E> Try<E> execute(final @Nonnull Callable<E> callable) {
    Objects.requireNonNull(callable, "callable must not be null");
    try {
      return success(callable.call());
    } catch (final Exception exc) {
      return failure(exc);
    }
  }

  /**
   * Tries to run the given ThrowingRunnable and throws a runtime exception in case it fails.
   * <p>
   * If the given ThrowingRunnable throws a RuntimeException that Exception is rethrown.
   *
   * @param runnable A runnable which might throw a checked Exception.
   */
  public static void run(final @Nonnull ThrowingRunnable runnable) {
    Objects.requireNonNull(runnable, "runnable must not be null");
    try {
      runnable.run();
    } catch (final RuntimeException exc) {
      throw exc;
    } catch (final Exception exc) {
      throw new RuntimeException(exc);
    }
  }

  /**
   * Tries to run the given ThrowingRunnable and throws a runtime exception with the given message in case it fails.
   * <p>
   * If the given ThrowingRunnable throws a RuntimeException it is not rethrown but wrapped in a new RuntimeException
   * with the given message (just like a checked Exception).
   *
   * @param message  The message to create the RuntimeException with in case it fails.
   * @param runnable A runnable which might throw a checked Exception.
   */
  public static void run(final @Nonnull String message, final @Nonnull ThrowingRunnable runnable) {
    Objects.requireNonNull(message, "message must not be null");
    Objects.requireNonNull(runnable, "runnable must not be null");
    try {
      runnable.run();
    } catch (final Exception exc) {
      throw new RuntimeException(message, exc);
    }
  }
}
