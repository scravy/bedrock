package com.simplaex.bedrock;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

interface SequenceMethods<Predicate, BiPredicate, Sequence extends SequenceMethods<Predicate, BiPredicate, Sequence>> {

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
  Sequence shuffled(@Nonnull final Random random);

  @Nonnull
  Sequence reversed();

  @Nonnull
  Sequence sorted();

  @Nonnull
  Sequence trimmedToSize();

  @Nonnull
  Sequence subSequence(@Nonnegative final int beginOffset, @Nonnegative final int endOffset);

  @Nonnull
  Sequence subSequenceView(@Nonnegative final int beginOffset, @Nonnegative final int endOffset);

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


  default boolean startsWith(@Nonnull final Sequence sequence) {
    return takeView(sequence.length()).equals(sequence);
  }

  default boolean endsWith(@Nonnull final Sequence sequence) {
    return takeRightView(sequence.length()).equals(sequence);
  }

  boolean exists(@Nonnull Predicate predicate);

  boolean forAll(@Nonnull Predicate predicate);

  @Nonnull
  default Sequence shuffled() {
    return shuffled(ThreadLocalRandom.current());
  }

  @Nonnull
  default Sequence init() {
    return subSequence(0, length() - 1);
  }

  @Nonnull
  default Sequence initView() {
    return subSequenceView(0, length() - 1);
  }

  @Nonnull
  Seq<Sequence> inits();

  @Nonnull
  Seq<Sequence> initsView();

  @Nonnull
  default Sequence tail() {
    return subSequence(1, length());
  }

  @Nonnull
  default Sequence tailView() {
    return subSequenceView(1, length());
  }

  @Nonnull
  Seq<Sequence> tails();

  @Nonnull
  Seq<Sequence> tailsView();

  @Nonnull
  default Sequence take(@Nonnegative final int length) {
    return subSequence(0, length);
  }

  @Nonnull
  default Sequence takeView(@Nonnegative final int length) {
    return subSequenceView(0, length);
  }

  @Nonnull
  default Sequence takeRight(@Nonnegative final int length) {
    return subSequence(length() - length, length());
  }

  @Nonnull
  default Sequence takeRightView(@Nonnegative final int length) {
    return subSequenceView(length() - length, length());
  }

  @Nonnull
  default Sequence drop(@Nonnegative final int length) {
    return subSequence(length, length());
  }

  @Nonnull
  default Sequence dropView(@Nonnegative final int length) {
    return subSequenceView(length, length());
  }

  @Nonnull
  default Sequence dropRight(@Nonnegative final int length) {
    return subSequence(0, length() - length);
  }

  @Nonnull
  default Sequence dropRightView(@Nonnegative final int length) {
    return subSequenceView(0, length() - length);
  }

  @Nonnull
  Iterator<Sequence> permutationsIterator();

  @Nonnull
  default Iterable<Sequence> permutationsIterable() {
    return this::permutationsIterator;
  }

  @Nonnull
  default Stream<Sequence> permutationsStream() {
    return StreamSupport.stream(permutationsIterable().spliterator(), false);
  }

  @Nonnull
  Sequence distinct();

  @Nonnull
  Sequence takeWhile(@Nonnull Predicate predicate);

  @Nonnull
  Sequence takeWhileView(@Nonnull Predicate predicate);

  @Nonnull
  Sequence dropWhile(@Nonnull Predicate predicate);

  @Nonnull
  Sequence dropWhileView(@Nonnull Predicate predicate);

  @Nonnull
  Sequence filter(@Nonnull Predicate predicate);

  @Nonnull
  Sequence filterNot(@Nonnull Predicate predicate);

  @Nonnull
  Pair<Sequence, Sequence> breakBy(@Nonnull Predicate predicate);

  @Nonnull
  Pair<Sequence, Sequence> breakByView(@Nonnull Predicate predicate);

  @Nonnull
  Pair<Sequence, Sequence> spanBy(@Nonnull Predicate predicate);

  @Nonnull
  Pair<Sequence, Sequence> spanByView(@Nonnull Predicate predicate);

  @Nonnull
  Pair<Sequence, Sequence> partitionBy(@Nonnull Predicate predicate);

  @Nonnull
  Seq<Sequence> group();

  @Nonnull
  Seq<Sequence> groupBy(@Nonnull BiPredicate operator);

  @Nonnull
  Seq<Sequence> permutations();

}
