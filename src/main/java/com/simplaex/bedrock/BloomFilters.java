package com.simplaex.bedrock;

import lombok.experimental.UtilityClass;

/**
 * Utility for calculating the size of and false positives probabilities for
 * bloom filters as in googles guava library.
 */
@UtilityClass
public final class BloomFilters {

  private static final double Log2Square = Math.log(2) * Math.log(2);

  /**
   * Returns the number of bits given the number of expected insertions
   * and a desired false positives probability.
   */
  public static long numBits(final long expectedInsertions, final double falsePositivesProbability) {
    return (long) (-((double) expectedInsertions) * Math.log(falsePositivesProbability) / Log2Square);
  }

  /**
   * Returns the number of expected insertions given the number of bits desired
   * and a desired false positives probability.
   */
  public static long expectedInsertions(final long numBits, final double falsePositivesProbability) {
    return (long) -(((double) numBits) * Log2Square / Math.log(falsePositivesProbability));
  }

  /**
   * Returns the false positives probability given the number of bits desired
   * and the expected insertions.
   */
  public static double falsePositivesProbability(final long numBits, final long expectedInsertions) {
    return Math.exp(((double) numBits) * Log2Square / (-((double) expectedInsertions)));
  }
}
