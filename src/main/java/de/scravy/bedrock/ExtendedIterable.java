package de.scravy.bedrock;

@FunctionalInterface
public interface ExtendedIterable<T> extends Iterable<T>, HasLengthAtLeast {

  @FunctionalInterface
  interface ForEachWithIndexConsumer<T> {
    void accept(final int index, final T thing);
  }

  default void forEachWithIndex(final ForEachWithIndexConsumer<T> consumer) {
    int i = 0;
    for (final T t : this) {
      try {
        consumer.accept(i, t);
      } finally {
        i += 1;
      }
    }
  }

  @Override
  default boolean lengthAtLeast(final int length) {
    int count = 0;
    for (final T ignore : this) {
      count += 1;
      if (count >= length) {
        return true;
      }
    }
    return false;
  }

  static <T> ExtendedIterable<T> fromIterable(final Iterable<T> iterable) {
    return iterable::iterator;
  }

}
