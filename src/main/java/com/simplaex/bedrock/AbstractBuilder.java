package com.simplaex.bedrock;

import javax.annotation.Nonnull;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Objects;

public abstract class AbstractBuilder<Element, TargetCollection, This extends AbstractBuilder<Element, TargetCollection, This>>
  implements Builder<Element, TargetCollection> {

  @Override
  public abstract This add(final Element elem);

  @SuppressWarnings("unchecked")
  @SafeVarargs
  @Nonnull
  public final This addAll(final Element... elems) {
    Objects.requireNonNull(elems);
    for (final Element elem : elems) {
      add(elem);
    }
    return (This) this;
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  public This addElements(final Iterable<? extends Element> elems) {
    Objects.requireNonNull(elems);
    elems.forEach(this::add);
    return (This) this;
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  public This addFromIterator(final Iterator<? extends Element> it) {
    Objects.requireNonNull(it);
    it.forEachRemaining(this::add);
    return (This) this;
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  public This addFromEnumeration(final Enumeration<? extends Element> enumeration) {
    Objects.requireNonNull(enumeration);
    while (enumeration.hasMoreElements()) {
      add(enumeration.nextElement());
    }
    return (This) this;
  }

}
