package com.simplaex.bedrock;

import lombok.experimental.UtilityClass;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.function.DoubleUnaryOperator;
import java.util.function.IntUnaryOperator;
import java.util.function.LongUnaryOperator;
import java.util.function.UnaryOperator;

@SuppressWarnings({"unused", "RedundantCast"})
@UtilityClass
public class Operators {

  public static int plus(final int a, final int b) {
    return a + b;
  }

  @Nonnull
  public static IntUnaryOperator plus(final int a) {
    return b -> a + b;
  }

  public static long plus(final long a, final long b) {
    return a + b;
  }

  @Nonnull
  public static LongUnaryOperator plus(final long a) {
    return b -> a + b;
  }

  public static double plus(final double a, final double b) {
    return a + b;
  }

  @Nonnull
  public static DoubleUnaryOperator plus(final double a) {
    return b -> a + b;
  }

  public static Integer plus(@Nullable final Integer a, @Nullable final Integer b) {
    if (a == null || b == null) {
      return null;
    }
    return a + b;
  }

  @Nonnull
  public static UnaryOperator<Integer> plus(@Nullable final Integer a) {
    return b -> plus(a, b);
  }

  public static Long plus(@Nullable final Long a, @Nullable final Long b) {
    if (a == null || b == null) {
      return null;
    }
    return a + b;
  }

  @Nonnull
  public static UnaryOperator<Long> plus(@Nullable final Long a) {
    return b -> plus(a, b);
  }

  public static Double plus(@Nullable final Double a, @Nullable final Double b) {
    if (a == null || b == null) {
      return null;
    }
    return a + b;
  }

  @Nonnull
  public static UnaryOperator<Double> plus(@Nullable final Double a) {
    return b -> plus(a, b);
  }

  public static BigInteger plus(@Nullable final BigInteger a, @Nullable final BigInteger b) {
    if (a == null || b == null) {
      return null;
    }
    return a.add(b);
  }

  @Nonnull
  public static UnaryOperator<BigInteger> plus(@Nullable final BigInteger a) {
    return b -> plus(a, b);
  }

  public static BigDecimal plus(@Nullable final BigDecimal a, @Nullable final BigDecimal b) {
    if (a == null || b == null) {
      return null;
    }
    return a.add(b);
  }

  @Nonnull
  public static UnaryOperator<BigDecimal> plus(@Nullable final BigDecimal a) {
    return b -> plus(a, b);
  }

  @SuppressWarnings("unchecked")
  public static <N extends Number> N plus(@Nullable final N a, @Nullable final N b) {
    if (a instanceof Integer && b instanceof Integer) {
      return (N) (Integer) (((Integer) a) + ((Integer) b));
    } else if (a instanceof Long && b instanceof Long) {
      return (N) (Long) (((Long) a) + ((Long) b));
    } else if (a instanceof Double && b instanceof Double) {
      return (N) (Double) (((Double) a) + ((Double) b));
    } else if (a instanceof BigInteger && b instanceof BigInteger) {
      return (N) ((BigInteger) a).add((BigInteger) b);
    } else if (a instanceof BigDecimal && b instanceof BigDecimal) {
      return (N) ((BigDecimal) a).add((BigDecimal) b);
    }
    return null;
  }

  @Nonnull
  public static <N extends Number> UnaryOperator<N> plus(@Nullable final N a) {
    return b -> plus(a, b);
  }

  public static int or(final int a, final int b) {
    return a | b;
  }

  @Nonnull
  public static IntUnaryOperator or(final int a) {
    return b -> a | b;
  }

  public static long or(final long a, final long b) {
    return a | b;
  }

  @Nonnull
  public static LongUnaryOperator or(final long a) {
    return b -> a | b;
  }

  public static Integer or(@Nullable final Integer a, @Nullable final Integer b) {
    if (a == null || b == null) {
      return null;
    }
    return a | b;
  }

  @Nonnull
  public static UnaryOperator<Integer> or(@Nullable final Integer a) {
    return b -> or(a, b);
  }

  public static Long or(@Nullable final Long a, @Nullable final Long b) {
    if (a == null || b == null) {
      return null;
    }
    return a | b;
  }

