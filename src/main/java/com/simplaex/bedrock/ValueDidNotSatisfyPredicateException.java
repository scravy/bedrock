package com.simplaex.bedrock;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

@EqualsAndHashCode(callSuper = false)
public class ValueDidNotSatisfyPredicateException extends Exception {
  @Getter
  private final Predicate<?> predicate;

  @Getter
  private final Object value;

  public ValueDidNotSatisfyPredicateException(@Nonnull final Predicate<?> predicate, final Object value) {
    this.predicate = predicate;
    this.value = value;
  }
}
