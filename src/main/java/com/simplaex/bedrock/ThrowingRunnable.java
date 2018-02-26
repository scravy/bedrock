package com.simplaex.bedrock;

@FunctionalInterface
public interface ThrowingRunnable {

  void run() throws Exception;
}
