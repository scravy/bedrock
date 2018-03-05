package com.simplaex.bedrock;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

interface SequenceMethods<P, Q, R extends SequenceMethods<P, Q, R>> {

  @SuppressWarnings("unchecked")
  Comparator<Object> nullAcceptingComparator = (left, right) -> {
    if (left == null && right == null) {
      return 0;
    }
    if (left == null) {
      return -1;
    }
    if (right == null) {
      return 1;
    }
    return ((Comparable) left).compareTo(right);
  };

  @Nonnegative
  int length();

  @Nonnull
  R shuffled(@Nonnull final Random random);

  @Nonnull
  R reversed();

  @Nonnull
  R sorted();

  @Nonnull
  R trimmedToSize();

  @Nonnull
  R subSequence(@Nonnegative final int beginOffset, @Nonnegative final int endOffset);

  @Nonnull
  R subSequenceView(@Nonnegative final int beginOffset, @Nonnegative final int endOffset);

  /**
   * Exactly the same as length().
   */
  @Nonnegative
  default int size() {
    return length();
  }

  default boolean isEmpty() {
    return length() == 0;
  }

  @Nonnull
  default String asString() {
    return asString("");
  }

  @Nonnull
  String asString(String delimiter);


  default boolean startsWith(@Nonnull final R sequence) {
    return takeView(sequence.length()).equals(sequence);
  }

  default boolean endsWith(@Nonnull final R sequence) {
    return takeRightView(sequence.length()).equals(sequence);
  }

  boolean exists(@Nonnull P predicate);

  boolean forAll(@Nonnull P predicate);

  @Nonnull
  default R shuffled() {
    return shuffled(ThreadLocalRandom.current());
  }

  @Nonnull
  default R init() {
    return subSequence(0, length() - 1);
  }

  @Nonnull
  default R initView() {
    return subSequenceView(0, length() - 1);
  }

  @Nonnull
  Seq<R> inits();

  @Nonnull
  Seq<R> initsView();

  @Nonnull
  default R tail() {
    return subSequence(1, length());
  }

  @Nonnull
  default R tailView() {
    return subSequenceView(1, length());
  }

  @Nonnull
  Seq<R> tails();

  @Nonnull
  Seq<R> tailsView();

  @Nonnull
  default R take(@Nonnegative final int length) {
    return subSequence(0, length);
  }

  @Nonnull
  default R takeView(@Nonnegative final int length) {
    return subSequenceView(0, length);
  }

  @Nonnull
  default R takeRight(@Nonnegative final int length) {
    return subSequence(length() - length, length());
  }

  @Nonnull
  default R takeRightView(@Nonnegative final int length) {
    return subSequenceView(length() - length, length());
  }

  @Nonnull
  default R drop(@Nonnegative final int length) {
    return subSequence(length, length());
  }

  @Nonnull
  default R dropView(@Nonnegative final int length) {
    return subSequenceView(length, length());
  }

  @Nonnull
  default R dropRight(@Nonnegative final int length) {
    return subSequence(0, length() - length);
  }

  @Nonnull
  default R dropRightView(@Nonnegative final int length) {
    return subSequenceView(0, length() - length);
  }

  @Nonnull
  Iterator<R> permutationsIterator();

  @Nonnull
  default Iterable<R> permutationsIterable() {
    return this::permutationsIterator;
  }

  @Nonnull
  default Stream<R> permutationsStream() {
    return StreamSupport.stream(permutationsIterable().spliterator(), false);
  }

  @Nonnull
  Seq<R> permutations();

  @Nonnull
  R takeWhile(@Nonnull P predicate);

  @Nonnull
  R takeWhileView(@Nonnull P predicate);

  @Nonnull
  R dropWhile(@Nonnull P predicate);

  @Nonnull
  R dropWhileView(@Nonnull P predicate);

  @Nonnull
  Pair<R, R> breakBy(@Nonnull P predicate);

  @Nonnull
  Pair<R, R> breakByView(@Nonnull P predicate);

  @Nonnull
  Pair<R, R> spanBy(@Nonnull P predicate);

  @Nonnull
  Pair<R, R> spanByView(@Nonnull P predicate);

  @Nonnull
  R filter(@Nonnull P predicate);

  @Nonnull
  R filterNot(@Nonnull P predicate);

  @Nonnull
  Seq<R> group();

  @Nonnull
  Seq<R> groupBy(@Nonnull Q operator);

  @Nonnull
  R distinct();

  @Nonnull
  Pair<R, R> partitionBy(@Nonnull P predicate);


}
