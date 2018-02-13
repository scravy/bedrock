package com.simplaex.bedrock;

@FunctionalInterface
public interface ThrowingConsumer<A> {
  void accept(final A arg) throws Exception;
}