  @Nonnull
  public static UnaryOperator<Long> or(@Nullable final Long a) {
    return b -> or(a, b);
  }

  public static BigInteger or(@Nullable final BigInteger a, @Nullable final BigInteger b) {
    if (a == null || b == null) {
      return null;
    }
    return a.or(b);
  }

  @Nonnull
  public static UnaryOperator<BigInteger> or(@Nullable final BigInteger a) {
    return b -> or(a, b);
  }

  @SuppressWarnings("unchecked")
  public static <N extends Number> N or(@Nullable final N a, @Nullable final N b) {
    if (a instanceof Integer && b instanceof Integer) {
      return (N) (Integer) (((Integer) a) | ((Integer) b));
    } else if (a instanceof Long && b instanceof Long) {
      return (N) (Long) (((Long) a) | ((Long) b));
    } else if (a instanceof BigInteger && b instanceof BigInteger) {
      return (N) ((BigInteger) a).or((BigInteger) b);
    }
    return null;
  }

  @Nonnull
  public static <N extends Number> UnaryOperator<N> or(@Nullable final N a) {
    return b -> or(a, b);
  }

  public static int minus(final int a, final int b) {
    return a - b;
  }

  @Nonnull
  public static IntUnaryOperator minus(final int b) {
    return a -> a - b;
  }

  public static long minus(final long a, final long b) {
    return a - b;
  }

  @Nonnull
  public static LongUnaryOperator minus(final long b) {
    return a -> a - b;
  }

  public static double minus(final double a, final double b) {
    return a - b;
  }

  @Nonnull
  public static DoubleUnaryOperator minus(final double b) {
    return a -> a - b;
  }

  public static Integer minus(@Nullable final Integer a, @Nullable final Integer b) {
    if (a == null || b == null) {
      return null;
    }
    return a - b;
  }

  @Nonnull
  public static UnaryOperator<Integer> minus(@Nullable final Integer b) {
    return a -> minus(a, b);
  }

  public static Long minus(@Nullable final Long a, @Nullable final Long b) {
    if (a == null || b == null) {
      return null;
    }
    return a - b;
  }

  @Nonnull
  public static UnaryOperator<Long> minus(@Nullable final Long b) {
    return a -> minus(a, b);
  }

  public static Double minus(@Nullable final Double a, @Nullable final Double b) {
    if (a == null || b == null) {
      return null;
    }
    return a - b;
  }

  @Nonnull
  public static UnaryOperator<Double> minus(@Nullable final Double b) {
    return a -> minus(a, b);
  }

  public static BigInteger minus(@Nullable final BigInteger a, @Nullable final BigInteger b) {
    if (a == null || b == null) {
      return null;
    }
    return a.subtract(b);
  }

  @Nonnull
  public static UnaryOperator<BigInteger> minus(@Nullable final BigInteger b) {
    return a -> minus(a, b);
  }

  public static BigDecimal minus(@Nullable final BigDecimal a, @Nullable final BigDecimal b) {
    if (a == null || b == null) {
      return null;
    }
    return a.subtract(b);
  }

  @Nonnull
  public static UnaryOperator<BigDecimal> minus(@Nullable final BigDecimal b) {
    return a -> minus(a, b);
  }

  @SuppressWarnings("unchecked")
  public static <N extends Number> N minus(@Nullable final N a, @Nullable final N b) {
    if (a instanceof Integer && b instanceof Integer) {
      return (N) (Integer) (((Integer) a) - ((Integer) b));
    } else if (a instanceof Long && b instanceof Long) {
      return (N) (Long) (((Long) a) - ((Long) b));
    } else if (a instanceof Double && b instanceof Double) {
      return (N) (Double) (((Double) a) - ((Double) b));
    } else if (a instanceof BigInteger && b instanceof BigInteger) {
      return (N) ((BigInteger) a).subtract((BigInteger) b);
    } else if (a instanceof BigDecimal && b instanceof BigDecimal) {
      return (N) ((BigDecimal) a).subtract((BigDecimal) b);
    }
    return null;
  }

  @Nonnull
  public static <N extends Number> UnaryOperator<N> minus(@Nullable final N b) {
    return a -> minus(a, b);
  }

