package de.scravy.bedrock;

import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class Promise<T> implements Callback<T> {

  public enum State {
    PENDING,
    FULFILLED,
    FAILED
  }

  @Nonnull
  @Getter
  private volatile State state;
  private volatile Object result;

  private Promise(@Nonnull final State state, final Object result) {
    this.state = state;
    this.result = result;
  }

  @Nonnull
  public static <T> Promise<T> promise() {
    return new Promise<>(State.PENDING, null);
  }

  @Nonnull
  public static <T> Promise<T> fulfilled(final T value) {
    return new FulfilledPromise<>(value);
  }

  @Nonnull
  public static <T> Promise<T> failed(final Throwable value) {
    return new FailedPromise<>(value);
  }

  private static class FulfilledPromise<T> extends Promise<T> {
    private FulfilledPromise(final T result) {
      super(State.FULFILLED, result);
    }

    @Override
    public void fulfill(final T result) {
      throw new IllegalStateException("Already fulfilled (" + getState() + ").");
    }

    @Override
    public void fail(final Throwable result) {
      throw new IllegalStateException("Already fulfilled (" + getState() + ").");
    }

    @Override
    @Nonnull
    public Promise<T> onComplete(@Nonnull final Callback<T> callback) {
      Try.unfailable(() -> callback.call(null, getValue()));
      return this;
    }
  }

  private static class FailedPromise<T> extends Promise<T> {
    private FailedPromise(final Throwable exc) {
      super(State.FAILED, exc);
    }

    @Override
    public void fulfill(final T result) {
      throw new IllegalStateException("Already fulfilled (" + getState() + ").");
    }

    @Override
    public void fail(final Throwable result) {
      throw new IllegalStateException("Already fulfilled (" + getState() + ").");
    }

    @Override
    @Nonnull
    public Promise<T> onComplete(@Nonnull final Callback<T> callback) {
      Try.unfailable(() -> callback.call(getException(), null));
      return this;
    }
  }

  private abstract static class UntypedPromise extends Promise<Object> {
    private UntypedPromise() {
      super(State.PENDING, null);
    }
  }

  private static class MappedPromise extends UntypedPromise {

    private ThrowingFunction transform;

    private MappedPromise(final ThrowingFunction transform) {
      this.transform = transform;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void fulfill(final Object result) {
      try {
        super.fulfill(transform.execute(result));
      } catch (final Exception exc) {
        Try.unfailable(() -> super.fail(exc));
      } finally {
        transform = null;
      }
    }
  }

  private static class CallbackPromise extends UntypedPromise {
    private final Callback<Object> callback;

    @SuppressWarnings("unchecked")
    private <T> CallbackPromise(final Callback<T> callback) {
      this.callback = (Callback<Object>) callback;
    }

    @Override
    public void fulfill(final Object result) {
      Try.unfailable(() -> callback.call(null, result));
    }

    @Override
    public void fail(final Throwable exc) {
      Try.unfailable(() -> callback.call(exc, null));
    }
  }

  private final Deque<UntypedPromise> children = new ArrayDeque<>(1);

  public void fulfill(final T result) {
    synchronized (children) {
      if (state != State.PENDING) {
        throw new IllegalStateException("Already fulfilled (" + state + ").");
      }
      this.result = result;
      state = State.FULFILLED;
      while (!children.isEmpty()) {
        final UntypedPromise child = children.pop();
        try {
          child.fulfill(result);
        } catch (final Exception exc) {
          Try.unfailable(() -> child.fail(exc));
        }
      }
      children.notifyAll();
    }
  }

  public void fail(final Throwable exc) {
    synchronized (children) {
      if (state != State.PENDING) {
        throw new IllegalStateException("Already fulfilled (" + state + ").");
      }
      this.result = exc;
      state = State.FAILED;
      while (!children.isEmpty()) {
        final UntypedPromise child = children.pop();
        Try.unfailable(() -> child.fail(exc));
      }
      children.notifyAll();
    }
  }

  @SuppressWarnings("unchecked")
  public T get() throws AsyncExecutionException {
    for (; ; ) {
      synchronized (children) {
        switch (state) {
          case PENDING:
            try {
              children.wait();
            } catch (final InterruptedException exc) {
              throw new AsyncExecutionException(exc);
            }
            break;
          case FULFILLED:
            return (T) result;
          case FAILED:
            throw new AsyncExecutionException(result);
        }
      }
    }
  }

  public void waitFor() {
    for (; ; ) {
      synchronized (children) {
        switch (state) {
          case PENDING:
            try {
              children.wait();
            } catch (final InterruptedException exc) {
              throw new AsyncExecutionException(exc);
            }
            break;
          case FULFILLED:
          case FAILED:
            return;
        }
      }
    }
  }

  public Throwable getException() {
    if (state == State.FAILED) {
      return (Throwable) result;
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  public T getValue() {
    if (state == State.FULFILLED) {
      return (T) result;
    }
    return null;
  }

  public boolean isSuccess() {
    return state == State.FULFILLED;
  }

  public boolean isFailure() {
    return state == State.FAILED;
  }

  public boolean isPending() {
    return state == State.PENDING;
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  public <U> Promise<U> map(final @Nonnull ThrowingFunction<T, U> function) {
    Objects.requireNonNull(function, "'function' must not be null.");
    synchronized (children) {
      if (state == State.PENDING) {
        final MappedPromise mappedPromise = new MappedPromise(function);
        children.push(mappedPromise);
        return (Promise<U>) mappedPromise;
      }
    }
    if (state == State.FULFILLED) {
      try {
        return fulfilled(function.execute((T) result));
      } catch (final Exception exc) {
        return failed(exc);
      }
    }
    return failed((Throwable) result);
  }

  @Nonnull
  public Promise<T> filter(final @Nonnull Predicate<T> predicate) {
    Objects.requireNonNull(predicate, "'predicate' must not be null.");
    return map(value -> {
      if (predicate.test(value)) {
        return value;
      } else {
        throw new ValueDidNotSatisfyPredicateException(predicate, value);
      }
    });
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  public Promise<T> onComplete(final @Nonnull Callback<T> callback) {
    Objects.requireNonNull(callback, "'callback' must not be null.");
    synchronized (children) {
      if (state == State.PENDING) {
        final CallbackPromise callbackPromise = new CallbackPromise(callback);
        children.push(callbackPromise);
        return this;
      }
    }
    if (state == State.FULFILLED) {
      Try.unfailable(() -> callback.call(null, (T) result));
    } else {
      Try.unfailable(() -> callback.call(result, null));
    }
    return this;
  }

  @Nonnull
  public Promise<T> onSuccess(final @Nonnull ThrowingConsumer<T> consumer) {
    return onComplete((error, result) -> {
      if (error == null) {
        Try.unfailable(() -> consumer.accept(result));
      }
    });
  }

  @Nonnull
  public Promise<T> onFailure(final @Nonnull ThrowingConsumer<Throwable> consumer) {
    return onComplete((error, result) -> {
      if (error instanceof Throwable) {
        Try.unfailable(() -> consumer.accept((Throwable) error));
      }
    });
  }

  @Nonnull
  public final Optional<T> toOptional() {
    waitFor();
    if (isSuccess()) {
      return Optional.ofNullable(getValue());
    } else {
      return Optional.empty();
    }
  }

  @Nonnull
  public final Try<T> toTry() {
    waitFor();
    if (isSuccess()) {
      return Try.success(getValue());
    } else {
      final Throwable exception = getException();
      if (exception instanceof Exception) {
        return Try.failure((Exception) exception);
      }
      return Try.failure(new AsyncExecutionException(exception));
    }
  }

  @Override
  public final void call(final Object error, final T result) {
    if (error == null) {
      fulfill(result);
    } else {
      if (error instanceof Throwable) {
        fail((Throwable) error);
      } else {
        fail(new AsyncExecutionException(error));
      }
    }
  }

}
