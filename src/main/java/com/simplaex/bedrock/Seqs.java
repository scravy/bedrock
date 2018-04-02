package com.simplaex.bedrock;

import lombok.experimental.UtilityClass;

import java.util.Objects;

@UtilityClass
public class Seqs {

  public static <E extends Comparable<? super E>> E minimum(final Seq<E> seq) {
    if (seq instanceof SeqSimpleSorted) {
      return seq.head();
    }
    Objects.requireNonNull(seq, "'seq' must not be null");
    return seq.minimumBy(Comparable::compareTo);
  }

  public static <E extends Comparable<? super E>> E maximum(final Seq<E> seq) {
    if (seq instanceof SeqSimpleSorted) {
      return seq.last();
    }
    Objects.requireNonNull(seq, "'seq' must not be null");
    return seq.maximumBy(Comparable::compareTo);
  }

  public static int intSum(final Seq<Integer> seq) {
    Objects.requireNonNull(seq, "'seq' must not be null");
    return seq.foldl(Operators::plus, 0);
  }

  public static long longSum(final Seq<Long> seq) {
    Objects.requireNonNull(seq, "'seq' must not be null");
    return seq.foldl(Operators::plus, 0L);
  }

  public static double doubleSum(final Seq<Double> seq) {
    Objects.requireNonNull(seq, "'seq' must not be null");
    return seq.foldl(Operators::plus, 0.0);
  }

  public static int intProduct(final Seq<Integer> seq) {
    Objects.requireNonNull(seq, "'seq' must not be null");
    return seq.foldl(Operators::plus, 1);
  }

  public static long longProduct(final Seq<Long> seq) {
    Objects.requireNonNull(seq, "'seq' must not be null");
    return seq.foldl(Operators::plus, 1L);
  }

  public static double doubleProduct(final Seq<Double> seq) {
    Objects.requireNonNull(seq, "'seq' must not be null");
    return seq.foldl(Operators::plus, 1.0);
  }

  public static boolean and(final Seq<Boolean> seq) {
    Objects.requireNonNull(seq, "'seq' must not be null");
    return seq.foldr((left, right) -> left && right, true);
  }

  public static boolean or(final Seq<Boolean> seq) {
    Objects.requireNonNull(seq, "'seq' must not be null");
    return seq.foldr((left, right) -> left || right, false);
  }

}
