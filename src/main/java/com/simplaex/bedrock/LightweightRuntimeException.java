package com.simplaex.bedrock;

public class LightweightRuntimeException extends RuntimeException {

  public LightweightRuntimeException() {
    super();
  }

  public LightweightRuntimeException(final String message) {
    super(message);
  }

  public LightweightRuntimeException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public LightweightRuntimeException(final Throwable cause) {
    super(cause);
  }

  @Override
  public final synchronized Throwable fillInStackTrace() {
    return this;
  }
}
