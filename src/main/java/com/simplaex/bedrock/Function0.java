package com.simplaex.bedrock;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

@FunctionalInterface
public interface Function0<R> extends Supplier<R> {

  @Nonnull
  default Function0<R> memoizing() {
    return Control.memoizing(this);
  }

  @Nonnull
  static <R> Function0<R> from(final Supplier<R> supplier) {
    return supplier::get;
  }
}
