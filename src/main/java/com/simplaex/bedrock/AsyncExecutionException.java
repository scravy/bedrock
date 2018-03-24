package com.simplaex.bedrock;

import lombok.Getter;

public class AsyncExecutionException extends LightweightRuntimeException {

  @Getter
  public final Object reason;

  private static String message(final Object reason) {
    if (reason instanceof Throwable) {
      final Throwable throwable = (Throwable) reason;
      return throwable.getClass().getName() + ": " + throwable.getMessage();
    }
    if (reason instanceof String) {
      return (String) reason;
    }
    return reason.toString();
  }

  private static Throwable cause(final Object reason) {
    if (reason instanceof Throwable) {
      return (Throwable) reason;
    }
    return null;
  }

  @SuppressWarnings("WeakerAccess")
  public AsyncExecutionException(final Object reason) {
    super(message(reason), cause(reason));
    this.reason = reason;
  }
}
