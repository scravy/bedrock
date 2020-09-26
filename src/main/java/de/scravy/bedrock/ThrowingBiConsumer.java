package de.scravy.bedrock;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@FunctionalInterface
public interface ThrowingBiConsumer<A, B> extends BiConsumer<A, B> {

  void consume(final A arg1, final B arg2) throws Exception;

  @Override
  default void accept(final A arg1, final B arg2) {
    try {
      consume(arg1, arg2);
    } catch (final Error | RuntimeException exc) {
      throw exc;
    } catch (final Exception exc) {
      throw new RuntimeException(exc);
    }
  }

  default BiConsumer<A, B> safe(final Consumer<? super Exception> errorHandler) {
    return (a1, a2) -> {
      try {
        consume(a1, a2);
      } catch (final Exception exc) {
        errorHandler.accept(exc);
      }
    };
  }
}
