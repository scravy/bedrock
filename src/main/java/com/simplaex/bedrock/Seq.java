package com.simplaex.bedrock;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.simplaex.bedrock.Control.swap;

/**
 * An immutable sequence.
 *
 * @param <E> The type of the Elements contained in this Sequence.
 */
@Immutable
@SuppressWarnings({"unused"})
public abstract class Seq<E> implements
  Serializable,
  RandomAccess,
  SequenceMethods<Predicate<? super E>, BiPredicate<? super E, ? super E>, Seq<E>>,
  Container<E>,
  IntFunction<E> {

  private int hashCode = 0;

  public abstract E get(@Nonnegative final int index);

  @Override
  public E apply(@Nonnegative final int index) {
    return get(index);
  }

  @Override
  public boolean isEmpty() {
    return length() == 0;
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

  @Override
  public E draw(@Nonnull final Random random) throws NoSuchElementException {
    Objects.requireNonNull(random, "the supplied 'random' generator must not be null");
    if (isEmpty()) {
      throw new NoSuchElementException("drawing from an empty set");
    }
    final int ix = random.nextInt(size());
    return get(ix);
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
  public String asString(@Nonnull final String delimiter) {
    return stream().map(Objects::toString).collect(Collectors.joining(delimiter));
  }

  @Nonnegative
  public int count(@Nullable final E e) {
    final int len = length();
    int c = 0;
    for (int i = 0; i < len; i += 1) {
      final E el = get(i);
      if (Objects.equals(el, e)) {
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
      if (Objects.equals(el, e)) {
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

  @Override
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
    final SeqBuilder<F> resultBuilder = Seq.builder();
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
    final SeqBuilder<F> resultBuilder = Seq.builder();
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
    Objects.requireNonNull(sequence, "'sequence' must not be null");
    final int len = Math.min(length(), sequence.length());
    final Object[] arr = new Object[len];
    for (int i = 0; i < len; i += 1) {
      arr[i] = function.apply(get(i), sequence.get(i));
    }
    return new SeqSimple<>(arr);
  }

  @Nonnull
  public Seq<Pair<Integer, E>> zipWithIndex() {
    return Seq.rangeExclusive(0, length()).zip(this);
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

  public <A> A foldl1f(
    @Nonnull final BiFunction<? super A, ? super E, ? extends A> function,
    @Nonnull final Function<? super E, ? extends A> startValueFunction
  ) {
    return tail().foldl(function, startValueFunction.apply(head()));
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

  public <A> A foldr1f(
    @Nonnull final BiFunction<? super E, ? super A, ? extends A> function,
    @Nonnull final Function<? super E, ? extends A> startValueFunction
  ) {
    Objects.requireNonNull(startValueFunction, "'startValueFunction' must not be null");
    return initView().foldr(function, startValueFunction.apply(last()));
  }

  @Override
  @Nonnull
  public Pair<Seq<E>, Seq<E>> partitionBy(@Nonnull final Predicate<? super E> predicate) {
    Objects.requireNonNull(predicate, "'predicate' must not be null");
    final int sizeHint = length() / 2 + 1;
    final SeqBuilder<E> b1 = Seq.builder(sizeHint);
    final SeqBuilder<E> b2 = Seq.builder(sizeHint);
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
    final SeqBuilder<Seq<E>> b1 = Seq.builder();
    final SeqBuilder<E> b2 = Seq.builder();
    E previous = head();
    b2.add(previous);
    for (int i = 1; i < size(); i += 1) {
      final E current = get(i);
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

  @Nonnull
  @SuppressWarnings("unchecked")
  public <F extends E> Seq<F> filter(@Nonnull final Class<F> clazz) {
    final Seq res = filter(element -> element != null && clazz.isAssignableFrom(element.getClass()));
    return (Seq<F>) res;
  }

  @Override
  @Nonnull
  public Seq<E> filter(@Nonnull final Predicate<? super E> predicate) {
    Objects.requireNonNull(predicate, "'predicate' must not be null");
    final int sizeHint = length() / 2;
    final SeqBuilder<E> b = Seq.builder(sizeHint);
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
    final int targetSize = (seq.size() + 1) * size() - seq.size();
    final Object[] targetArray = new Object[targetSize];
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
    final int targetSize = size() * 2 - 1;
    final Object[] targetArray = new Object[targetSize];
    targetArray[0] = head();
    int targetIndex = 1;
    for (int i = 1; i < size(); i += 1) {
      targetArray[targetIndex++] = e;
      targetArray[targetIndex++] = get(i);
    }
    return new SeqSimple<>(targetArray);
  }

  /**
   * Returns a copy of this Seq with no duplicates.
   * <p>
   * Order is maintained.
   */
  @Nonnull
  @Override
  public Seq<E> distinct() {
    final HashSet<E> elements = new HashSet<>();
    final SeqBuilder<E> builder = Seq.builder(size());
    forEach(element -> {
      if (!elements.contains(element)) {
        builder.add(element);
        elements.add(element);
      }
    });
    return builder.result();
  }

  /**
   * Returns a Seq with the elements from the given Seq removed.
   * <p>
   * Order is maintained, but the resuling Seq is not distinct; if this Seq contains duplicates then the result
   * may contain duplicates too.
   */
  public Seq<E> without(final Seq<E> seq) {
    return filter(element -> !seq.contains(element));
  }

  /**
   * Returns a distinct Seq that contains the elements from both this Seq and the given Seq.
   * <p>
   * Order is maintained, but there are no duplicates.
   */
  public Seq<E> union(final Seq<E> seq) {
    final HashSet<E> elements = new HashSet<>();
    final SeqBuilder<E> builder = Seq.builder(size());
    final Consumer<E> appender = element -> {
      if (!elements.contains(element)) {
        builder.add(element);
        elements.add(element);
      }
    };
    forEach(appender);
    seq.forEach(appender);
    return builder.result();
  }

  /**
   * Returns a distinct Seq that contains only the elements that occur in both sets.
   * <p>
   * Order is maintained.
   */
  public Seq<E> intersect(final Seq<E> seq) {
    return union(seq).filter(e -> contains(e) && seq.contains(e));
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
    final Iterator<E> thisIterator = iterator();
    final Iterator<E> thatIterator = thatSeq.iterator();
    for (int i = 0; i < len; i += 1) {
      final E thisElement = thisIterator.next();
      final E thatElement = thatIterator.next();
      if (!Objects.equals(thisElement, thatElement)) {
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

  static <E> void reverse(final @Nonnull E[] array) {
    final int len = array.length;
    final int halfLen = len / 2;
    for (int i = 0, j = len - 1; i < halfLen; i += 1, j -= 1) {
      swap(array, i, j);
    }
  }

  /**
   * Rotates the list by amount positions.
   * <p>
   * Positive values rotate items to the right, negative values to the left.
   *
   * <code>Seq.of(1, 2, 3).rotated(1).equals(Seq.of(3, 1, 2))</code>
   * <code>Seq.of(1, 2, 3).rotated(-1).equals(Seq.of(2, 3, 1))</code>
   *
   * @param amount The amount of positions to rotate, positive values to the right, negative values to the left.
   * @return The rotated sequence.
   */
  @Nonnull
  @Override
  public Seq<E> rotated(final int amount) {
    final Object[] array = new Object[size()];
    if (amount == 0) {
      return this;
    }
    if (amount < 0) {
      return leftRotated(-amount);
    }
    for (int i = 0; i < size(); i += 1) {
      array[(i + amount) % size()] = get(i);
    }
    return ofArrayZeroCopyInternal(array);
  }

  @Nonnull
  private Seq<E> leftRotated(final int amount) {
    final Object[] array = new Object[size()];
    for (int i = 0; i < size(); i += 1) {
      array[i] = get((i + amount) % size());
    }
    return ofArrayZeroCopyInternal(array);
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

  /**
   * Create a <code>java.util.List</code> that contains the elements of this sequence.
   * <p>
   * This action does not copy any data.
   *
   * @return An unmodifiable list that is backed by this Sequence.
   */
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
      public java.util.Set<Characteristics> characteristics() {
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

  @SafeVarargs
  @Nonnull
  public static <E> Seq<E> seq(@Nonnull final E... es) {
    return ofArray(es);
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

  @FunctionalInterface
  @Deprecated
  public interface WithIndexConsumer<E> {
    void consume(int index, E element);
  }

  @Deprecated
  public void forEach(@Nonnull final WithIndexConsumer<E> consumer) {
    Objects.requireNonNull(consumer, "'consumer' must not be null");
    for (int i = 0; i < size(); i += 1) {
      consumer.consume(i, get(i));
    }
  }

  @Nonnull
  public static Seq<Integer> codepointsOfString(@Nonnull final String string) {
    final SeqBuilder<Integer> b = builder();
    Strings.forEachCodepoint(string, b::add);
    return b.result();
  }

  @SafeVarargs
  @Nonnull
  public static <E> Seq<E> concatView(@Nonnull final Seq<E>... seqs) {
    Objects.requireNonNull(seqs);

    if (seqs.length == 0) {
      return Seq.empty();
    }
    int size = 0;
    for (final Seq<E> seq : seqs) {
      size += seq.length();
    }
    if (size == 0) {
      return Seq.empty();
    }
    return new SeqGenerated<E>(
      ix -> {
        Seq<E> seq = seqs[0];
        int i = 1;
        while (ix >= seq.length() && i < seqs.length) {
          ix -= seq.length();
          seq = seqs[i];
          i += 1;
        }
        return seq.get(ix);
      },
      size
    ) {
      @Nonnull
      @Override
      public Iterator<E> iterator() {
        return new Iterator<E>() {

          private int i = 0;
          private int j = 0;

          @Override
          public boolean hasNext() {
            return i < seqs.length && j < seqs[i].length();
          }

          @Override
          public E next() {
            final E elem = seqs[i].get(j);
            j += 1;
            if (j == seqs[i].length()) {
              j = 0;
              i += 1;
            }
            return elem;
          }
        };
      }
    };
  }

  @SafeVarargs
  @Nonnull
  public static <E> Seq<E> concat(@Nonnull final Seq<E>... seqs) {
    Objects.requireNonNull(seqs);
    int size = 0;
    for (final Seq<E> seq : seqs) {
      size += seq.length();
    }
    if (size == 0) {
      return Seq.empty();
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

  @Nonnull
  public static <E> Seq<E> ofGenerator(@Nonnull final IntFunction<E> function, @Nonnegative final int length) {
    Objects.requireNonNull(function, "'function' must not be null.");
    return new SeqGenerated<>(function, length);
  }

  @Nonnull
  public static <E> Seq<E> ofGeneratorMemoizing(@Nonnull final IntFunction<E> function, @Nonnegative final int length) {
    Objects.requireNonNull(function, "'function' must not be null.");
    return new SeqGenerated<>(Control.memoizing(function), length);
  }

  @Nonnull
  public static Seq<Character> wrap(@Nonnull final char[] array) {
    if (array.length == 0) {
      return Seq.empty();
    }
    return ofGenerator(ix -> array[ix], array.length);
  }

  @Nonnull
  public static Seq<Boolean> wrap(@Nonnull final boolean[] array) {
    if (array.length == 0) {
      return Seq.empty();
    }
    return ofGenerator(ix -> array[ix], array.length);
  }

  @Nonnull
  public static Seq<Byte> wrap(@Nonnull final byte[] array) {
    if (array.length == 0) {
      return Seq.empty();
    }
    return ofGenerator(ix -> array[ix], array.length);
  }

  @Nonnull
  public static Seq<Short> wrap(@Nonnull final short[] array) {
    if (array.length == 0) {
      return Seq.empty();
    }
    return ofGenerator(ix -> array[ix], array.length);
  }

  @Nonnull
  public static Seq<Integer> wrap(@Nonnull final int[] array) {
    if (array.length == 0) {
      return Seq.empty();
    }
    return ofGenerator(ix -> array[ix], array.length);
  }

  @Nonnull
  public static Seq<Long> wrap(@Nonnull final long[] array) {
    if (array.length == 0) {
      return Seq.empty();
    }
    return ofGenerator(ix -> array[ix], array.length);
  }

  @Nonnull
  public static Seq<Float> wrap(@Nonnull final float[] array) {
    if (array.length == 0) {
      return Seq.empty();
    }
    return ofGenerator(ix -> array[ix], array.length);
  }

  @Nonnull
  public static Seq<Double> wrap(@Nonnull final double[] array) {
    if (array.length == 0) {
      return Seq.empty();
    }
    return ofGenerator(ix -> array[ix], array.length);
  }

  @Nonnull
  public static Seq<Character> wrap(@Nonnull final Character[] array) {
    if (array.length == 0) {
      return Seq.empty();
    }
    return ofGenerator(ix -> array[ix], array.length);
  }

  @Nonnull
  public static Seq<Boolean> wrap(@Nonnull final Boolean[] array) {
    if (array.length == 0) {
      return Seq.empty();
    }
    return ofGenerator(ix -> array[ix], array.length);
  }

  @Nonnull
  public static Seq<Byte> wrap(@Nonnull final Byte[] array) {
    if (array.length == 0) {
      return Seq.empty();
    }
    return ofGenerator(ix -> array[ix], array.length);
  }

  @Nonnull
  public static Seq<Short> wrap(@Nonnull final Short[] array) {
    if (array.length == 0) {
      return Seq.empty();
    }
    return ofGenerator(ix -> array[ix], array.length);
  }

  @Nonnull
  public static Seq<Integer> wrap(@Nonnull final Integer[] array) {
    if (array.length == 0) {
      return Seq.empty();
    }
    return ofGenerator(ix -> array[ix], array.length);
  }

  @Nonnull
  public static Seq<Long> wrap(@Nonnull final Long[] array) {
    if (array.length == 0) {
      return Seq.empty();
    }
    return ofGenerator(ix -> array[ix], array.length);
  }

  @Nonnull
  public static Seq<Float> wrap(@Nonnull final Float[] array) {
    if (array.length == 0) {
      return Seq.empty();
    }
    return ofGenerator(ix -> array[ix], array.length);
  }

  @Nonnull
  public static Seq<Double> wrap(@Nonnull final Double[] array) {
    if (array.length == 0) {
      return Seq.empty();
    }
    return ofGenerator(ix -> array[ix], array.length);
  }

  @Nonnull
  public static <E> Seq<E> wrap(@Nonnull final E[] array) {
    if (array.length == 0) {
      return Seq.empty();
    }
    return ofGenerator(ix -> array[ix], array.length);
  }

  @Nonnull
  public static <E> Seq<E> wrap(@Nonnull final List<E> list) {
    if (list.isEmpty()) {
      return Seq.empty();
    }
    return ofGenerator(list::get, list.size());
  }

  @Nonnull
  public static Seq<Boolean> wrap(@Nonnull final BitSet bitSet) {
    if (bitSet.isEmpty()) {
      return Seq.empty();
    }
    return ofGenerator(bitSet::get, bitSet.length());
  }

  @Nonnull
  public static Seq<Character> wrap(@Nonnull final String string) {
    if (string.isEmpty()) {
      return Seq.empty();
    }
    return ofGenerator(string::charAt, string.length());
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

  public static boolean any(@Nonnull final Seq<Boolean> seq) {
    Objects.requireNonNull(seq, "the supplied sequence must not be null");
    for (final Boolean e : seq) {
      if (Boolean.TRUE.equals(e)) {
        return true;
      }
    }
    return false;
  }

  public static boolean all(@Nonnull final Seq<Boolean> seq) {
    Objects.requireNonNull(seq, "the supplied sequence must not be null");
    for (final Boolean e : seq) {
      if (!Boolean.TRUE.equals(e)) {
        return false;
      }
    }
    return true;
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
    return all(seq);
  }

  public static boolean or(final Seq<Boolean> seq) {
    return any(seq);
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

  public static <A, B, C, D> Seq<Quadruple<A, B, C, D>> zip(
    final Seq<A> as,
    final Seq<B> bs,
    final Seq<C> cs,
    final Seq<D> ds
  ) {
    final int length = Arithmetic.minimum(as.length(), bs.length(), cs.length(), ds.length());
    final SeqBuilder<Quadruple<A, B, C, D>> builder = Seq.builder(length);
    for (int i = 0; i < length; i += 1) {
      builder.add(Quadruple.of(as.get(i), bs.get(i), cs.get(i), ds.get(i)));
    }
    return builder.result();
  }

  public static <A, B, C> Seq<Triple<A, B, C>> zip(
    final Seq<A> as,
    final Seq<B> bs,
    final Seq<C> cs
  ) {
    final int length = Arithmetic.minimum(as.length(), bs.length(), cs.length());
    final SeqBuilder<Triple<A, B, C>> builder = Seq.builder(length);
    for (int i = 0; i < length; i += 1) {
      builder.add(Triple.of(as.get(i), bs.get(i), cs.get(i)));
    }
    return builder.result();
  }

  public static <A, B, C> Seq<Pair<A, B>> zip(
    final Seq<A> as,
    final Seq<B> bs
  ) {
    final int length = Arithmetic.minimum(as.length(), bs.length());
    final SeqBuilder<Pair<A, B>> builder = Seq.builder(length);
    for (int i = 0; i < length; i += 1) {
      builder.add(Pair.of(as.get(i), bs.get(i)));
    }
    return builder.result();
  }
}
