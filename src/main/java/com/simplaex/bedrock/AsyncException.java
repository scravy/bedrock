package com.simplaex.bedrock;

import lombok.EqualsAndHashCode;
import lombok.Value;

@EqualsAndHashCode(callSuper = false)
@Value
public class AsyncException extends LightweightRuntimeException {

  public final Object reason;

  public AsyncException(final Object reason) {
    this.reason = reason;
  }

  @Override
  public String getMessage() {
    return reason.toString();
  }
}
