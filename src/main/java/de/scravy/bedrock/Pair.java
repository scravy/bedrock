package de.scravy.bedrock;

import de.scravy.bedrock.hlist.C;
import de.scravy.bedrock.hlist.HList;
import de.scravy.bedrock.hlist.Nil;
import lombok.Value;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * An immutable tuple.
 *
 * @param <A> The first component of the tuple.
 * @param <B> The second component of the tuple.
 */
@Value
@Immutable
public class Pair<A, B> implements Map.Entry<A, B>, Serializable, Comparable<Pair<A, B>>, Tuple2<A, B> {

  private final A first;
  private final B second;

  public A fst() {
    return first;
  }

  public B snd() {
    return second;
  }

  @Override
  public A getKey() {
    return first;
  }

  @Override
  public B getValue() {
    return second;
  }

  /**
   * Since tuples are immutable this method will throw an UnsupportedOperationException.
   * It is only defined to implement the Map.Entry interface.
   *
   * @param value No matter what you throw in, this method is not supported.
   * @return Nothing, it throws an UnsupportedOperationException.
   * @throws UnsupportedOperationException Thrown always.
   */
  @Override
  public B setValue(final B value) throws UnsupportedOperationException {
    throw new UnsupportedOperationException();
  }

  @SuppressWarnings("unchecked")
  @Override
  public int compareTo(@Nonnull final Pair<A, B> p) {
    final int r;
    if (first == null) {
      if (p.first == null) {
        r = 0;
      } else {
        return -1;
      }
    } else if (p.first == null) {
      return 1;
    } else {
      r = ((Comparable) first).compareTo(p.first);
    }
    if (r != 0) {
      return r;
    }
    if (second == null) {
      if (p.second == null) {
        return 0;
      } else {
        return -1;
      }
    } else if (p.second == null) {
      return 1;
    } else {
      return ((Comparable) second).compareTo(p.second);
    }
  }

  @Nonnull
  public static <A, B> Pair<A, B> pair(final A a, final B b) {
    return new Pair<>(a, b);
  }

  @Nonnull
  public static <A, B> Pair<A, B> of(final A a, final B b) {
    return new Pair<>(a, b);
  }

  @Nonnull
  public static <A, B> Pair<A, B> of(final Map.Entry<A, B> entry) {
    if (entry instanceof Pair) {
      return (Pair<A, B>) entry;
    }
    return new Pair<>(entry.getKey(), entry.getValue());
  }

  @Nonnull
  public static <C, A extends C, B extends C> List<C> toList(@Nonnull final Tuple2<A, B> pair) {
    return new AbstractList<C>() {
      @Override
      public C get(final int index) {
        switch (index) {
          case 0:
            return pair.getFirst();
          case 1:
            return pair.getSecond();
          default:
            throw new IndexOutOfBoundsException(Integer.toString(index));
        }
      }

      @Override
      public int size() {
        return 2;
      }
    };
  }

  @Nonnull
  public Pair<B, A> swapped() {
    return of(getSecond(), getFirst());
  }

  @Nonnull
  public C<A, C<B, Nil>> toHList() {
    return toHList2();
  }

  public static <A, B, L extends HList<L>> Pair<A, B> fromHList(final C<A, C<B, L>> hlist) {
    return pair(hlist.getHead(), hlist.getTail().getHead());
  }

  @Nonnull
  public static <C, A extends C, B extends C> Seq<C> toSeq(final Tuple2<A, B> pair) {
    return Tuple2.toSeq(pair);
  }

  @Nonnull
  public <C, D> Pair<C, D> map(final Function<A, C> f, final Function<B, D> g) {
    return Pair.of(f.apply(fst()), g.apply(snd()));
  }

  @Nonnull
  public <C> Pair<C, B> mapFirst(final Function<A, C> f) {
    return Pair.of(f.apply(fst()), snd());
  }

  @Nonnull
  public <C> Pair<A, C> mapSecond(final Function<B, C> f) {
    return Pair.of(fst(), f.apply(snd()));
  }

  @Nonnull
  public <C> Pair<C, B> withFirst(final C v) {
    return Pair.of(v, snd());
  }

  @Nonnull
  public <C> Pair<A, C> withSecond(final C v) {
    return Pair.of(fst(), v);
  }

}
