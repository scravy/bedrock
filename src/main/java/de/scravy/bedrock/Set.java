package de.scravy.bedrock;

import lombok.EqualsAndHashCode;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

@EqualsAndHashCode
@Immutable
public final class Set<E extends Comparable<? super E>> implements
  Serializable,
  Container<E> {

  private final SeqSimpleSorted<E> underlying;

  private Set(final SeqSimpleSorted<E> underlying) {
    this.underlying = underlying;
  }

  public boolean contains(final E element) {
    return underlying != null && underlying.contains(element);
  }

  public boolean containsAll(final Iterable<E> iterable) {
    Objects.requireNonNull(iterable, "'iterable' must not be null");
    if (underlying == null) {
      return !iterable.iterator().hasNext();
    }
    for (final E e : iterable) {
      if (!underlying.contains(e)) {
        return false;
      }
    }
    return true;
  }

  public boolean containsAny(final Iterable<E> iterable) {
    Objects.requireNonNull(iterable, "'iterable' must not be null");
    if (underlying == null) {
      return false;
    }
    for (final E e : iterable) {
      if (underlying.contains(e)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean forAll(@Nonnull final Predicate<? super E> predicate) {
    return underlying == null || underlying.forAll(predicate);
  }

  @Override
  public boolean exists(@Nonnull final Predicate<? super E> predicate) {
    return underlying != null && underlying.exists(predicate);
  }

  @Override
  public E draw(@Nonnull final Random random) throws NoSuchElementException {
    if (underlying == null) {
      throw new NoSuchElementException("drawing from an empty set");
    }
    final int ix = random.nextInt(size());
    return underlying.get(ix);
  }

  @Nonnull
  @SuppressWarnings("unchecked")
  public <F extends E> Set<F> filter(@Nonnull final Class<F> clazz) {
    if (underlying == null) {
      return empty();
    }
    final Set set = filter(element -> element != null && clazz.isAssignableFrom(element.getClass()));
    return (Set<F>) set;
  }

  public Set<E> filter(@Nonnull final Predicate<E> predicate) {
    Objects.requireNonNull(predicate, "'predicate' must not be null");
    if (isEmpty()) {
      return empty();
    }
    return ofSeqInternal(underlying.filter(predicate));
  }

  public Set<E> filterNot(@Nonnull final Predicate<E> predicate) {
    Objects.requireNonNull(predicate, "'predicate' must not be null");
    return filter(predicate.negate());
  }

  public Set<E> union(final Set<E> other) {
    Objects.requireNonNull(other, "'other' must not be null");
    if (other.isEmpty()) {
      return this;
    }
    if (isEmpty()) {
      return other;
    }
    return ofSeqInternal(underlying.union(other.underlying));
  }

  public Set<E> intersect(final Set<E> other) {
    Objects.requireNonNull(other, "'other' must not be null");
    if (isEmpty() || other.isEmpty()) {
      return empty();
    }
    return ofSeqInternal(underlying.intersect(other.underlying));
  }

  public Set<E> without(final Set<E> other) {
    Objects.requireNonNull(other, "'other' must not be null");
    if (isEmpty() || other.isEmpty()) {
      return this;
    }
    return ofSeqInternal(underlying.without(other.underlying));
  }

  public <F extends Comparable<? super F>> Set<F> transform(final Function<E, F> f) {
    return stream().map(f).collect(builder());
  }

  public E maximum() {
    return toSeq().lastOptional().orElseThrow(NoSuchElementException::new);
  }

  public E minimum() {
    return toSeq().headOptional().orElseThrow(NoSuchElementException::new);
  }

  @Nonnull
  public Optional<E> maximumOptional() {
    return toSeq().lastOptional();
  }

  @Nonnull
  public Optional<E> minimumOptional() {
    return toSeq().headOptional();
  }

  @Override
  public boolean isEmpty() {
    return underlying == null;
  }

  @Nonnull
  @Override
  public String asString(@Nonnull final String delimiter) {
    return underlying == null ? "" : underlying.asString(delimiter);
  }

  public int size() {
    return underlying == null ? 0 : underlying.size();
  }

  /**
   * Cost: O(1) - The underlying structure is already a Seq
   */
  @Nonnull
  public Seq<E> toSeq() {
    return underlying == null ? Seq.empty() : underlying;
  }

  /**
   * Cost: O(1) - Returns a view on the underlying Seq
   */
  @Nonnull
  public List<E> toList() {
    return underlying == null ? Collections.emptyList() : underlying.toList();
  }

  @Nonnull
  public java.util.Set<E> toSet() {
    return new AbstractSet<E>() {
      @Nonnull
      @Override
      public Iterator<E> iterator() {
        return Set.this.iterator();
      }

      @Override
      public int size() {
        return Set.this.size();
      }
    };
  }

  /**
   * INTERNAL: Assumes the seq is a sorted, distinct seq.
   */
  @Nonnull
  private static <E extends Comparable<? super E>> Set<E> ofSeqInternal(final Seq<E> seq) {
    if (seq.isEmpty()) {
      return empty();
    }
    final SeqSimpleSorted<E> seqSimpleSorted;
    if (seq instanceof SeqSimpleSorted) {
      seqSimpleSorted = (SeqSimpleSorted<E>) seq; // assumes it's distinct
    } else {
      seqSimpleSorted = (SeqSimpleSorted<E>) seq.sorted().distinct(); // if not a SeqSimpleSorted, make it one
    }
    return new Set<>(seqSimpleSorted);
  }

  @SafeVarargs
  @Nonnull
  public static <E extends Comparable<? super E>> Set<E> of(@Nonnull final E... elements) {
    return ofSeqInternal(Seq.ofArray(elements));
  }

  @SafeVarargs
  @Nonnull
  public static <E extends Comparable<? super E>> Set<E> set(@Nonnull final E... elements) {
    return ofSeqInternal(Seq.ofArray(elements));
  }

  @Nonnull
  public static <E extends Comparable<? super E>> Set<E> ofSeq(@Nonnull final Seq<E> elements) {
    return ofSeqInternal(elements.distinct());
  }

  @Nonnull
  public static <E extends Comparable<? super E>> Set<E> ofIterable(@Nonnull final Iterable<E> elements) {
    if (elements instanceof Collection) {
      return ofCollection((Collection<E>) elements);
    }
    return ofSeqInternal(Seq.ofIterable(elements).distinct());
  }

  @Nonnull
  public static <E extends Comparable<? super E>> Set<E> ofIterator(@Nonnull final Iterator<E> elements) {
    final SetBuilder<E> b = builder();
    elements.forEachRemaining(b::add);
    return b.result();
  }

  @Nonnull
  public static <E extends Comparable<? super E>> Set<E> ofCollection(@Nonnull final Collection<E> elements) {
    if (elements.isEmpty()) {
      return empty();
    }
    if (elements instanceof SortedSet) {
      return ofSortedSet((SortedSet<E>) elements);
    }
    return ofSeqInternal(Seq.ofCollection(elements).distinct());
  }

  @Nonnull
  public static <E extends Comparable<? super E>> Set<E> ofSortedSet(@Nonnull final SortedSet<E> elements) {
    return ofSeqInternal(new SeqSimpleSorted<E>(elements.toArray()));
  }

  @SuppressWarnings("unchecked")
  private static final Set EMPTY = new Set(null);

  @Nonnull
  public static <E extends Comparable<? super E>> SetBuilder<E> builder() {
    return new SetBuilder<>();
  }

  @Nonnull
  @SuppressWarnings("unchecked")
  public static <E extends Comparable<? super E>> Set<E> empty() {
    return (Set<E>) EMPTY;
  }

  @Nonnull
  @Override
  public Iterator<E> iterator() {
    return underlying == null ? Seq.<E>empty().iterator() : underlying.iterator();
  }

  @Nonnull
  @Override
  public String toString() {
    return underlying == null ? "∅" : ('{' + underlying.asString(", ") + '}');
  }

}
