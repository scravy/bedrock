package com.simplaex.bedrock;

import lombok.Getter;

public class ExecutionException extends RuntimeException {

  @Getter
  private final Seq<Throwable> causes;

  public ExecutionException(final Throwable... causes) {
    this.causes = Seq.ofArray(causes);
  }

  public ExecutionException(final Seq<Throwable> causes) {
    this.causes = causes;
  }

}
