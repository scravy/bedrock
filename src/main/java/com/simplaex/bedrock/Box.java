package com.simplaex.bedrock;

import lombok.*;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.*;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class Box<T> {

  interface NumberBox {

    void inc();

    void dec();

  }

  public abstract T getValue();

  public abstract void setValue(T value);

  public T apply(@Nonnull final Function<T, T> function) {
    Objects.requireNonNull(function, "'function' must not be null.");
    setValue(function.apply(getValue()));
    return getValue();
  }

  public boolean exists(@Nonnull final Predicate<T> predicate) {
    Objects.requireNonNull(predicate, "'predicate' must not be null.");
    return predicate.test(getValue());
  }

  public boolean contains(final T value) {
    if (value == null) {
      return getValue() == null;
    }
    return value.equals(getValue());
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

  @EqualsAndHashCode(callSuper = false)
  @Data
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  private static class AnyBox<T> extends Box<T> {

    private T value;

  }

  @EqualsAndHashCode(callSuper = false)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  public static class IntBox extends Box<Integer> implements NumberBox {

    private int intValue;

    @Nonnull
    @Override
    public Integer getValue() {
      return intValue;
    }

    @Override
    public void setValue(@Nonnull final Integer value) {
      intValue = value;
    }

    public int update(@Nonnull final IntUnaryOperator function) {
      intValue = function.applyAsInt(intValue);
      return intValue;
    }

    @Override
    public void inc() {
      intValue = intValue += 1;
    }

    @Override
    public void dec() {
      intValue = intValue -= 1;
    }

    public void add(final int value) {
      intValue += value;
    }

    public void sub(final int value) {
      intValue -= value;
    }

    public boolean exists(final IntPredicate predicate) {
      return predicate.test(intValue);
    }
  }

  @EqualsAndHashCode(callSuper = false)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  public static class LongBox extends Box<Long> implements NumberBox {

    private long longValue;

    @Nonnull
    @Override
    public Long getValue() {
      return longValue;
    }

    @Override
    public void setValue(@Nonnull final Long value) {
      longValue = value;
    }

    public long update(@Nonnull final LongUnaryOperator function) {
      longValue = function.applyAsLong(longValue);
      return longValue;
    }

    @Override
    public void inc() {
      longValue = longValue += 1;
    }

    @Override
    public void dec() {
      longValue = longValue -= 1;
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

    public boolean exists(final LongPredicate predicate) {
      return predicate.test(longValue);
    }
  }

  @EqualsAndHashCode(callSuper = false)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  public static class DoubleBox extends Box<Double> implements NumberBox {

    private double doubleValue;

    @Nonnull
    @Override
    public Double getValue() {
      return doubleValue;
    }

    @Override
    public void setValue(@Nonnull final Double value) {
      doubleValue = value;
    }

    public double update(@Nonnull final DoubleUnaryOperator function) {
      doubleValue = function.applyAsDouble(doubleValue);
      return doubleValue;
    }

    @Override
    public void inc() {
      doubleValue = doubleValue += 1.0;
    }

    @Override
    public void dec() {
      doubleValue = doubleValue -= 1.0;
    }

    public void add(final double value) {
      doubleValue += value;
    }

    public void sub(final double value) {
      doubleValue -= value;
    }

    public boolean exists(final DoublePredicate predicate) {
      return predicate.test(doubleValue);
    }
  }
}
