package com.simplaex.bedrock;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.DoublePredicate;

@UtilityClass
public class Numbers {

  @SuppressWarnings("unchecked")
  public static <N extends Number> N zero(final Class<N> numberClass) {
    final Class<?> clazz = Reflections.getBoxedClassFor(numberClass);
    if (Integer.class.equals(clazz)) {
      return (N) (Integer) 0;
    }
    if (Long.class.equals(clazz)) {
      return (N) (Long) 0L;
    }
    if (Short.class.equals(clazz)) {
      return (N) (Short) (short) 0;
    }
    if (Byte.class.equals(clazz)) {
      return (N) (Byte) (byte) 0;
    }
    if (Double.class.equals(clazz)) {
      return (N) (Double) 0.0;
    }
    if (Float.class.equals(clazz)) {
      return (N) (Float) 0f;
    }
    if (BigInteger.class.equals(clazz)) {
      return (N) BigInteger.ZERO;
    }
    if (BigDecimal.class.equals(clazz)) {
      return (N) BigDecimal.ZERO;
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  public static <N extends Number> N one(final Class<N> numberClass) {
    final Class<?> clazz = Reflections.getBoxedClassFor(numberClass);
    if (Integer.class.equals(clazz)) {
      return (N) (Integer) 1;
    }
    if (Long.class.equals(clazz)) {
      return (N) (Long) 1L;
    }
    if (Short.class.equals(clazz)) {
      return (N) (Short) (short) 1;
    }
    if (Byte.class.equals(clazz)) {
      return (N) (Byte) (byte) 1;
    }
    if (Double.class.equals(clazz)) {
      return (N) (Double) 1.0;
    }
    if (Float.class.equals(clazz)) {
      return (N) (Float) 1f;
    }
    if (BigInteger.class.equals(clazz)) {
      return (N) BigInteger.ONE;
    }
    if (BigDecimal.class.equals(clazz)) {
      return (N) BigDecimal.ONE;
    }
    return null;
  }

  public static DoublePredicate isApproximately(final double expected, final double error) {
    return value -> Math.abs(expected - value) < error;
  }

}
