package com.simplaex.bedrock;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Builds up a collection made up of elements of type Element.
 *
 * @param <Element>          The type of the elements in the target collection.
 * @param <TargetCollection> The complete, parameterized type of the target collection.
 */
public interface Builder<Element, TargetCollection>
  extends Iterable<Element>, Collector<Element, Builder<Element, TargetCollection>, TargetCollection> {

  @Nonnull
  TargetCollection result();

  @Nonnull
  default TargetCollection build() {
    return result();
  }

  Builder<Element, TargetCollection> add(final Element elem);

  @SuppressWarnings("unchecked")
  @Override
  default Supplier<Builder<Element, TargetCollection>> supplier() {
    return () -> Try.execute(() -> getClass().getDeclaredConstructor().newInstance()).orElseThrowRuntime();
  }

  @Override
  default BiConsumer<Builder<Element, TargetCollection>, Element> accumulator() {
    return Builder::add;
  }

  @Override
  default BinaryOperator<Builder<Element, TargetCollection>> combiner() {
    return (b1, b2) -> {
      for (final Element element : b2) {
        b1.add(element);
      }
      return b1;
    };
  }

  @Override
  default Function<Builder<Element, TargetCollection>, TargetCollection> finisher() {
    return Builder::build;
  }

  @Override
  default Set<Characteristics> characteristics() {
    return Collections.emptySet();
  }
}
