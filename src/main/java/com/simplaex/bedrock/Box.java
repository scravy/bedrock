package com.simplaex.bedrock;

import lombok.*;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;
import java.util.function.LongUnaryOperator;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class Box<T> {

  public abstract T getValue();

  public abstract void setValue(T value);

  public T apply(@Nonnull final Function<T, T> function) {
    Objects.requireNonNull(function, "'function' must not be null.");
    setValue(function.apply(getValue()));
    return getValue();
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
  public static class IntBox extends Box<Integer> {

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
  }

  @EqualsAndHashCode(callSuper = false)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  public static class LongBox extends Box<Long> {

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
  }

  @EqualsAndHashCode(callSuper = false)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  public static class DoubleBox extends Box<Double> {

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
  }
}
