package com.simplaex.bedrock;

import lombok.Value;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.List;
import java.util.function.Function;

@Value(staticConstructor = "of")
@Immutable
public class Quadruple<A, B, C, D> implements Serializable, Comparable<Quadruple<A, B, C, D>>, Tuple4<A, B, C, D> {

  private A first;
  private B second;
  private C third;
  private D fourth;

  @SuppressWarnings("unchecked")
  @Override
  public int compareTo(@Nonnull final Quadruple<A, B, C, D> tuple) {
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
        r = 0;
      } else {
        return -1;
      }
    } else if (tuple.third == null) {
      return 1;
    } else {
      r = ((Comparable) third).compareTo(tuple.third);
    }
    if (r != 0) {
      return r;
    }
    if (fourth == null) {
      if (tuple.fourth == null) {
        return 0;
      } else {
        return -1;
      }
    } else if (tuple.fourth == null) {
      return 1;
    } else {
      return ((Comparable) fourth).compareTo(tuple.fourth);
    }
  }

  @Nonnull
  public static <E, A extends E, B extends E, C extends E, D extends E> List<E> toList(final Quadruple<A, B, C, D> tuple) {
    return new AbstractList<E>() {
      @Override
      public E get(final int index) {
        switch (index) {
          case 0:
            return tuple.getFirst();
          case 1:
            return tuple.getSecond();
          case 2:
            return tuple.getThird();
          case 3:
            return tuple.getFourth();
          default:
            return null;
        }
      }

      @Override
      public int size() {
        return 4;
      }
    };
  }

  @Nonnull
  public List<Object> toList() {
    return toList(this);
  }

  @Nonnull
  public static <E, A extends E, B extends E, C extends E, D extends E> Seq<E> toSeq(final Tuple4<A, B, C, D> quadruple) {
    return Seq.of(quadruple.getFirst(), quadruple.getSecond(), quadruple.getThird(), quadruple.getFourth());
  }

  @Nonnull
  public Seq<Object> toSeq() {
    return toSeq(this);
  }

  @Nonnull
  public <E, F, G, H> Quadruple<E, F, G, H> map(
    final Function<A, E> f,
    final Function<B, F> g,
    final Function<C, G> h,
    final Function<D, H> i
  ) {
    return Quadruple.of(f.apply(getFirst()), g.apply(getSecond()), h.apply(getThird()), i.apply(getFourth()));
  }

  @Nonnull
  public <E> Quadruple<E, B, C, D> mapFirst(final Function<A, E> f) {
    return Quadruple.of(f.apply(getFirst()), getSecond(), getThird(), getFourth());
  }

  @Nonnull
  public <E> Quadruple<A, E, C, D> mapSecond(final Function<B, E> f) {
    return Quadruple.of(getFirst(), f.apply(getSecond()), getThird(), getFourth());
  }

  @Nonnull
  public <E> Quadruple<A, B, E, D> mapThird(final Function<C, E> f) {
    return Quadruple.of(getFirst(), getSecond(), f.apply(getThird()), getFourth());
  }

  @Nonnull
  public <E> Quadruple<A, B, C, E> mapFourth(final Function<D, E> f) {
    return Quadruple.of(getFirst(), getSecond(), getThird(), f.apply(getFourth()));
  }

  @Nonnull
  public <E> Quadruple<E, B, C, D> withFirst(final E v) {
    return Quadruple.of(v, getSecond(), getThird(), getFourth());
  }

  @Nonnull
  public <E> Quadruple<A, E, C, D> withSecond(final E v) {
    return Quadruple.of(getFirst(), v, getThird(), getFourth());
  }

  @Nonnull
  public <E> Quadruple<A, B, E, D> withThird(final E v) {
    return Quadruple.of(getFirst(), getSecond(), v, getFourth());
  }

  @Nonnull
  public <E> Quadruple<A, B, C, E> withFourth(final E v) {
    return Quadruple.of(getFirst(), getSecond(), getThird(), v);
  }

  public static <A, B, C, D> Quadruple<A, B, C, D> quadruple(final A a, final B b, final C c, final D d) {
    return Quadruple.of(a, b, c, d);
  }
}
