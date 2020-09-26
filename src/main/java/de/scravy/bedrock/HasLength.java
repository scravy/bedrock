package de.scravy.bedrock;

import javax.annotation.Nonnegative;

@FunctionalInterface
public interface HasLength extends HasLengthAtLeast {
  @Nonnegative
  int length();

  @Override
  default boolean lengthAtLeast(@Nonnegative final int length) {
    return length() <= length;
  }
}
