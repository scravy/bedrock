package com.simplaex.bedrock;

import lombok.val;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class Seq<E>
  implements Serializable, RandomAccess, Iterable<E>, SequenceMethods<Seq<E>> {

  final Object[] backingArray;

  Seq(final Object[] array) {
    backingArray = array;
  }

  public abstract E get(@Nonnegative final int index);

  @Override
  @Nonnull
  public Seq<E> shuffled(@Nonnull final Random random) {
    Objects.requireNonNull(random);
    final Object[] array = toArray();
    final int len = array.length;
    for (int i = 0; i < len; i += 1) {
      swap(array, i, random.nextInt(len));
    }
    return new SeqSimple<>(array);
  }

  @Nonnull
  public abstract Seq<E> sortedBy(@Nonnull final Comparator<? super E> comparator);

  @Nonnull
  public abstract E[] toArray(@Nonnull final Class<E> clazz);

  @Nonnull
  public abstract Object[] toArray();

  @Nonnull
  public String asString() {
    return stream().map(Objects::toString).collect(Collectors.joining(""));
  }

  @Nonnegative
  public int count(@Nullable final E e) {
    final int len = length();
    int c = 0;
    for (int i = 0; i < len; i += 1) {
      final E el = get(i);
      if (el == e || el != null && el.equals(e)) {
        c += 1;
      }
    }
    return c;
  }

  @Nonnegative
  public int countBy(@Nonnull final Predicate<? super E> predicate) {
    Objects.requireNonNull(predicate);
    final int len = length();
    int c = 0;
    for (int i = 0; i < len; i += 1) {
      final E el = get(i);
      if (predicate.test(el)) {
        c += 1;
      }
    }
    return c;
  }

  public int find(@Nullable final E e) {
    final int len = length();
    for (int i = 0; i < len; i += 1) {
      final E el = get(i);
      if (el == e || el != null && el.equals(e)) {
        return i;
      }
    }
    return -1;
  }

  public int findBy(@Nonnull final Predicate<? super E> e) {
    Objects.requireNonNull(e);
    final int len = length();
    for (int i = 0; i < len; i += 1) {
      final E el = get(i);
      if (e.test(el)) {
        return i;
      }
    }
    return -1;
  }

  public boolean contains(@Nullable final E e) {
    return find(e) > -1;
  }

  public boolean exists(@Nonnull final Predicate<E> predicate) {
    return findBy(predicate) > -1;
  }

  public boolean forAll(@Nonnull final Predicate<E> predicate) {
    return countBy(predicate) == length();
  }

  void checkBounds(final int index) {
    if (index >= length() || index < 0) {
      throw new IndexOutOfBoundsException();
    }
  }

  @Nonnull
  public <F> Seq<F> map(@Nonnull final Function<E, F> f) {
    Objects.requireNonNull(f);
    final Object[] array = new Object[length()];
    int i = 0;
    for (final E e : this) {
      array[i++] = f.apply(e);
    }
    return new SeqSimple<>(array);
  }

  @Nonnull
  public <F> Seq<F> flatMap(@Nonnull final Function<E, Seq<F>> f) {
    Objects.requireNonNull(f);
    @SuppressWarnings("unchecked") final Seq<F>[] array = (Seq<F>[]) new Seq[length()];
    int i = 0;
    int c = 0;
    for (final E e : this) {
      final Seq<F> result = f.apply(e);
      c += result.length();
      array[i++] = result;
    }
    final Object[] resultArray = new Object[c];
    i = 0;
    for (final Seq<F> s : array) {
      for (final F e : s) {
        resultArray[i++] = e;
      }
    }
    return new SeqSimple<>(resultArray);
  }

  @Nonnull
  public <A> Seq<Pair<E, A>> zip(@Nonnull final Seq<A> a) {
    return zipWith(Pair::new, a);
  }

  @Nonnull
  public <A, C> Seq<C> zipWith(@Nonnull final BiFunction<E, A, C> f, @Nonnull final Seq<A> a) {
    Objects.requireNonNull(f);
    Objects.requireNonNull(a);
    final int len = Math.min(length(), a.length());
    final Object[] arr = new Object[len];
    for (int i = 0; i < len; i += 1) {
      arr[i] = f.apply(get(i), a.get(i));
    }
    return new SeqSimple<>(arr);
  }

  @Nonnull
  public <A> A foldl(@Nonnull final BiFunction<A, E, A> f, final A startValue) {
    Objects.requireNonNull(f);
    A acc = startValue;
    for (int i = 0; i < length(); i += 1) {
      acc = f.apply(acc, get(i));
    }
    return acc;
  }

  @Nonnull
  public <A> A foldr(@Nonnull final BiFunction<E, A, A> f, final A startValue) {
    Objects.requireNonNull(f);
    A acc = startValue;
    for (int i = length() - 1; i >= 0; i -= 1) {
      acc = f.apply(get(i), acc);
    }
    return acc;
  }

  @Nonnull
  public Pair<Seq<E>, Seq<E>> partitionBy(@Nonnull final Predicate<E> p) {
    Objects.requireNonNull(p);
    final int sizeHint = length() / 2 + 1;
    val b1 = Seq.<E>builder(sizeHint);
    val b2 = Seq.<E>builder(sizeHint);
    forEach(x -> {
      if (p.test(x)) {
        b1.add(x);
      } else {
        b2.add(x);
      }
    });
    return Pair.of(b1.result(), b2.result());
  }

  @Nonnull
  public Seq<E> filter(@Nonnull final Predicate<E> p) {
    Objects.requireNonNull(p);
    final int sizeHint = length() / 2;
    val b = Seq.<E>builder(sizeHint);
    forEach(x -> {
      if (p.test(x)) {
        b.add(x);
      }
    });
    return b.result();
  }

  @Nonnull
  public Seq<E> filterNot(@Nonnull final Predicate<E> p) {
    return filter(p.negate());
  }

  @Nonnull
  public Seq<E> takeWhile(@Nonnull final Predicate<E> p) {
    int i = 0;
    while (i < length() && p.test(get(i))) {
      i += 1;
    }
    return take(i);
  }

  @Nonnull
  public Seq<E> takeWhileView(@Nonnull final Predicate<E> p) {
    int i = 0;
    while (i < length() && p.test(get(i))) {
      i += 1;
    }
    return takeView(i);
  }

  @Nonnull
  public Seq<E> dropWhile(@Nonnull final Predicate<E> p) {
    int i = 0;
    while (i < length() && p.test(get(i))) {
      i += 1;
    }
    return drop(i);
  }

  @Nonnull
  public Seq<E> dropWhileView(@Nonnull final Predicate<E> p) {
    int i = 0;
    while (i < length() && p.test(get(i))) {
      i += 1;
    }
    return dropView(i);
  }

  @Override
  @Nonnull
  public Seq<Seq<E>> inits() {
    final int len = length();
    @SuppressWarnings("unchecked") final Seq<E>[] seqs = (Seq<E>[]) new Seq[len];
    for (int i = 0; i < len; i += 1) {
      seqs[i] = take(i + 1);
    }
    return new SeqSimple<>(seqs);
  }

  @Override
  @Nonnull
  public Seq<Seq<E>> initsView() {
    final int len = length();
    @SuppressWarnings("unchecked") final Seq<E>[] seqs = (Seq<E>[]) new Seq[len];
    for (int i = 0; i < len; i += 1) {
      seqs[i] = takeView(i + 1);
    }
    return new SeqSimple<>(seqs);
  }

  @Override
  @Nonnull
  public Seq<Seq<E>> tails() {
    final int len = length();
    @SuppressWarnings("unchecked") final Seq<E>[] seqs = (Seq<E>[]) new Seq[len];
    for (int i = 0; i < len; i += 1) {
      seqs[i] = takeRight(i + 1);
    }
    return new SeqSimple<>(seqs);
  }

  @Override
  @Nonnull
  public Seq<Seq<E>> tailsView() {
    final int len = length();
    @SuppressWarnings("unchecked") final Seq<E>[] seqs = (Seq<E>[]) new Seq[len];
    for (int i = 0; i < len; i += 1) {
      seqs[i] = takeRightView(i + 1);
    }
    return new SeqSimple<>(seqs);
  }

  public E head() {
    return get(0);
  }

  public E last() {
    return get(length() - 1);
  }

  @Nonnull
  public Optional<E> headOptional() {
    if (isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(head());
  }

  @Nonnull
  public Optional<E> lastOptional() {
    if (isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(last());
  }

  @Override
  public boolean equals(@Nullable final Object that) {
    if (this == that) {
      return true;
    }
    if (!(that instanceof Seq)) {
      return false;
    }
    final int len = length();
    @SuppressWarnings("unchecked") final Seq<E> thatSeq = (Seq<E>) that;
    if (len != thatSeq.length()) {
      return false;
    }
    for (int i = 0; i < len; i += 1) {
      final E thisElement = get(i);
      final E thatElement = thatSeq.get(i);
      if (!(thisElement == thatElement || thisElement != null && thisElement.equals(thatElement))) {
        return false;
      }
    }
    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(backingArray);
  }

  @Override
  public String toString() {
    return stream().map(Objects::toString).collect(Collectors.joining(", ", "[", "]"));
  }

  static <E> void swap(final E[] array, final int i, final int j) {
    final E src = array[i];
    array[i] = array[j];
    array[j] = src;
  }

  static <E> void reverse(@Nonnull final E[] array) {
    final int len = array.length;
    final int halfLen = len / 2;
    for (int i = 0, j = len - 1; i < halfLen; i += 1, j -= 1) {
      swap(array, i, j);
    }
  }

  @Nonnull
  @Override
  public Iterator<E> iterator() {
    return new Iterator<E>() {

      private int current = 0;

      @Override
      public boolean hasNext() {
        return current < length();
      }

      @Override
      public E next() {
        return get(current++);
      }
    };
  }

  @Nonnull
  public Iterator<E> reverseIterator() {
    return reversed().iterator();
  }

  @Nonnull
  public List<E> toList() {
    return new AbstractList<E>() {
      @Override
      public E get(final int index) {
        return Seq.this.get(index);
      }

      @Override
      public int size() {
        return length();
      }
    };
  }

  @Nonnull
  public Stream<E> stream() {
    return toList().stream();
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  public static <E> Seq<E> empty() {
    return (Seq<E>) SeqEmpty.EMPTY;
  }

  @SafeVarargs
  @Nonnull
  public static <E> Seq<E> of(@Nonnull final E... es) {
    Objects.requireNonNull(es);
    if (es.length == 0) {
      return empty();
    }
    return new SeqSimple<>(es);
  }

  @Nonnull
  public static <E> Seq<E> ofArray(@Nonnull final E[] array) {
    Objects.requireNonNull(array);
    if (array.length == 0) {
      return empty();
    }
    return new SeqSimple<>(array.clone());
  }

  @Nonnull
  public static <E> Seq<E> ofCollection(@Nonnull final Collection<? extends E> collection) {
    Objects.requireNonNull(collection);
    if (collection.isEmpty()) {
      return empty();
    }
    final Object[] array = new Object[collection.size()];
    int i = 0;
    for (final E e : collection) {
      array[i++] = e;
    }
    return new SeqSimple<>(array);
  }

  @Nonnull
  public static <E> Seq<E> ofIterable(@Nonnull final Iterable<? extends E> iterable) {
    Objects.requireNonNull(iterable);
    final SeqBuilder<E> builder = new SeqBuilder<>();
    iterable.forEach(builder::add);
    return builder.result();
  }

  @Nonnull
  public static <E> Seq<E> ofIterator(@Nonnull final Iterator<? extends E> iterator) {
    Objects.requireNonNull(iterator);
    final SeqBuilder<E> builder = new SeqBuilder<>();
    while (iterator.hasNext()) {
      builder.add(iterator.next());
    }
    return builder.result();
  }

  @Nonnull
  public static Seq<Character> ofString(@Nonnull final String string) {
    Objects.requireNonNull(string);
    final Character[] array = new Character[string.length()];
    for (int i = 0; i < array.length; i += 1) {
      array[i] = string.charAt(i);
    }
    return new SeqSimple<>(array);
  }

  @Nonnull
  public static Seq<Integer> codepointsOfString(@Nonnull final String string) {
    final char[] array = string.toCharArray();
    final SeqBuilder<Integer> b = builder();
    for (int i = 0; i < array.length; i += 1) {
      final char c1 = array[i];
      if (Character.isHighSurrogate(c1)) {
        i += 1;
        if (i < array.length) {
          final char c2 = array[i];
          if (Character.isLowSurrogate(c2)) {
            final int codepoint = Character.toCodePoint(c1, c2);
            b.add(codepoint);
          }
        }
      } else {
        b.add((int) c1);
      }
    }
    return b.result();
  }

  @SafeVarargs
  @Nonnull
  public static <E> Seq<E> concat(@Nonnull final Seq<E>... seqs) {
    Objects.requireNonNull(seqs);
    int size = 0;
    for (final Seq<E> seq : seqs) {
      size += seq.length();
    }
    final Object[] array = new Object[size];
    int i = 0;
    for (final Seq<E> seq : seqs) {
      for (final E elem : seq) {
        array[i++] = elem;
      }
    }
    return new SeqSimple<>(array);
  }

  @Nonnull
  public static <E> SeqBuilder<E> builder() {
    return new SeqBuilder<>();
  }

  @Nonnull
  public static <E> SeqBuilder<E> builder(final int sizeHint) {
    return new SeqBuilder<>(sizeHint);
  }

  public static <C, A extends C, B extends C> Seq<C> fromPair(final Pair<A, B> p) {
    return Seq.<C>builder().addAll(p.fst, p.snd).result();
  }

}
