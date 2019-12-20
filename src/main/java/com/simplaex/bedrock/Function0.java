package com.simplaex.bedrock;

import java.util.function.Supplier;

@FunctionalInterface
public interface Function0<R> extends Supplier<R> {

  default Function0<R> memoizing() {
    return Control.memoizing(this);
  }

  static <R> Function0<R> from(final Supplier<R> supplier) {
    return supplier::get;
  }
}
