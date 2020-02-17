package com.simplaex.bedrock;

import com.simplaex.bedrock.hlist.HList;
import com.simplaex.bedrock.hlist.Nil;
import lombok.Value;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Value(staticConstructor = "of")
@Immutable
public class Triple<A, B, C> implements Serializable, Comparable<Triple<A, B, C>>, Tuple3<A, B, C> {

  private A first;
  private B second;
  private C third;

  @SuppressWarnings("unchecked")
  @Override
  public int compareTo(@Nonnull final Triple<A, B, C> tuple) {
    int r;
    if (first == null) {
      if (tuple.first == null) {
        r = 0;
      } else {
        return -1;
      }
    } else if (tuple.first == null) {
      return 1;
    } else {
      r = ((Comparable) first).compareTo(tuple.first);
    }
    if (r != 0) {
      return r;
    }
    if (second == null) {
      if (tuple.second == null) {
        r = 0;
      } else {
        return -1;
      }
    } else if (tuple.second == null) {
      return 1;
    } else {
      r = ((Comparable) second).compareTo(tuple.second);
    }
    if (r != 0) {
      return r;
    }
    if (third == null) {
      if (tuple.third == null) {
        return 0;
      } else {
        return -1;
      }
    } else if (tuple.third == null) {
      return 1;
    } else {
      return ((Comparable) third).compareTo(tuple.third);
    }
  }

  @Nonnull
  public static <D, A extends D, B extends D, C extends D> List<D> toList(@Nonnull final Tuple3<A, B, C> tuple) {
    return new AbstractList<D>() {
      @Override
      public D get(final int index) {
        switch (index) {
          case 0:
            return tuple.getFirst();
          case 1:
            return tuple.getSecond();
          case 2:
            return tuple.getThird();
          default:
            throw new IndexOutOfBoundsException(Integer.toString(index));
        }
      }

      @Override
      public int size() {
        return 3;
      }
    };
  }

  @Nonnull
  public com.simplaex.bedrock.hlist.C<A, com.simplaex.bedrock.hlist.C<B, com.simplaex.bedrock.hlist.C<C, Nil>>> toHList() {
    return toHList3();
  }

  @Nonnull
  public static <A, B, C, L extends HList<L>> Triple<A, B, C> fromHList(
    @Nonnull final com.simplaex.bedrock.hlist.C<A, com.simplaex.bedrock.hlist.C<B, com.simplaex.bedrock.hlist.C<C, L>>> hlist) {
    return triple(hlist.getHead(), hlist.getTail().getHead(), hlist.getTail().getTail().getHead());
  }

  @Nonnull
  public static <D, A extends D, B extends D, C extends D> Seq<D> toSeq(
    @Nonnull final Tuple3<A, B, C> triple
  ) {
    return Seq.of(triple.getFirst(), triple.getSecond(), triple.getThird());
  }

  @Nonnull
  public <D, E, F> Triple<D, E, F> map(
    @Nonnull final Function<A, D> f,
    @Nonnull final Function<B, E> g,
    @Nonnull final Function<C, F> h
  ) {
    return Triple.of(f.apply(getFirst()), g.apply(getSecond()), h.apply(getThird()));
  }

  @Nonnull
  public <D> Triple<D, B, C> mapFirst(@Nonnull final Function<A, D> f) {
    Objects.requireNonNull(f, "Function 'f' must not be null");
    return Triple.of(f.apply(getFirst()), getSecond(), getThird());
  }

  @Nonnull
  public <D> Triple<A, D, C> mapSecond(@Nonnull final Function<B, D> f) {
    Objects.requireNonNull(f, "Function 'f' must not be null");
    return Triple.of(getFirst(), f.apply(getSecond()), getThird());
  }

  @Nonnull
  public <D> Triple<A, B, D> mapThird(@Nonnull final Function<C, D> f) {
    Objects.requireNonNull(f, "Function 'f' must not be null");
    return Triple.of(getFirst(), getSecond(), f.apply(getThird()));
  }

  @Nonnull
  public <D> Triple<D, B, C> withFirst(final D v) {
    return Triple.of(v, getSecond(), getThird());
  }

  @Nonnull
  public <D> Triple<A, D, C> withSecond(final D v) {
    return Triple.of(getFirst(), v, getThird());
  }

  @Nonnull
  public <D> Triple<A, B, D> withThird(final D v) {
    return Triple.of(getFirst(), getSecond(), v);
  }

  public static <A, B, C> Triple<A, B, C> triple(final A a, final B b, final C c) {
    return Triple.of(a, b, c);
  }
}
