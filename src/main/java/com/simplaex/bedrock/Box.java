package com.simplaex.bedrock;

import lombok.*;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.*;

/**
 * A Box holding a mutable value.
 *
 * @param <T> The type of the value.
 */
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class Box<T> implements Function0<T>, Consumer<T>, ExtendedIterable<T> {

  interface NumberBox {

    void inc();

    void dec();

  }

  @Nonnull
  @Override
  public Iterator<T> iterator() {
    return Collections.singleton(get()).iterator();
  }

  public T apply(@Nonnull final Function<T, T> function) {
    Objects.requireNonNull(function, "'function' must not be null.");
    accept(function.apply(get()));
    return get();
  }

  public T applyAtomic(@Nonnull final Function<T, T> function) {
    synchronized (this) {
      return apply(function);
    }
  }

  public boolean exists(@Nonnull final Predicate<T> predicate) {
    Objects.requireNonNull(predicate, "'predicate' must not be null.");
    return predicate.test(get());
  }

  public boolean contains(final T value) {
    if (value == null) {
      return get() == null;
    }
    return value.equals(get());
  }

  @Nonnull
  public static <T> Box<T> box(final T value) {
    return new AnyBox<>(value);
  }

  @Nonnull
  public static <T> Box<T> box() {
    return new AnyBox<>(null);
  }

  @Nonnull
  public static IntBox intBox(final int value) {
    return new IntBox(value);
  }

  @Nonnull
  public static LongBox longBox(final long value) {
    return new LongBox(value);
  }

  @Nonnull
  public static DoubleBox doubleBox(final double value) {
    return new DoubleBox(value);
  }

  @Nonnull
  public static IntBox intBox() {
    return new IntBox(0);
  }

  @Nonnull
  public static LongBox longBox() {
    return new LongBox(0L);
  }

  @Nonnull
  public static DoubleBox doubleBox() {
    return new DoubleBox(0.0);
  }

  @EqualsAndHashCode(callSuper = false)
  @Data
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  private static class AnyBox<T> extends Box<T> {

    private T value;

    public T get() {
      return value;
    }

    public void accept(final T value) {
      setValue(value);
    }
  }

  @EqualsAndHashCode(callSuper = false)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  public static class IntBox extends Box<Integer> implements NumberBox {

    private int intValue;

    @Nonnull
    @Override
    public Integer get() {
      return intValue;
    }

    @Override
    public void accept(@Nonnull final Integer value) {
      intValue = value;
    }

    public int update(@Nonnull final IntUnaryOperator function) {
      intValue = function.applyAsInt(intValue);
      return intValue;
    }

    public int updateAtomic(@Nonnull final IntUnaryOperator function) {
      synchronized (this) {
        intValue = function.applyAsInt(intValue);
      }
      return intValue;
    }

    @Override
    public void inc() {
      intValue += 1;
    }

    @Override
    public void dec() {
      intValue -= 1;
    }

    public void add(final int value) {
      intValue += value;
    }

    public void sub(final int value) {
      intValue -= value;
    }

    @Deprecated
    public boolean exists(final IntPredicate predicate) {
      return intExists(predicate);
    }

    public boolean intExists(final IntPredicate predicate) {
      return predicate.test(intValue);
    }

    public void setValue(final int value) {
      this.intValue = value;
    }

    public int getValue() {
      return intValue;
    }
  }

  @EqualsAndHashCode(callSuper = false)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  public static class LongBox extends Box<Long> implements NumberBox {

    private long longValue;

    @Nonnull
    @Override
    public Long get() {
      return longValue;
    }

    @Override
    public void accept(@Nonnull final Long value) {
      longValue = value;
    }

    public long update(@Nonnull final LongUnaryOperator function) {
      longValue = function.applyAsLong(longValue);
      return longValue;
    }

    public long updateAtomic(@Nonnull final LongUnaryOperator function) {
      synchronized (this) {
        longValue = function.applyAsLong(longValue);
      }
      return longValue;
    }

    @Override
    public void inc() {
      longValue += 1;
    }

    @Override
    public void dec() {
      longValue -= 1;
    }

    public void add(final int value) {
      longValue += value;
    }

    public void sub(final int value) {
      longValue -= value;
    }

    public void add(final long value) {
      longValue += value;
    }

    public void sub(final long value) {
      longValue -= value;
    }

    @Deprecated
    public boolean exists(final LongPredicate predicate) {
      return longExists(predicate);
    }

    public boolean longExists(final LongPredicate predicate) {
      return predicate.test(longValue);
    }

    public void setValue(final long value) {
      this.longValue = value;
    }

    public long getValue() {
      return longValue;
    }
  }

  @EqualsAndHashCode(callSuper = false)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  public static class DoubleBox extends Box<Double> implements NumberBox {

    private double doubleValue;

    @Nonnull
    @Override
    public Double get() {
      return doubleValue;
    }

    @Override
    public void accept(@Nonnull final Double value) {
      doubleValue = value;
    }

    public double update(@Nonnull final DoubleUnaryOperator function) {
      doubleValue = function.applyAsDouble(doubleValue);
      return doubleValue;
    }

    public double updateAtomic(@Nonnull final DoubleUnaryOperator function) {
      synchronized (this) {
        doubleValue = function.applyAsDouble(doubleValue);
      }
      return doubleValue;
    }

    @Override
    public void inc() {
      doubleValue += 1.0;
    }

    @Override
    public void dec() {
      doubleValue -= 1.0;
    }

    public void add(final double value) {
      doubleValue += value;
    }

    public void sub(final double value) {
      doubleValue -= value;
    }

    @Deprecated
    public boolean exists(final DoublePredicate predicate) {
      return doubleExists(predicate);
    }

    public boolean doubleExists(final DoublePredicate predicate) {
      return predicate.test(doubleValue);
    }

    public void setValue(final double value) {
      this.doubleValue = value;
    }

    public double getValue() {
      return doubleValue;
    }
  }
}
