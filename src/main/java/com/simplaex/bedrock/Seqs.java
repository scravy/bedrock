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
    return seq.foldl(Operators::times, 1);
  }

  public static long longProduct(final Seq<Long> seq) {
    Objects.requireNonNull(seq, "'seq' must not be null");
    return seq.foldl(Operators::times, 1L);
  }

  public static double doubleProduct(final Seq<Double> seq) {
    Objects.requireNonNull(seq, "'seq' must not be null");
    return seq.foldl(Operators::times, 1.0);
  }

  public static boolean and(final Seq<Boolean> seq) {
    Objects.requireNonNull(seq, "'seq' must not be null");
    return seq.foldr((left, right) -> left && right, true);
  }

  public static boolean or(final Seq<Boolean> seq) {
    Objects.requireNonNull(seq, "'seq' must not be null");
    return seq.foldr((left, right) -> left || right, false);
  }

  public static <A, B> int commonPrefixLength(final Seq<A> as, final Seq<B> bs) {
    Objects.requireNonNull(as, "'as' must not be null");
    Objects.requireNonNull(bs, "'bs' must not be null");

    final int length = Math.min(as.size(), bs.size());

    int commonPrefixLength = 0;
    for (int i = 0; i < length; i += 1) {
      final A a = as.get(i);
      final B b = bs.get(i);

      if (a == null) {
        if (b == null) {
          commonPrefixLength += 1;
          continue;
        }
        break;
      }
      if (a.equals(b)) {
        commonPrefixLength += 1;
        continue;
      }
      break;
    }

    return commonPrefixLength;
  }

  public static <E, A extends E, B extends E> Seq<A> commonPrefix(final Seq<A> as, final Seq<B> bs) {
    final int commonPrefixLength = commonPrefixLength(as, bs);
    return as.subSequence(0, commonPrefixLength);
  }

  public static <E, A extends E, B extends E> Seq<A> commonPrefixView(final Seq<A> as, final Seq<B> bs) {
    final int commonPrefixLength = commonPrefixLength(as, bs);
    return as.subSequenceView(0, commonPrefixLength);
  }

}
