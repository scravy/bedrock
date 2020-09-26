package de.scravy.bedrock;

import de.scravy.bedrock.hlist.Nil;

import javax.annotation.Nonnull;

import java.util.List;

import static de.scravy.bedrock.hlist.HList.hlist;

public interface Tuple3<A,B,C> extends Tuple2<A,B> {

  C getThird();

  @Nonnull
  default de.scravy.bedrock.hlist.C<A, de.scravy.bedrock.hlist.C<B, de.scravy.bedrock.hlist.C<C, Nil>>> toHList3() {
    return hlist(getFirst(), getSecond(), getThird());
  }

  @Nonnull
  default Seq<Object> toSeq() {
    return Triple.toSeq(this);
  }

  @Nonnull
  default List<Object> toList() {
    return Triple.toList(this);
  }

  default <R> R apply(@Nonnull final Function3<A, B, C, R> f) {
    return f.apply(getFirst(), getSecond(), getThird());
  }
}
