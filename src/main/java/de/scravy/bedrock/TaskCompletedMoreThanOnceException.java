package de.scravy.bedrock;

public class TaskCompletedMoreThanOnceException extends LightweightRuntimeException {

  private final Object task;
  private final Object error;
  private final Object result;
  private final Object duplicateError;
  private final Object duplicateResult;

  public TaskCompletedMoreThanOnceException(
    final Object task,
    final Object error,
    final Object result,
    final Object duplicateError,
    final Object duplicateResult
  ) {
    this.task = task;
    this.error = error;
    this.result = result;
    this.duplicateError = duplicateError;
    this.duplicateResult = duplicateResult;
  }

  @Override
  public String getMessage() {
    return "The task " +
      task +
      " was already fulfilled (error=" +
      error +
      ", result=" +
      result +
      "), but was attempted to be fulfilled another time (error=" +
      duplicateError +
      ", result=" +
      duplicateResult +
      ")";
  }

}
