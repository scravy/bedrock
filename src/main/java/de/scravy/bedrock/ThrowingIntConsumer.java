package de.scravy.bedrock;

import javax.annotation.Nonnull;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

@FunctionalInterface
public interface ThrowingIntConsumer {

  void consume(final int arg) throws Exception;

  default void accept(final int arg) {
    try {
      consume(arg);
    } catch (final Error | RuntimeException exc) {
      throw exc;
    } catch (final Exception exc) {
      throw new RuntimeException(exc);
    }
  }

  @Nonnull
  default IntConsumer safe(final Consumer<? super Exception> errorHandler) {
    return arg -> {
      try {
        consume(arg);
      } catch (final Exception exc) {
        errorHandler.accept(exc);
      }
    };
  }
}
