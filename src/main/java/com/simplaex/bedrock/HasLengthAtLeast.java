package com.simplaex.bedrock;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface HasLengthAtLeast {
  /**
   * Checks whether this has at least that much elements.
   */
  boolean lengthAtLeast(@Nonnull final int length);
}
