package com.simplaex.bedrock;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

@SuppressWarnings({"unused", "RedundantCast"})
@UtilityClass
public class Operators {

  public static int plus(final int a, final int b) {
    return a + b;
  }

  public static long plus(final long a, final long b) {
    return a + b;
  }

  public static double plus(final double a, final double b) {
    return a + b;
  }

  public static Integer plus(final Integer a, final Integer b) {
    if (a == null || b == null) {
      return null;
    }
    return a + b;
  }

  public static Long plus(final Long a, final Long b) {
    if (a == null || b == null) {
      return null;
    }
    return a + b;
  }

  public static Double plus(final Double a, final Double b) {
    if (a == null || b == null) {
      return null;
    }
    return a + b;
  }

  @SuppressWarnings("unchecked")
  public static <N extends Number> N plus(final N a, final N b) {
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

  public static int or(final int a, final int b) {
    return a | b;
  }

  public static long or(final long a, final long b) {
    return a | b;
  }

  public static Integer or(final Integer a, final Integer b) {
    if (a == null || b == null) {
      return null;
    }
    return a | b;
  }

  public static Long or(final Long a, final Long b) {
    if (a == null || b == null) {
      return null;
    }
    return a | b;
  }

  @SuppressWarnings("unchecked")
  public static <N extends Number> N or(final N a, final N b) {
    if (a instanceof Integer && b instanceof Integer) {
      return (N) (Integer) (((Integer) a) | ((Integer) b));
    } else if (a instanceof Long && b instanceof Long) {
      return (N) (Long) (((Long) a) | ((Long) b));
    } else if (a instanceof BigInteger && b instanceof BigInteger) {
      return (N) ((BigInteger) a).or((BigInteger) b);
    }
    return null;
  }

  public static int minus(final int a, final int b) {
    return a - b;
  }

  public static long minus(final long a, final long b) {
    return a - b;
  }

  public static double minus(final double a, final double b) {
    return a - b;
  }

  public static Integer minus(final Integer a, final Integer b) {
    if (a == null || b == null) {
      return null;
    }
    return a - b;
  }

  public static Long minus(final Long a, final Long b) {
    if (a == null || b == null) {
      return null;
    }
    return a - b;
  }

  public static Double minus(final Double a, final Double b) {
    if (a == null || b == null) {
      return null;
    }
    return a - b;
  }

  @SuppressWarnings("unchecked")
  public static <N extends Number> N minus(final N a, final N b) {
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

  public static int times(final int a, final int b) {
    return a * b;
  }

  public static long times(final long a, final long b) {
    return a * b;
  }

  public static double times(final double a, final double b) {
    return a * b;
  }

  public static Integer times(final Integer a, final Integer b) {
    if (a == null || b == null) {
      return null;
    }
    return a * b;
  }

  public static Long times(final Long a, final Long b) {
    if (a == null || b == null) {
      return null;
    }
    return a * b;
  }

  public static Double times(final Double a, final Double b) {
    if (a == null || b == null) {
      return null;
    }
    return a * b;
  }

  @SuppressWarnings("unchecked")
  public static <N extends Number> N times(final N a, final N b) {
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

  public static int and(final int a, final int b) {
    return a & b;
  }

  public static long and(final long a, final long b) {
    return a & b;
  }

  public static Integer and(final Integer a, final Integer b) {
    if (a == null || b == null) {
      return null;
    }
    return a & b;
  }

  public static Long and(final Long a, final Long b) {
    if (a == null || b == null) {
      return null;
    }
    return a & b;
  }

  @SuppressWarnings("unchecked")
  public static <N extends Number> N and(final N a, final N b) {
    if (a instanceof Integer && b instanceof Integer) {
      return (N) (Integer) (((Integer) a) & ((Integer) b));
    } else if (a instanceof Long && b instanceof Long) {
      return (N) (Long) (((Long) a) & ((Long) b));
    } else if (a instanceof BigInteger && b instanceof BigInteger) {
      return (N) ((BigInteger) a).and((BigInteger) b);
    }
    return null;
  }

  public static int div(final int a, final int b) {
    return a / b;
  }

  public static long div(final long a, final long b) {
    return a / b;
  }

  public static double div(final double a, final double b) {
    return a / b;
  }

  public static Integer div(final Integer a, final Integer b) {
    if (a == null || b == null) {
      return null;
    }
    return a / b;
  }

  public static Long div(final Long a, final Long b) {
    if (a == null || b == null) {
      return null;
    }
    return a / b;
  }

  public static Double div(final Double a, final Double b) {
    if (a == null || b == null) {
      return null;
    }
    return a / b;
  }

  @SuppressWarnings("unchecked")
  public static <N extends Number> N div(final N a, final N b) {
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

  public static int mod(final int a, final int b) {
    return a % b;
  }

  public static long mod(final long a, final long b) {
    return a % b;
  }

  public static double mod(final double a, final double b) {
    return a % b;
  }

  public static Integer mod(final Integer a, final Integer b) {
    if (a == null || b == null) {
      return null;
    }
    return a % b;
  }

  public static Long mod(final Long a, final Long b) {
    if (a == null || b == null) {
      return null;
    }
    return a % b;
  }

  public static Double mod(final Double a, final Double b) {
    if (a == null || b == null) {
      return null;
    }
    return a % b;
  }

  @SuppressWarnings("unchecked")
  public static <N extends Number> N mod(final N a, final N b) {
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

  public static int xor(final int a, final int b) {
    return a ^ b;
  }

  public static long xor(final long a, final long b) {
    return a ^ b;
  }

  public static Integer xor(final Integer a, final Integer b) {
    if (a == null || b == null) {
      return null;
    }
    return a ^ b;
  }

  public static Long xor(final Long a, final Long b) {
    if (a == null || b == null) {
      return null;
    }
    return a ^ b;
  }

  @SuppressWarnings("unchecked")
  public static <N extends Number> N xor(final N a, final N b) {
    if (a instanceof Integer && b instanceof Integer) {
      return (N) (Integer) (((Integer) a) ^ ((Integer) b));
    } else if (a instanceof Long && b instanceof Long) {
      return (N) (Long) (((Long) a) ^ ((Long) b));
    } else if (a instanceof BigInteger && b instanceof BigInteger) {
      return (N) ((BigInteger) a).xor((BigInteger) b);
    }
    return null;
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

  public static Boolean neq(final Object a, final Object b) {
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

  public static Boolean eq(final Object a, final Object b) {
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
