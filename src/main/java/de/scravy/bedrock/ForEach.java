package de.scravy.bedrock;

import lombok.experimental.UtilityClass;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@UtilityClass
public class ForEach {

  static <A> void forEach(
    @Nonnull final Iterable<A> seq,
    @Nonnull final Consumer<? super A> consumer
  ) {
    Objects.requireNonNull(seq);
    Objects.requireNonNull(consumer);
    seq.forEach(consumer);
  }

  static <A> void forEach(
    @Nonnull final Iterator<A> seq,
    @Nonnull final Consumer<? super A> consumer
  ) {
    Objects.requireNonNull(seq);
    Objects.requireNonNull(consumer);
    seq.forEachRemaining(consumer);
  }

  static <A, B> void forEach(
    @Nonnull final Iterable<? extends Tuple2<A, B>> seq,
    @Nonnull final BiConsumer<? super A, ? super B> consumer
  ) {
    Objects.requireNonNull(seq);
    Objects.requireNonNull(consumer);
    seq.forEach(tuple -> consumer.accept(tuple.getFirst(), tuple.getSecond()));
  }

  @FunctionalInterface
  interface TriConsumer<A, B, C> {
    void accept(final A a, final B b, final C c);
  }

  static <A, B, C> void forEach(
    @Nonnull final Iterable<? extends Tuple3<A, B, C>> seq,
    @Nonnull final TriConsumer<? super A, ? super B, ? super C> consumer
  ) {
    Objects.requireNonNull(seq);
    Objects.requireNonNull(consumer);
    seq.forEach(triple -> consumer.accept(triple.getFirst(), triple.getSecond(), triple.getThird()));
  }

  static <A> void forEach(
    @Nonnull final A[] as,
    @Nonnull final Consumer<? super A> consumer
  ) {
    Objects.requireNonNull(as);
    Objects.requireNonNull(consumer);
    for (final A a : as) {
      consumer.accept(a);
    }
  }

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  static <A> void forEach(
    @Nonnull final Optional<A> optional,
    @Nonnull final Consumer<? super A> consumer
  ) {
    Objects.requireNonNull(optional);
    Objects.requireNonNull(consumer);
    optional.ifPresent(consumer);
  }
}
