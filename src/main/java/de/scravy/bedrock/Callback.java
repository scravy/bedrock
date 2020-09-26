package de.scravy.bedrock;

import javax.annotation.Nonnull;
import java.util.Objects;

@FunctionalInterface
public interface Callback<R> {

  void call(final Object error, final R result) throws Exception;

  default void call(final Try<R> tryResult) {
    tryResult.consume(this::fail, this::success);
  }

  default void success(final R result) {
    try {
      call(null, result);
    } catch (final Exception exc) {
      Control.report(exc);
    }
  }

  default void fail(final Object error) {
    try {
      call(error, null);
    } catch (final Exception exc) {
      Control.report(exc);
    }
  }

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
