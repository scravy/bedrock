package com.simplaex.bedrock;

import com.simplaex.bedrock.hlist.C;
import com.simplaex.bedrock.hlist.Nil;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.BiFunction;

import static com.simplaex.bedrock.hlist.HList.hlist;

public interface Tuple2<A, B> {

  A getFirst();

  B getSecond();

  @Nonnull
  default List<Object> toList() {
    return Pair.toList(this);
  }

  @Nonnull
  default Seq<Object> toSeq() {
    return Pair.toSeq(this);
  }

  @Nonnull
  default C<A, C<B, Nil>> toHList2() {
    return hlist(getFirst(), getSecond());
  }

  default <R> R apply(@Nonnull final BiFunction<A, B, R> f) {
    return f.apply(getFirst(), getSecond());
  }

  @Nonnull
  static <C, A extends C, B extends C> Seq<C> toSeq(final Tuple2<A, B> pair) {
    return Seq.of(pair.getFirst(), pair.getSecond());
  }

}
