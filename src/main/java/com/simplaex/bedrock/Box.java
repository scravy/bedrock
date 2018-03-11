package com.simplaex.bedrock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.function.Function;

@Data
@NoArgsConstructor
@AllArgsConstructor
public final class Box<T> {

  private T value;

  public T apply(final Function<T, T> function) {
    Objects.requireNonNull(function, "'function' must not be null.");
    setValue(function.apply(getValue()));
    return getValue();
  }
}