  public static int times(final int a, final int b) {
    return a * b;
  }

  @Nonnull
  public static IntUnaryOperator times(final int a) {
    return b -> a * b;
  }

  public static long times(final long a, final long b) {
    return a * b;
  }

  @Nonnull
  public static LongUnaryOperator times(final long a) {
    return b -> a * b;
  }

  public static double times(final double a, final double b) {
    return a * b;
  }

  @Nonnull
  public static DoubleUnaryOperator times(final double a) {
    return b -> a * b;
  }

  public static Integer times(final Integer a, final Integer b) {
    if (a == null || b == null) {
      return null;
    }
    return a * b;
  }

  @Nonnull
  public static UnaryOperator<Integer> times(@Nullable final Integer a) {
    return b -> times(a, b);
  }

  public static Long times(final Long a, final Long b) {
    if (a == null || b == null) {
      return null;
    }
    return a * b;
  }

  @Nonnull
  public static UnaryOperator<Long> times(@Nullable final Long a) {
    return b -> times(a, b);
  }

  public static Double times(final Double a, final Double b) {
    if (a == null || b == null) {
      return null;
    }
    return a * b;
  }

  @Nonnull
  public static UnaryOperator<Double> times(@Nullable final Double a) {
    return b -> times(a, b);
  }

  public static BigInteger times(final BigInteger a, final BigInteger b) {
    if (a == null || b == null) {
      return null;
    }
    return a.multiply(b);
  }

  @Nonnull
  public static UnaryOperator<BigInteger> times(@Nullable final BigInteger a) {
    return b -> times(a, b);
  }

  public static BigDecimal times(final BigDecimal a, final BigDecimal b) {
    if (a == null || b == null) {
      return null;
    }
    return a.multiply(b);
  }

  @Nonnull
  public static UnaryOperator<BigDecimal> times(@Nullable final BigDecimal a) {
    return b -> times(a, b);
  }

  @SuppressWarnings("unchecked")
  public static <N extends Number> N times(@Nullable final N a, @Nullable final N b) {
    if (a instanceof Integer && b instanceof Integer) {
      return (N) (Integer) (((Integer) a) * ((Integer) b));
    } else if (a instanceof Long && b instanceof Long) {
      return (N) (Long) (((Long) a) * ((Long) b));
    } else if (a instanceof Double && b instanceof Double) {
      return (N) (Double) (((Double) a) * ((Double) b));
    } else if (a instanceof BigInteger && b instanceof BigInteger) {
      return (N) ((BigInteger) a).multiply((BigInteger) b);
    } else if (a instanceof BigDecimal && b instanceof BigDecimal) {
      return (N) ((BigDecimal) a).multiply((BigDecimal) b);
    }
    return null;
  }

  @Nonnull
  public static <N extends Number> UnaryOperator<N> times(@Nullable final N a) {
    return b -> times(a, b);
  }

  public static int and(final int a, final int b) {
    return a & b;
  }

  @Nonnull
  public static IntUnaryOperator and(final int a) {
    return b -> a & b;
  }

  public static long and(final long a, final long b) {
    return a & b;
  }

  @Nonnull
  public static LongUnaryOperator and(final long a) {
    return b -> a & b;
  }

  public static Integer and(@Nullable final Integer a, @Nullable final Integer b) {
    if (a == null || b == null) {
      return null;
    }
    return a & b;
  }

  @Nonnull
  public static UnaryOperator<Integer> and(@Nullable final Integer a) {
    return b -> and(a, b);
  }

  public static Long and(@Nullable final Long a, @Nullable final Long b) {
    if (a == null || b == null) {
      return null;
    }
    return a & b;
  }

  @Nonnull
  public static UnaryOperator<Long> and(@Nullable final Long a) {
    return b -> and(a, b);
  }

  public static BigInteger and(@Nullable final BigInteger a, @Nullable final BigInteger b) {
    if (a == null || b == null) {
      return null;
    }
    return a.and(b);
  }

  @Nonnull
  public static UnaryOperator<BigInteger> and(@Nullable final BigInteger a) {
    return b -> and(a, b);
  }

