package com.simplaex.bedrock;

import lombok.Getter;
import lombok.SneakyThrows;

import java.util.ArrayDeque;
import java.util.Deque;

public final class Promise<T> {

  private Promise() {
  }

  public static <T> Promise<T> promise() {
    return new Promise<>();
  }

  private final Deque<Callback<T>> callbacks = new ArrayDeque<>();

  public enum State {
    PENDING,
    FULFILLED,
    FAILED
  }

  @Getter
  private volatile State state = State.PENDING;
  private volatile Object result = null;

  public void fulfill(final T result) {
    synchronized (callbacks) {
      if (state != State.PENDING) {
        throw new IllegalStateException("Already fulfilled (" + state + ").");
      }
      this.result = result;
      state = State.FULFILLED;
      callbacks.notifyAll();
    }
  }

  public void fail(final Throwable exc) {
    synchronized (callbacks) {
      if (state != State.PENDING) {
        throw new IllegalStateException("Already fulfilled (" + state + ").");
      }
      this.result = exc;
      state = State.FAILED;
      callbacks.notifyAll();
    }
  }

  @SuppressWarnings("unchecked")
  @SneakyThrows
  public T get() {
    for (; ; ) {
      switch (state) {
        case PENDING:
          synchronized (callbacks) {
            callbacks.wait();
          }
          break;
        case FULFILLED:
          return (T) result;
        case FAILED:
          throw (Throwable) result;
      }
    }
  }

}
