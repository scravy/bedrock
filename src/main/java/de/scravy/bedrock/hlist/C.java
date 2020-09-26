package de.scravy.bedrock.hlist;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Value
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = false)
public class C<E, L extends HList<L>> extends HList<C<E, L>> {

  private final E head;
  private final L tail;

  @Override
  public int size() {
    return 1 + tail.size();
  }

  @Override
  public <T> T foldl(@Nonnull final BiFunction<T, Object, T> f, final T init) {
    return tail.foldl(f, f.apply(init, head));
  }

  @Override
  public <T> T foldr(@Nonnull final BiFunction<Object, T, T> f, final T init) {
    return f.apply(head, tail.foldr(f, init));
  }

  @Override
  C<E, L> mask(@Nonnegative final int index, @Nonnull final ForEachWithIndexPredicate predicate) {
    final C<E, L> thiz = predicate.test(index, head) ? this : withHead(null);
    return thiz.withTail(tail.mask(index + 1, predicate));
  }

  @Nonnull
  @Override
  public Iterator<Object> iterator() {
    return new Iterator<Object>() {
      HList current = C.this;

      @Override
      public boolean hasNext() {
        return current != Nil.INSTANCE;
      }

      @Override
      public Object next() {
        final C<?, ?> xs = (C) current;
        current = xs.tail;
        return xs.head;
      }
    };
  }

  @Nonnull
  public <F> C<F, C<E, L>> cons(final F elem) {
    return new C<>(elem, this);
  }

  @Nonnull
  public <F> C<F, L> withHead(final F head) {
    if (head == this.head) {
      @SuppressWarnings("unchecked") final C<F, L> thiz = (C<F, L>) this;
      return thiz;
    }
    return new C<>(head, tail);
  }

  @Nonnull
  public <M extends HList<M>> C<E, M> withTail(final M tail) {
    if (tail == this.tail) {
      @SuppressWarnings("unchecked") final C<E, M> thiz = (C<E, M>) this;
      return thiz;
    }
    return new C<>(head, tail);
  }

  @Override
  public String toString() {
    return stream()
      .map(Objects::toString)
      .collect(Collectors.joining(",", "[", "]"));
  }

  @Override
  public int compareTo(@Nonnull final C<E, L> that) {
    final int headComparison = Objects.compare(getHead(), that.getHead(), Comparator.nullsFirst((l, r) -> {
      @SuppressWarnings("unchecked") final int result = ((Comparable) l).compareTo(r);
      return result;
    }));
    if (headComparison != 0) {
      return headComparison;
    }
    return getTail().compareTo(that.getTail());
  }
}