  @SuppressWarnings("unchecked")
  public static <N extends Number> N and(@Nullable final N a, @Nullable final N b) {
    if (a instanceof Integer && b instanceof Integer) {
      return (N) (Integer) (((Integer) a) & ((Integer) b));
    } else if (a instanceof Long && b instanceof Long) {
      return (N) (Long) (((Long) a) & ((Long) b));
    } else if (a instanceof BigInteger && b instanceof BigInteger) {
      return (N) ((BigInteger) a).and((BigInteger) b);
    }
    return null;
  }

  @Nonnull
  public static <N extends Number> UnaryOperator<N> and(@Nullable final N a) {
    return b -> and(a, b);
  }

  public static int div(final int a, final int b) {
    return a / b;
  }

  @Nonnull
  public static IntUnaryOperator div(final int b) {
    return a -> a / b;
  }

  public static long div(final long a, final long b) {
    return a / b;
  }

  @Nonnull
  public static LongUnaryOperator div(final long b) {
    return a -> a / b;
  }

  public static double div(final double a, final double b) {
    return a / b;
  }

  @Nonnull
  public static DoubleUnaryOperator div(final double b) {
    return a -> a / b;
  }

  public static Integer div(@Nullable final Integer a, @Nullable final Integer b) {
    if (a == null || b == null) {
      return null;
    }
    return a / b;
  }

  @Nonnull
  public static UnaryOperator<Integer> div(@Nullable final Integer b) {
    return a -> div(a, b);
  }

  public static Long div(@Nullable final Long a, @Nullable final Long b) {
    if (a == null || b == null) {
      return null;
    }
    return a / b;
  }

  @Nonnull
  public static UnaryOperator<Long> div(@Nullable final Long b) {
    return a -> div(a, b);
  }

  public static Double div(@Nullable final Double a, @Nullable final Double b) {
    if (a == null || b == null) {
      return null;
    }
    return a / b;
  }

  @Nonnull
  public static UnaryOperator<Double> div(@Nullable final Double b) {
    return a -> div(a, b);
  }

  public static BigInteger div(@Nullable final BigInteger a, @Nullable final BigInteger b) {
    if (a == null || b == null) {
      return null;
    }
    return a.divide(b);
  }

  @Nonnull
  public static UnaryOperator<BigInteger> div(@Nullable final BigInteger b) {
    return a -> div(a, b);
  }

  public static BigDecimal div(@Nullable final BigDecimal a, @Nullable final BigDecimal b) {
    if (a == null || b == null) {
      return null;
    }
    return a.divide(b, RoundingMode.HALF_EVEN);
  }

  @Nonnull
  public static UnaryOperator<BigDecimal> div(@Nullable final BigDecimal b) {
    return a -> div(a, b);
  }

  @SuppressWarnings("unchecked")
  public static <N extends Number> N div(@Nullable final N a, @Nullable final N b) {
    if (a instanceof Integer && b instanceof Integer) {
      return (N) (Integer) (((Integer) a) / ((Integer) b));
    } else if (a instanceof Long && b instanceof Long) {
      return (N) (Long) (((Long) a) / ((Long) b));
    } else if (a instanceof Double && b instanceof Double) {
      return (N) (Double) (((Double) a) / ((Double) b));
    } else if (a instanceof BigInteger && b instanceof BigInteger) {
      return (N) ((BigInteger) a).divide((BigInteger) b);
    } else if (a instanceof BigDecimal && b instanceof BigDecimal) {
      return (N) ((BigDecimal) a).divide((BigDecimal) b, RoundingMode.HALF_EVEN);
    }
    return null;
  }

  @Nonnull
  public static <N extends Number> UnaryOperator<N> div(@Nullable final N b) {
    return a -> div(a, b);
  }

  public static int mod(final int a, final int b) {
    return a % b;
  }

  @Nonnull
  public static IntUnaryOperator mod(final int b) {
    return a -> a % b;
  }

  public static long mod(final long a, final long b) {
    return a % b;
  }

  @Nonnull
  public static LongUnaryOperator mod(final long b) {
    return a -> a % b;
  }

  public static double mod(final double a, final double b) {
    return a % b;
  }

