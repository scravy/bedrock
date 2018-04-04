package com.simplaex.bedrock;

import lombok.val;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An immutable sequence.
 *
 * @param <E> The type of the Elements contained in this Sequence.
 */
@Immutable
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class Seq<E> implements
  Serializable,
  RandomAccess,
  Iterable<E>,
  SequenceMethods<Predicate<? super E>, BiPredicate<? super E, ? super E>, Seq<E>>,
  IntFunction<E> {

  private int hashCode = 0;

  public abstract E get(@Nonnegative final int index);

  @Override
  public E apply(@Nonnegative final int index) {
    return get(index);
  }

  @Override
  @Nonnull
  public Seq<E> shuffled(@Nonnull final Random random) {
    Objects.requireNonNull(random, "the supplied 'random' generator must not be null");
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
  public <F extends Comparable<? super F>> Seq<E> sortedOn(@Nonnull final Function<? super E, ? extends F> function) {
    Objects.requireNonNull(function, "'function' must not be null");
    return sortedBy(Comparator.comparing(function));
  }

  @Nonnull
  public abstract E[] toArray(@Nonnull final Class<E> clazz);

  @Nonnull
  public abstract Object[] toArray();

  @Override
  @Nonnull
  public String asString(final String delimiter) {
    return stream().map(Objects::toString).collect(Collectors.joining(delimiter));
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
    Objects.requireNonNull(predicate, "'predicate' must not be null");
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

  public int findBy(@Nonnull final Predicate<? super E> predicate) {
    Objects.requireNonNull(predicate, "'predicate' must not be null");
    final int len = length();
    for (int i = 0; i < len; i += 1) {
      final E element = get(i);
      if (predicate.test(element)) {
        return i;
      }
    }
    return -1;
  }

  public Optional<E> findFirst(@Nonnull final Predicate<? super E> predicate) {
    Objects.requireNonNull(predicate, "'predicate' must not be null");
    final int len = length();
    for (int i = 0; i < len; i += 1) {
      final E element = get(i);
      if (predicate.test(element)) {
        return Optional.ofNullable(element);
      }
    }
    return Optional.empty();
  }

  public boolean contains(@Nullable final E e) {
    return find(e) > -1;
  }

  @Override
  public boolean exists(@Nonnull final Predicate<? super E> predicate) {
    return findBy(predicate) > -1;
  }

  @Override
  public boolean forAll(@Nonnull final Predicate<? super E> predicate) {
    return countBy(predicate) == length();
  }

  void checkBounds(final int index) {
    if (index >= length() || index < 0) {
      throw new IndexOutOfBoundsException();
    }
  }

  @Nonnull
  public <F> Seq<F> map(@Nonnull final Function<? super E, ? extends F> function) {
    Objects.requireNonNull(function, "'function' must not be null");
    final Object[] array = new Object[length()];
    int i = 0;
    for (final E e : this) {
      array[i++] = function.apply(e);
    }
    return new SeqSimple<>(array);
  }

  @Nonnull
  public <F> Seq<F> flatMap(@Nonnull final Function<? super E, Seq<F>> function) {
    Objects.requireNonNull(function, "'function' must not be null");
    @SuppressWarnings("unchecked") final Seq<F>[] array = (Seq<F>[]) new Seq[length()];
    int i = 0;
    int c = 0;
    for (final E e : this) {
      final Seq<F> result = function.apply(e);
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
  public <F> Seq<F> flatMapOptional(@Nonnull final Function<? super E, Optional<F>> function) {
    Objects.requireNonNull(function, "'function' must not be null");
    @SuppressWarnings("unchecked") final Seq<F>[] array = (Seq<F>[]) new Seq[length()];
    val resultBuilder = Seq.<F>builder();
    for (final E e : this) {
      final Optional<F> result = function.apply(e);
      result.ifPresent(resultBuilder::add);
    }
    return resultBuilder.build();
  }

  @Nonnull
  public <F> Seq<F> flatMapIterable(@Nonnull final Function<? super E, ? extends Iterable<F>> function) {
    Objects.requireNonNull(function, "'function' must not be null");
    @SuppressWarnings("unchecked") final Seq<F>[] array = (Seq<F>[]) new Seq[length()];
    val resultBuilder = Seq.<F>builder();
    for (final E e : this) {
      resultBuilder.addElements(function.apply(e));
    }
    return resultBuilder.build();
  }

  @Nonnull
  public <A> Seq<Pair<E, A>> zip(@Nonnull final Seq<A> a) {
    return zipWith(Pair::new, a);
  }

  @Nonnull
  public <A, C> Seq<C> zipWith(
    final @Nonnull BiFunction<? super E, ? super A, ? extends C> function,
    final @Nonnull Seq<A> sequence
  ) {
    Objects.requireNonNull(function, "'function' must not be null");
    Objects.requireNonNull(sequence);
    final int len = Math.min(length(), sequence.length());
    final Object[] arr = new Object[len];
    for (int i = 0; i < len; i += 1) {
      arr[i] = function.apply(get(i), sequence.get(i));
    }
    return new SeqSimple<>(arr);
  }

  public <A> A foldl(@Nonnull final BiFunction<? super A, ? super E, ? extends A> function, final A startValue) {
    Objects.requireNonNull(function, "'function' must not be null");
    A acc = startValue;
    for (int i = 0; i < length(); i += 1) {
      acc = function.apply(acc, get(i));
    }
    return acc;
  }

  public E foldl1(@Nonnull final BiFunction<? super E, ? super E, ? extends E> function) {
    return foldl(function, head());
  }

  public <A> A foldr(@Nonnull final BiFunction<? super E, ? super A, ? extends A> function, final A startValue) {
    Objects.requireNonNull(function, "'function' must not be null");
    A acc = startValue;
    for (int i = length() - 1; i >= 0; i -= 1) {
      acc = function.apply(get(i), acc);
    }
    return acc;
  }

  public E foldr1(@Nonnull final BiFunction<? super E, ? super E, ? extends E> function) {
    return foldr(function, last());
  }

  @Override
  @Nonnull
  public Pair<Seq<E>, Seq<E>> partitionBy(@Nonnull final Predicate<? super E> predicate) {
    Objects.requireNonNull(predicate, "'predicate' must not be null");
    final int sizeHint = length() / 2 + 1;
    val b1 = Seq.<E>builder(sizeHint);
    val b2 = Seq.<E>builder(sizeHint);
    forEach(x -> {
      if (predicate.test(x)) {
        b1.add(x);
      } else {
        b2.add(x);
      }
    });
    return Pair.of(b1.result(), b2.result());
  }

  @SuppressWarnings("unchecked")
  public E maximum() {
    try {
      return (E) maximum((Seq) this);
    } catch (final ClassCastException exc) {
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  public E minimum() {
    try {
      return (E) minimum((Seq) this);
    } catch (final ClassCastException exc) {
      return null;
    }
  }

  public E maximumBy(final Comparator<? super E> comparator) {
    return foldl1((left, right) -> comparator.compare(left, right) < 0 ? right : left);
  }

  public E minimumBy(final Comparator<? super E> comparator) {
    return foldl1((left, right) -> comparator.compare(left, right) > 0 ? right : left);
  }

  @Override
  @Nonnull
  public Seq<Seq<E>> group() {
    return groupBy(Objects::equals);
  }

  @Override
  @Nonnull
  public Seq<Seq<E>> groupBy(@Nonnull final BiPredicate<? super E, ? super E> operator) {
    Objects.requireNonNull(operator, "'operator' must not be null");
    if (isEmpty()) {
      return Seq.empty();
    }
    val b1 = Seq.<Seq<E>>builder();
    val b2 = Seq.<E>builder();
    E previous = head();
    b2.add(previous);
    for (int i = 1; i < size(); i += 1) {
      val current = get(i);
      if (!operator.test(previous, current)) {
        b1.add(b2.result());
        b2.clear();
      }
      b2.add(current);
      previous = current;
    }
    if (!b2.isEmpty()) {
      b1.add(b2.result());
    }
    return b1.result();
  }

  @Override
  @Nonnull
  public Seq<E> filter(@Nonnull final Predicate<? super E> predicate) {
    Objects.requireNonNull(predicate, "'predicate' must not be null");
    final int sizeHint = length() / 2;
    val b = Seq.<E>builder(sizeHint);
    forEach(x -> {
      if (predicate.test(x)) {
        b.add(x);
      }
    });
    return b.result();
  }

  @Override
  @Nonnull
  public Seq<E> filterNot(@Nonnull final Predicate<? super E> predicate) {
    Objects.requireNonNull(predicate, "'predicate' must not be null");
    return filter(predicate.negate());
  }

  @Override
  @Nonnull
  public Seq<E> takeWhile(@Nonnull final Predicate<? super E> predicate) {
    Objects.requireNonNull(predicate, "'predicate' must not be null");
    int i = 0;
    while (i < length() && predicate.test(get(i))) {
      i += 1;
    }
    return take(i);
  }

  @Override
  @Nonnull
  public Seq<E> takeWhileView(@Nonnull final Predicate<? super E> predicate) {
    Objects.requireNonNull(predicate, "'predicate' must not be null");
    int i = 0;
    while (i < length() && predicate.test(get(i))) {
      i += 1;
    }
    return takeView(i);
  }

  @Override
  @Nonnull
  public Seq<E> dropWhile(@Nonnull final Predicate<? super E> predicate) {
    Objects.requireNonNull(predicate, "'predicate' must not be null");
    int i = 0;
    while (i < length() && predicate.test(get(i))) {
      i += 1;
    }
    return drop(i);
  }

  @Override
  @Nonnull
  public Seq<E> dropWhileView(@Nonnull final Predicate<? super E> predicate) {
    Objects.requireNonNull(predicate, "'predicate' must not be null");
    int i = 0;
    while (i < length() && predicate.test(get(i))) {
      i += 1;
    }
    return dropView(i);
  }

  @Override
  @Nonnull
  public Pair<Seq<E>, Seq<E>> breakBy(@Nonnull final Predicate<? super E> predicate) {
    Objects.requireNonNull(predicate, "'predicate' must not be null");
    final int ix = findBy(predicate);
    if (ix < 0) {
      return Pair.of(this, Seq.empty());
    } else if (ix == 0) {
      return Pair.of(Seq.empty(), this);
    } else {
      return Pair.of(subSequence(0, ix), subSequence(ix, length()));
    }
  }

  @Override
  @Nonnull
  public Pair<Seq<E>, Seq<E>> breakByView(@Nonnull final Predicate<? super E> predicate) {
    Objects.requireNonNull(predicate, "'predicate' must not be null");
    final int ix = findBy(predicate);
    if (ix < 0) {
      return Pair.of(this, Seq.empty());
    } else if (ix == 0) {
      return Pair.of(Seq.empty(), this);
    } else {
      return Pair.of(subSequenceView(0, ix), subSequenceView(ix, length()));
    }
  }

  @Override
  @Nonnull
  public Pair<Seq<E>, Seq<E>> spanBy(@Nonnull final Predicate<? super E> predicate) {
    Objects.requireNonNull(predicate, "'predicate' must not be null");
    return breakBy(predicate.negate());
  }

  @Override
  @Nonnull
  public Pair<Seq<E>, Seq<E>> spanByView(@Nonnull final Predicate<? super E> predicate) {
    Objects.requireNonNull(predicate, "'predicate' must not be null");
    return breakByView(predicate.negate());
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

  @Nonnull
  public Seq<E> intercalate(@Nonnull final Seq<E> seq) {
    if (isEmpty()) {
      return this;
    }
    val targetSize = (seq.size() + 1) * size() - seq.size();
    val targetArray = new Object[targetSize];
    targetArray[0] = head();
    int targetIndex = 1;
    for (int i = 1; i < size(); i += 1) {
      for (int j = 0; j < seq.size(); j += 1) {
        targetArray[targetIndex++] = seq.get(j);
      }
      targetArray[targetIndex++] = get(i);
    }
    return new SeqSimple<>(targetArray);
  }

  @Nonnull
  public Seq<E> intersperse(final E e) {
    if (isEmpty()) {
      return this;
    }
    val targetSize = size() * 2 - 1;
    val targetArray = new Object[targetSize];
    targetArray[0] = head();
    int targetIndex = 1;
    for (int i = 1; i < size(); i += 1) {
      targetArray[targetIndex++] = e;
      targetArray[targetIndex++] = get(i);
    }
    return new SeqSimple<>(targetArray);
  }

  @Nonnull
  @Override
  public Seq<E> distinct() {
    val elements = new HashSet<E>();
    val builder = Seq.<E>builder(size());
    forEach(element -> {
      if (!elements.contains(element)) {
        builder.add(element);
        elements.add(element);
      }
    });
    return builder.result();
  }

  @Override
  public boolean equals(final @Nullable Object that) {
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
  public final int hashCode() {
    if (hashCode == 0) {
      hashCode = calculateHashCode();
    }
    return hashCode;
  }

  int calculateHashCode() {
    int result = 1;
    for (final E element : this) {
      result = 31 * result + (element == null ? 0 : element.hashCode());
    }
    return result;
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

  static <E> void reverse(final @Nonnull E[] array) {
    final int len = array.length;
    final int halfLen = len / 2;
    for (int i = 0, j = len - 1; i < halfLen; i += 1, j -= 1) {
      swap(array, i, j);
    }
  }

  @Nonnull
  @Override
  public Iterator<Seq<E>> permutationsIterator() {
    return new PermutationIterator<>(this);
  }

  @Nonnull
  @Override
  public Seq<Seq<E>> permutations() {
    return permutationsStream().collect(collector());
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

  public static <T> Collector<T, SeqBuilder<T>, Seq<T>> collector() {
    return new Collector<T, SeqBuilder<T>, Seq<T>>() {

      @Override
      public Supplier<SeqBuilder<T>> supplier() {
        return Seq::builder;
      }

      @Override
      public BiConsumer<SeqBuilder<T>, T> accumulator() {
        return SeqBuilder::add;
      }

      @Override
      public BinaryOperator<SeqBuilder<T>> combiner() {
        return SeqBuilder::addElements;
      }

      @Override
      public Function<SeqBuilder<T>, Seq<T>> finisher() {
        return SeqBuilder::result;
      }

      @Override
      public Set<Characteristics> characteristics() {
        return Collections.emptySet();
      }
    };
  }

  @Nonnull
  public <K> Mapping<K, Seq<E>> toMap(@Nonnull final Function<? super E, ? extends K> groupingFunction) {
    Objects.requireNonNull(groupingFunction, "'groupingFunction' must not be null");
    final Map<K, SeqBuilder<E>> map = new HashMap<>();
    boolean allComparable = true;
    for (final E element : this) {
      final K key = groupingFunction.apply(element);
      allComparable = allComparable && key instanceof Comparable;
      if (!map.containsKey(key)) {
        map.put(key, Seq.builder());
      }
      map.get(key).add(element);
    }
    if (map.isEmpty()) {
      return Mapping.empty();
    }
    final Map<K, Seq<E>> finalMap = new HashMap<>();
    map.forEach((key, builder) -> finalMap.put(key, builder.result()));
    return Mapping.wrap(finalMap);
  }

  @Nonnull
  public <K extends Comparable<K>> ArrayMap<K, Seq<E>> toArrayMap(@Nonnull final Function<? super E, ? extends K> groupingFunction) {
    Objects.requireNonNull(groupingFunction, "'groupingFunction' must not be null");
    final TreeMap<K, SeqBuilder<E>> map = new TreeMap<>();
    for (final E element : this) {
      final K key = groupingFunction.apply(element);
      if (!map.containsKey(key)) {
        map.put(key, Seq.builder());
      }
      map.get(key).add(element);
    }
    if (map.isEmpty()) {
      return ArrayMap.empty();
    }
    final Object[] keys = Seq.ofCollectionInternal(map.keySet()).backingArray;
    final Object[] values = new Object[keys.length];
    for (int i = 0; i < keys.length; i += 1) {
      //noinspection SuspiciousMethodCalls
      values[i] = map.get(keys[i]).result();
    }
    return new ArrayMap<>(keys, values);
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
  static <E> SeqSimple<E> ofArrayZeroCopyInternal(@Nonnull final Object[] array) {
    return new SeqSimple<>(array);
  }

  @Nonnull
  public static <E> Seq<E> ofCollection(@Nonnull final Collection<? extends E> collection) {
    Objects.requireNonNull(collection);
    if (collection.isEmpty()) {
      return empty();
    }
    return ofCollectionInternal(collection);
  }

  @Nonnull
  static <E> SeqSimple<E> ofCollectionInternal(@Nonnull final Collection<? extends E> collection) {
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
  public static <C, A extends C, B extends C> Seq<C> ofPair(@Nonnull final Pair<A, B> pair) {
    Objects.requireNonNull(pair, "'pair' must not be null");
    return Seq.<C>builder().addAll(pair.fst(), pair.snd()).result();
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

  public interface WithIndexConsumer<E> {
    void consume(int index, E element);
  }

  public void forEach(final WithIndexConsumer<E> consumer) {
    for (int i = 0; i < size(); i += 1) {
      consumer.consume(i, get(i));
    }
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

  @Nonnegative
  public static <E> Seq<E> ofGenerator(@Nonnull final IntFunction<E> function, @Nonnegative final int length) {
    Objects.requireNonNull(function, "'function' must not be null.");
    return new SeqGenerated<>(function, length);
  }

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

  public static Seq<Integer> rangeInclusive(final int from, final int to) {
    if (from <= to) {
      return ofGenerator(index -> from + index, to - from + 1);
    }
    final int length = from - to;
    return ofGenerator(index -> to + (length - index), length + 1);
  }

  public static Seq<Integer> rangeExclusive(final int from, final int to) {
    if (from == to) {
      return empty();
    }
    if (from < to) {
      return ofGenerator(index -> from + index, to - from);
    }
    final int length = from - to;
    return ofGenerator(index -> to + (length - index), length);
  }
}
