package com.simplaex.bedrock;

import javax.annotation.Nonnull;
import java.util.Objects;

@FunctionalInterface
public interface Callback<R> {

  void call(final Object error, final R result) throws Exception;

  default <T> Callback<T> after(@Nonnull final ThrowingFunction<T, R> function) {
    Objects.requireNonNull(function, "'function' must not be null.");
    return (err, res) -> {
      if (err != null) {
        call(err, null);
      } else {
        final Try<R> tryResult = Try.execute(() -> function.execute(res));
        if (tryResult.isFailure()) {
          call(tryResult.getException(), null);
        } else {
          call(null, tryResult.get());
        }
      }
    };
  }

}