  @Nonnull
  public static DoubleUnaryOperator mod(final double b) {
    return a -> a % b;
  }

  public static Integer mod(@Nullable final Integer a, @Nullable final Integer b) {
    if (a == null || b == null) {
      return null;
    }
    return a % b;
  }

  @Nonnull
  public static UnaryOperator<Integer> mod(@Nullable final Integer b) {
    return a -> mod(a, b);
  }

  public static Long mod(@Nullable final Long a, @Nullable final Long b) {
    if (a == null || b == null) {
      return null;
    }
    return a % b;
  }

  @Nonnull
  public static UnaryOperator<Long> mod(@Nullable final Long b) {
    return a -> mod(a, b);
  }

  public static Double mod(final Double a, final Double b) {
    if (a == null || b == null) {
      return null;
    }
    return a % b;
  }

  @Nonnull
  public static UnaryOperator<Double> mod(@Nullable final Double b) {
    return a -> mod(a, b);
  }

  public static BigInteger mod(final BigInteger a, final BigInteger b) {
    if (a == null || b == null) {
      return null;
    }
    return a.mod(b);
  }

  @Nonnull
  public static UnaryOperator<BigInteger> mod(@Nullable final BigInteger b) {
    return a -> mod(a, b);
  }

  @SuppressWarnings("unchecked")
  public static <N extends Number> N mod(@Nullable final N a, @Nullable final N b) {
    if (a instanceof Integer && b instanceof Integer) {
      return (N) (Integer) (((Integer) a) % ((Integer) b));
    } else if (a instanceof Long && b instanceof Long) {
      return (N) (Long) (((Long) a) % ((Long) b));
    } else if (a instanceof Double && b instanceof Double) {
      return (N) (Double) (((Double) a) % ((Double) b));
    } else if (a instanceof BigInteger && b instanceof BigInteger) {
      return (N) ((BigInteger) a).mod((BigInteger) b);
    }
    return null;
  }

  @Nonnull
  public static <N extends Number> UnaryOperator<N> mod(@Nullable final N b) {
    return a -> mod(a, b);
  }

  public static int xor(final int a, final int b) {
    return a ^ b;
  }

  @Nonnull
  public static IntUnaryOperator xor(final int b) {
    return a -> a ^ b;
  }

  public static long xor(final long a, final long b) {
    return a ^ b;
  }

  @Nonnull
  public static LongUnaryOperator xor(final long b) {
    return a -> a ^ b;
  }

  public static Integer xor(@Nullable final Integer a, @Nullable final Integer b) {
    if (a == null || b == null) {
      return null;
    }
    return a ^ b;
  }

  @Nonnull
  public static UnaryOperator<Integer> xor(@Nullable final Integer b) {
    return a -> xor(a, b);
  }

  public static Long xor(@Nullable final Long a, @Nullable final Long b) {
    if (a == null || b == null) {
      return null;
    }
    return a ^ b;
  }

  @Nonnull
  public static UnaryOperator<Long> xor(@Nullable final Long b) {
    return a -> xor(a, b);
  }

  public static BigInteger xor(@Nullable final BigInteger a, @Nullable final BigInteger b) {
    if (a == null || b == null) {
      return null;
    }
    return a.xor(b);
  }

  @Nonnull
  public static UnaryOperator<BigInteger> xor(@Nullable final BigInteger b) {
    return a -> xor(a, b);
  }

  @SuppressWarnings("unchecked")
  public static <N extends Number> N xor(@Nullable final N a, @Nullable final N b) {
    if (a instanceof Integer && b instanceof Integer) {
      return (N) (Integer) (((Integer) a) ^ ((Integer) b));
    } else if (a instanceof Long && b instanceof Long) {
      return (N) (Long) (((Long) a) ^ ((Long) b));
    } else if (a instanceof BigInteger && b instanceof BigInteger) {
      return (N) ((BigInteger) a).xor((BigInteger) b);
    }
    return null;
  }

  @Nonnull
  public static <N extends Number> UnaryOperator<N> xor(@Nullable final N b) {
    return a -> xor(a, b);
  }

  public static boolean neq(final boolean a, final boolean b) {
    return a != b;
  }

