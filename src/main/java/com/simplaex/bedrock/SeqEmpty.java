package com.simplaex.bedrock;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

class SeqEmpty extends Seq<Object> {

  static Seq<Object> EMPTY = new SeqEmpty();

  private SeqEmpty() {

  }

  @Override
  @Nonnegative
  public int length() {
    return 0;
  }

  @Override
  public Object get(@Nonnegative int index) {
    throw new IndexOutOfBoundsException();
  }

  @Nonnull
  @Override
  public Seq<Object> reversed() {
    return this;
  }

  @Nonnull
  @Override
  public Seq<Object> sorted() {
    return this;
  }

  @Nonnull
  @Override
  public Seq<Object> sortedBy(@Nonnull final Comparator<? super Object> comparator) {
    Objects.requireNonNull(comparator);
    return this;
  }

  @Nonnull
  @Override
  public Seq<Object> shuffled(@Nonnull final Random random) {
    Objects.requireNonNull(random);
    return this;
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  @Override
  public <T> Seq<T> map(@Nonnull final Function<? super Object, ? extends T> function) {
    Objects.requireNonNull(function);
    return (Seq<T>) this;
  }

  @Override
  @Nonnull
  public Seq<Object> subSequence(@Nonnegative final int beginOffset, @Nonnegative final int endOffset) {
    return this;
  }

  @Override
  @Nonnull
  public Seq<Object> subSequenceView(@Nonnegative final int beginOffset, @Nonnegative final int endOffset) {
    return this;
  }

  @Override
  @Nonnull
  public Seq<Seq<Object>> inits() {
    return empty();
  }

  @Override
  @Nonnull
  public Seq<Seq<Object>> tails() {
    return empty();
  }

  @Nonnegative
  public int count(@Nullable final Object e) {
    return 0;
  }

  @Nonnegative
  public int countBy(@Nonnull final Predicate<? super Object> predicate) {
    Objects.requireNonNull(predicate);
    return 0;
  }

  public int find(@Nullable final Object e) {
    return -1;
  }

  public int findBy(@Nonnull final Predicate<? super Object> predicate) {
    return -1;
  }

  public boolean contains(@Nullable final Object e) {
    return false;
  }

  public boolean exists(@Nonnull final Predicate<? super Object> predicate) {
    return false;
  }

  public boolean forAll(@Nonnull final Predicate<? super Object> predicate) {
    return true;
  }

  @Override
  public boolean isEmpty() {
    return true;
  }

  @Nonnull
  @Override
  public Seq<Object> trimmedToSize() {
    return this;
  }

  @Nonnull
  @Override
  public Object[] toArray() {
    return new Object[0];
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  @Override
  public Object[] toArray(@Nonnull final Class<Object> evidence) {
    Objects.requireNonNull(evidence);
    return (Object[]) Array.newInstance(evidence, 0);
  }

  @Nonnull
  @Override
  public List<Object> toList() {
    return Collections.emptyList();
  }

  @Nonnull
  @Override
  public Stream<Object> stream() {
    return Stream.empty();
  }

  @Override
  int calculateHashCode() {
    return 1;
  }
}
