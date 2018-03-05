package com.simplaex.bedrock;

import lombok.Getter;
import lombok.SneakyThrows;

import javax.annotation.Nonnull;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Predicate;

public class Promise<T> {

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

  public static <T> Promise<T> promise() {
    return new Promise<>(State.PENDING, null);
  }

  public static <T> Promise<T> fulfilled(final T value) {
    return new FulfilledPromise<>(value);
  }

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
  }

  private static class MappedPromise extends Promise<Object> {

    private ThrowingFunction transform;

    private MappedPromise(final ThrowingFunction transform) {
      super(State.PENDING, null);
      this.transform = transform;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void fulfill(final Object result) {
      try {
        super.fulfill(transform.execute(result));
      } catch (final Exception exc) {
        Try.run(() -> super.fail(exc));
      } finally {
        transform = null;
      }
    }

  }

  private final Deque<MappedPromise> children = new ArrayDeque<>(1);

  public void fulfill(final T result) {
    synchronized (children) {
      if (state != State.PENDING) {
        throw new IllegalStateException("Already fulfilled (" + state + ").");
      }
      this.result = result;
      state = State.FULFILLED;
      while (!children.isEmpty()) {
        final MappedPromise child = children.pop();
        try {
          child.fulfill(result);
        } catch (final Exception exc) {
          Try.run(() -> child.fail(exc));
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
        final MappedPromise child = children.pop();
        Try.run(() -> child.fail(exc));
      }
      children.notifyAll();
    }
  }

  @SuppressWarnings("unchecked")
  @SneakyThrows
  public T get() {
    for (; ; ) {
      synchronized (children) {
        switch (state) {
          case PENDING:
            children.wait();
            break;
          case FULFILLED:
            return (T) result;
          case FAILED:
            throw (Throwable) result;
        }
      }
    }
  }

  @SneakyThrows
  public void waitFor() {
    for (; ; ) {
      synchronized (children) {
        switch (state) {
          case PENDING:
            children.wait();
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
  public <U> Promise<U> map(final @Nonnull ThrowingFunction<T, U> func) {
    synchronized (children) {
      if (state == State.PENDING) {
        final MappedPromise mappedPromise = new MappedPromise(func);
        children.push(mappedPromise);
        return (Promise<U>) mappedPromise;
      }
    }
    if (state == State.FULFILLED) {
      try {
        return fulfilled(func.execute((T) result));
      } catch (final Exception exc) {
        return failed(exc);
      }
    }
    return failed((Throwable) result);
  }

  @Nonnull
  public Promise<T> filter(final @Nonnull Predicate<T> predicate) {
    return map(value -> {
      if (predicate.test(value)) {
        return value;
      } else {
        throw new ValueDidNotSatisfyPredicateException(predicate, value);
      }
    });
  }

}
