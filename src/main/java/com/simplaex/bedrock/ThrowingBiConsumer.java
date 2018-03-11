package com.simplaex.bedrock;

@FunctionalInterface
public interface ThrowingBiConsumer<A, B> {

  void accept(final A arg1, final B arg2) throws Exception;
}
