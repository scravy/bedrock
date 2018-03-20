package com.simplaex.bedrock;

import lombok.experimental.UtilityClass;

import java.util.function.Function;

@UtilityClass
public class Functions {

  public static <A, B, C> Function<A, C> compose(final Function<B, C> f, final Function<A, B> g) {
    return a -> f.apply(g.apply(a));
  }

}