  public static boolean neq(final int a, final int b) {
    return a != b;
  }

  public static boolean neq(final long a, final long b) {
    return a != b;
  }

  public static boolean neq(final double a, final double b) {
    return a != b;
  }

  public static Boolean neq(@Nullable final Object a, @Nullable final Object b) {
    if (a == null || b == null) {
      return null;
    }
    return !a.equals(b);
  }

  public static boolean eq(final boolean a, final boolean b) {
    return a == b;
  }

  public static boolean eq(final int a, final int b) {
    return a == b;
  }

  public static boolean eq(final long a, final long b) {
    return a == b;
  }

  public static boolean eq(final double a, final double b) {
    return a == b;
  }

  public static Boolean eq(@Nullable final Object a, @Nullable final Object b) {
    if (a == null || b == null) {
      return null;
    }
    return a.equals(b);
  }

  public static boolean lt(final int a, final int b) {
    return a < b;
  }

  public static boolean lt(final long a, final long b) {
    return a < b;
  }

  public static boolean lt(final double a, final double b) {
    return a < b;
  }

  public static Boolean lt(final Integer a, final Integer b) {
    if (a == null || b == null) {
      return null;
    }
    return a < b;
  }

  public static Boolean lt(final Long a, final Long b) {
    if (a == null || b == null) {
      return null;
    }
    return a < b;
  }

  public static Boolean lt(final Double a, final Double b) {
    if (a == null || b == null) {
      return null;
    }
    return a < b;
  }

  public static <E extends Comparable<? super E>> Boolean lt(final E a, final E b) {
    if (a == null || b == null) {
      return null;
    }
    return a.compareTo(b) < 0;
  }

  public static boolean lte(final int a, final int b) {
    return a <= b;
  }

  public static boolean lte(final long a, final long b) {
    return a <= b;
  }

  public static boolean lte(final double a, final double b) {
    return a <= b;
  }

  public static Boolean lte(final Integer a, final Integer b) {
    if (a == null || b == null) {
      return null;
    }
    return a <= b;
  }

  public static Boolean lte(final Long a, final Long b) {
    if (a == null || b == null) {
      return null;
    }
    return a <= b;
  }

  public static Boolean lte(final Double a, final Double b) {
    if (a == null || b == null) {
      return null;
    }
    return a <= b;
  }

  public static <E extends Comparable<? super E>> Boolean lte(final E a, final E b) {
    if (a == null || b == null) {
      return null;
    }
    return a.compareTo(b) <= 0;
  }

  public static boolean gt(final int a, final int b) {
    return a > b;
  }

  public static boolean gt(final long a, final long b) {
    return a > b;
  }

  public static boolean gt(final double a, final double b) {
    return a > b;
  }

  public static Boolean gt(final Integer a, final Integer b) {
    if (a == null || b == null) {
      return null;
    }
    return a > b;
  }

  public static Boolean gt(final Long a, final Long b) {
    if (a == null || b == null) {
      return null;
    }
    return a > b;
  }

  public static Boolean gt(final Double a, final Double b) {
    if (a == null || b == null) {
      return null;
    }
    return a > b;
  }

  public static <E extends Comparable<? super E>> Boolean gt(final E a, final E b) {
    if (a == null || b == null) {
      return null;
    }
    return a.compareTo(b) > 0;
  }

  public static boolean gte(final int a, final int b) {
    return a >= b;
  }

  public static boolean gte(final long a, final long b) {
    return a >= b;
  }

  public static boolean gte(final double a, final double b) {
    return a >= b;
  }

  public static Boolean gte(final Integer a, final Integer b) {
    if (a == null || b == null) {
      return null;
    }
    return a >= b;
  }

  public static Boolean gte(final Long a, final Long b) {
    if (a == null || b == null) {
      return null;
    }
    return a >= b;
  }

  public static Boolean gte(final Double a, final Double b) {
    if (a == null || b == null) {
      return null;
    }
    return a >= b;
  }

  public static <E extends Comparable<? super E>> Boolean gte(final E a, final E b) {
    if (a == null || b == null) {
      return null;
    }
    return a.compareTo(b) >= 0;
  }
}
