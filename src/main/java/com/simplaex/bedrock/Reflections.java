package com.simplaex.bedrock;

import lombok.experimental.UtilityClass;

import javax.annotation.Nonnull;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.Callable;

@SuppressWarnings("WeakerAccess")
@UtilityClass
public class Reflections {

  @SuppressWarnings("unchecked")
  @Nonnull
  public static <S, T> Optional<ThrowingFunction<S, T>> getFactoryConstructor(
    @Nonnull final Class<S> from,
    @Nonnull final Class<T> clazz
  ) {
    Objects.requireNonNull(from, "'from' must not be null");
    Objects.requireNonNull(clazz, "'clazz' must not be null");
    return Arrays.stream(clazz.getConstructors())
      .filter(constructor -> Modifier.isPublic(constructor.getModifiers()))
      .filter(constructor -> constructor.getParameterCount() == 1)
      .filter(constructor -> constructor.getParameterTypes()[0].isAssignableFrom(from))
      .findFirst()
      .map(constructor -> (ThrowingFunction<S, T>) (s -> (T) constructor.newInstance(s)));
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  public static <S, T> Optional<ThrowingFunction<S, T>> getFactoryMethod(
    @Nonnull final Class<S> from,
    @Nonnull final Class<T> clazz
  ) {
    Objects.requireNonNull(from, "'from' must not be null");
    Objects.requireNonNull(clazz, "'clazz' must not be null");
    return Arrays.stream(clazz.getMethods())
      .filter(method -> Modifier.isStatic(method.getModifiers()))
      .filter(method -> Modifier.isPublic(method.getModifiers()))
      .filter(method -> method.getParameterCount() == 1)
      .filter(method -> method.getReturnType().equals(clazz))
      .filter(method -> method.getParameterTypes()[0].isAssignableFrom(from))
      .findFirst()
      .map(method -> (ThrowingFunction<S, T>) (s -> (T) method.invoke(null, s)));
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  public static <S, T> Optional<ThrowingFunction<S, T>> getFactory(
    @Nonnull final Class<S> from,
    @Nonnull final Class<T> clazz
  ) {
    Objects.requireNonNull(from, "'from' must not be null");
    Objects.requireNonNull(clazz, "'clazz' must not be null");
    final Optional<ThrowingFunction<S, T>> factory = getFactoryConstructor(from, clazz);
    if (factory.isPresent()) {
      return factory;
    }
    return getFactoryMethod(from, clazz);
  }

  @SuppressWarnings("unchecked")
  @Nonnull
  public static <T> Optional<Callable<T>> getFactory(
    @Nonnull final Class<T> clazz
  ) {
    Objects.requireNonNull(clazz, "'clazz' must not be null");
    return Arrays.stream(clazz.getConstructors())
      .filter(constructor -> Modifier.isPublic(constructor.getModifiers()))
      .filter(constructor -> constructor.getParameterCount() == 0)
      .findFirst()
      .map(constructor -> (Callable<T>) (() -> (T) constructor.newInstance()));
  }

  @SuppressWarnings("unchecked")
  public static <T> T proxy(
    @Nonnull final Class<T> clazz,
    @Nonnull final ThrowingBiFunction<String, Seq<Object>, Object> handler
  ) {
    Objects.requireNonNull(clazz, "'clazz' must not be null");
    Objects.requireNonNull(handler, "'handler' must not be null");
    return (T) Proxy.newProxyInstance(
      Thread.currentThread().getContextClassLoader(),
      new Class[]{clazz},
      (proxy, method, args) -> handler.apply(method.getName(), new SeqSimple<>(args))
    );
  }

  private static final Map<Class<?>, Class<?>> boxedToPrimitiveClassesMap = new HashMap<>();

  static {
    boxedToPrimitiveClassesMap.put(Void.class, void.class);
    boxedToPrimitiveClassesMap.put(Boolean.class, boolean.class);
    boxedToPrimitiveClassesMap.put(Character.class, char.class);
    boxedToPrimitiveClassesMap.put(Byte.class, byte.class);
    boxedToPrimitiveClassesMap.put(Short.class, short.class);
    boxedToPrimitiveClassesMap.put(Integer.class, int.class);
    boxedToPrimitiveClassesMap.put(Long.class, long.class);
    boxedToPrimitiveClassesMap.put(Float.class, float.class);
    boxedToPrimitiveClassesMap.put(Double.class, double.class);
  }

  public static Class<?> getPrimitiveClassFor(final Class<?> boxedClass) {
    return boxedToPrimitiveClassesMap.get(boxedClass);
  }

  private static final Map<Class<?>, Class<?>> primitiveToBoxedClassesMap = new HashMap<>();

  static {
    primitiveToBoxedClassesMap.put(void.class, Void.class);
    primitiveToBoxedClassesMap.put(boolean.class, Boolean.class);
    primitiveToBoxedClassesMap.put(char.class, Character.class);
    primitiveToBoxedClassesMap.put(byte.class, Byte.class);
    primitiveToBoxedClassesMap.put(short.class, Short.class);
    primitiveToBoxedClassesMap.put(int.class, Integer.class);
    primitiveToBoxedClassesMap.put(long.class, Long.class);
    primitiveToBoxedClassesMap.put(float.class, Float.class);
    primitiveToBoxedClassesMap.put(double.class, Double.class);
  }

  public static Class<?> getBoxedClassFor(final Class<?> primitiveClass) {
    return primitiveToBoxedClassesMap.get(primitiveClass);
  }

}
