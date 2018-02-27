package com.simplaex.bedrock;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

public class Promise<T> {

  public enum State {
    PENDING,
    FULFILLED,
    FAILED
  }

  @Getter
  private volatile State state;
  private volatile Object result;

  private Promise(final State state, final Object result) {
    this.state = state;
    this.result = result;
  }

  public static <T> Promise<T> promise() {
    return new Promise<>(State.PENDING, null);
  }

  private static class FulfilledPromise<T> extends Promise<T> {
    private FulfilledPromise(final T result) {
      super(State.FULFILLED, result);
    }
  }

  private static class FailedPromise<T> extends Promise<T> {
    private FailedPromise(final Throwable exc) {
      super(State.FAILED, exc);
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
  public <U> Promise<U> map(final ThrowingFunction<T, U> func) {
    synchronized (children) {
      if (state == State.PENDING) {
        final MappedPromise mappedPromise = new MappedPromise(func);
        children.push(mappedPromise);
        return (Promise<U>) mappedPromise;
      }
    }
    if (state == State.FULFILLED) {
      try {
        return new FulfilledPromise<>(func.execute((T) result));
      } catch (final Exception exc) {
        return new FailedPromise<>(exc);
      }
    }
    return new FailedPromise<>((Throwable) result);
  }

  public Promise<T> filter(final Predicate<T> predicate) {
    return map(value -> {
      if (predicate.test(value)) {
        return value;
      } else {
        throw new ValueDidNotSatisfyPredicateException(predicate, value);
      }
    });
  }

}
