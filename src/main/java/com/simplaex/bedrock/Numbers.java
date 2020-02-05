package com.simplaex.bedrock;

import lombok.experimental.UtilityClass;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;
import java.util.function.DoublePredicate;

@UtilityClass
public class Numbers {

  @SuppressWarnings("unchecked")
  @Nonnull
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
    return Control.findFirstNonNull(
      clazz,
      c -> Reflections
        .getFactory(String.class, c)
        .map(factory -> (N) factory.apply("0"))
        .orElse(null),
      c -> Reflections
        .getFactory(int.class, c)
        .map(factory -> (N) factory.apply(0))
        .orElse(null),
      c -> Reflections
        .getFactory(long.class, c)
        .map(factory -> (N) factory.apply(0L))
        .orElse(null)
    ).orElseThrow(() -> new IllegalArgumentException(Objects.toString(clazz)));
  }

  @SuppressWarnings("unchecked")
  @Nonnull
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
    return Control.findFirstNonNull(
      clazz,
      c -> Reflections
        .getFactory(String.class, c)
        .map(factory -> (N) factory.apply("1"))
        .orElse(null),
      c -> Reflections
        .getFactory(int.class, c)
        .map(factory -> (N) factory.apply(1))
        .orElse(null),
      c -> Reflections
        .getFactory(long.class, c)
        .map(factory -> (N) factory.apply(1L))
        .orElse(null)
    ).orElseThrow(() -> new IllegalArgumentException(Objects.toString(clazz)));
  }

  public static DoublePredicate isApproximately(final double expected, final double error) {
    return value -> Math.abs(expected - value) < error;
  }

  public static int byteToInt(final byte b) {
    return b & 0xFF;
  }

  public static int intFromBytes(
    final byte b0, final byte b1, final byte b2, final byte b3
  ) {
    int value = 0;
    value |= byteToInt(b0);
    value <<= 8;
    value |= byteToInt(b1);
    value <<= 8;
    value |= byteToInt(b2);
    value <<= 8;
    value |= byteToInt(b3);
    return value;
  }

  public static int intFromByteArray(
    @Nonnull final byte[] bytes
  ) {
    Objects.requireNonNull(bytes, "'bytes' must not be null.");
    return intFromByteArrayAtOffset(0, bytes);
  }

  public static int intFromByteArrayAtOffset(
    @Nonnegative final int offset,
    @Nonnull final byte[] bytes
  ) {
    Objects.requireNonNull(bytes, "'bytes' must not be null.");
    return intFromBytes(bytes[offset], bytes[offset + 1], bytes[offset + 2], bytes[offset + 3]);
  }

  public static long longFromBytes(
    final byte b0, final byte b1, final byte b2, final byte b3,
    final byte b4, final byte b5, final byte b6, final byte b7
  ) {
    long value = 0L;
    value |= byteToInt(b0);
    value <<= 8;
    value |= byteToInt(b1);
    value <<= 8;
    value |= byteToInt(b2);
    value <<= 8;
    value |= byteToInt(b3);
    value <<= 8;
    value |= byteToInt(b4);
    value <<= 8;
    value |= byteToInt(b5);
    value <<= 8;
    value |= byteToInt(b6);
    value <<= 8;
    value |= byteToInt(b7);
    return value;
  }

  public static long longFromByteArray(
    @Nonnull final byte[] bytes
  ) {
    Objects.requireNonNull(bytes, "'bytes' must not be null.");
    return longFromByteArrayAtOffset(0, bytes);
  }

  public static long longFromByteArrayAtOffset(
    @Nonnegative final int offset,
    @Nonnull final byte[] bytes
  ) {
    Objects.requireNonNull(bytes, "'bytes' must not be null.");
    return longFromBytes(
      bytes[offset], bytes[offset + 1], bytes[offset + 2], bytes[offset + 3],
      bytes[offset + 4], bytes[offset + 5], bytes[offset + 6], bytes[offset + 7]
    );
  }

}
