package com.simplaex.bedrock;

public class LightweightException extends Exception {

  @Override
  public final synchronized Throwable fillInStackTrace() {
    return this;
  }
}
