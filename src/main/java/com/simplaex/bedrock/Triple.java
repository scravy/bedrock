package com.simplaex.bedrock;

import lombok.Value;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.List;
import java.util.function.Function;

@Value(staticConstructor = "of")
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

  public static <D, A extends D, B extends D, C extends D> List<D> toList(final Triple<A, B, C> tuple) {
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
            return null;
        }
      }

      @Override
      public int size() {
        return 3;
      }
    };
  }

  public List<Object> toList() {
    return toList(this);
  }

  public <D, E, F> Triple<D, E, F> map(final Function<A, D> f, final Function<B, E> g, final Function<C, F> h) {
    return Triple.of(f.apply(getFirst()), g.apply(getSecond()), h.apply(getThird()));
  }

  public <D> Triple<D, B, C> mapFirst(final Function<A, D> f) {
    return Triple.of(f.apply(getFirst()), getSecond(), getThird());
  }

  public <D> Triple<A, D, C> mapSecond(final Function<B, D> f) {
    return Triple.of(getFirst(), f.apply(getSecond()), getThird());
  }

  public <D> Triple<A, B, D> mapThird(final Function<C, D> f) {
    return Triple.of(getFirst(), getSecond(), f.apply(getThird()));
  }

  public <D> Triple<D, B, C> withFirst(final D v) {
    return Triple.of(v, getSecond(), getThird());
  }

  public <D> Triple<A, D, C> withSecond(final D v) {
    return Triple.of(getFirst(), v, getThird());
  }

  public <D> Triple<A, B, D> withThird(final D v) {
    return Triple.of(getFirst(), getSecond(), v);
  }
}