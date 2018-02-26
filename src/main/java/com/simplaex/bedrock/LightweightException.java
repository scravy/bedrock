package com.simplaex.bedrock;

public class LightweightException extends Exception {

  public LightweightException() {
    super();
  }

  public LightweightException(final String message) {
    super(message);
  }

  public LightweightException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public LightweightException(final Throwable cause) {
    super(cause);
  }

  @Override
  public final synchronized Throwable fillInStackTrace() {
    return this;
  }
}
