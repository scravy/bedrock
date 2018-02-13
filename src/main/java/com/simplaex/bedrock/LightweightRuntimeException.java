package com.simplaex.bedrock;

public class LightweightRuntimeException extends RuntimeException {

  @Override
  public final synchronized Throwable fillInStackTrace() {
    return this;
  }
}
