package de.scravy.bedrock;

import de.scravy.bedrock.hlist.Nil;

import javax.annotation.Nonnull;

import java.util.List;

import static de.scravy.bedrock.hlist.HList.hlist;

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
  default de.scravy.bedrock.hlist.C<A,
    de.scravy.bedrock.hlist.C<B,
      de.scravy.bedrock.hlist.C<C,
        de.scravy.bedrock.hlist.C<D, Nil>>>> toHList4() {
    return hlist(getFirst(), getSecond(), getThird(), getFourth());
  }

  default <R> R apply(@Nonnull final Function4<A, B, C, D, R> f) {
    return f.apply(getFirst(), getSecond(), getThird(), getFourth());
  }
}
