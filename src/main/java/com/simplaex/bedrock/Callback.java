package com.simplaex.bedrock;

@FunctionalInterface
public interface Callback<R> {

  void call(final Object error, final R result) throws Exception;

}
