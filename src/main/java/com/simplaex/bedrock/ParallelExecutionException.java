package com.simplaex.bedrock;

import lombok.Getter;

public class ParallelExecutionException extends RuntimeException {

  @Getter
  private final Seq<Throwable> causes;

  public ParallelExecutionException(final Throwable... causes) {
    this.causes = Seq.ofArray(causes);
  }

  public ParallelExecutionException(final Seq<Throwable> causes) {
    this.causes = causes;
  }

}
