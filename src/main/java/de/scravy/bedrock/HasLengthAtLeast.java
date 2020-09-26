package de.scravy.bedrock;

import javax.annotation.Nonnegative;

@FunctionalInterface
public interface HasLengthAtLeast {
  /**
   * Checks whether this has at least that much elements.
   */
  boolean lengthAtLeast(@Nonnegative final int length);
}
