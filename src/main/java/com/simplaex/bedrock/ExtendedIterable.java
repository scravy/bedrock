package com.simplaex.bedrock;

@FunctionalInterface
public interface ExtendedIterable<T> extends Iterable<T> {

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

  static <T> ExtendedIterable<T> fromIterable(final Iterable<T> iterable) {
    return iterable::iterator;
  }

}
