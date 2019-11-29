package com.simplaex.bedrock;

import javax.annotation.Nonnull;

/**
 * Builds up a collection made up of elements of type Element.
 *
 * @param <Element>          The type of the elements in the target collection.
 * @param <TargetCollection> The complete, parameterized type of the target collection.
 */
public interface Builder<Element, TargetCollection> {

  @Nonnull
  TargetCollection result();

  @Nonnull
  default TargetCollection build() {
    return result();
  }

  Builder<Element, TargetCollection> add(final Element elem);

}
