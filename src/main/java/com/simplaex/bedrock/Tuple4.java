package com.simplaex.bedrock;

import com.simplaex.bedrock.hlist.Nil;

import javax.annotation.Nonnull;

import java.util.List;

import static com.simplaex.bedrock.hlist.HList.hlist;

public interface Tuple4<A, B, C, D> extends Tuple3<A, B, C> {

  D getFourth();

  @Nonnull
  default Seq<Object> toSeq() {
    return Quadruple.toSeq(this);
  }

  @Nonnull
  default List<Object> toList() {
    return Quadruple.toList(this);
  }

  @Nonnull
  default com.simplaex.bedrock.hlist.C<A,
    com.simplaex.bedrock.hlist.C<B,
      com.simplaex.bedrock.hlist.C<C,
        com.simplaex.bedrock.hlist.C<D, Nil>>>> toHList4() {
    return hlist(getFirst(), getSecond(), getThird(), getFourth());
  }

  default <R> R apply(@Nonnull final Function4<A, B, C, D, R> f) {
    return f.apply(getFirst(), getSecond(), getThird(), getFourth());
  }
}
