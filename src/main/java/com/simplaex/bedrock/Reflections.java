package com.simplaex.bedrock;

import lombok.experimental.UtilityClass;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.Callable;

@UtilityClass
public class Reflections {

  @SuppressWarnings("unchecked")
  @Nonnull
  public static <S, T> Optional<ThrowingFunction<S, T>> getFactoryConstructor(
    @Nonnull final Class<S> from,
    @Nonnull final Class<T> clazz
  ) {
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
    return Arrays.stream(clazz.getConstructors())
      .filter(constructor -> Modifier.isPublic(constructor.getModifiers()))
      .filter(constructor -> constructor.getParameterCount() == 0)
      .findFirst()
      .map(constructor -> (Callable<T>) (() -> (T) constructor.newInstance()));
  }

  @SuppressWarnings("unchecked")
  public static <T> T proxy(final Class<T> clazz, final ThrowingBiFunction<String, Seq<Object>, Object> handler) {
    return (T) Proxy.newProxyInstance(
      Thread.currentThread().getContextClassLoader(),
      new Class[]{clazz},
      (proxy, method, args) -> handler.apply(method.getName(), new SeqSimple<>(args))
    );
  }

}
