package com.simplaex.bedrock;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.UtilityClass;

import javax.annotation.Nonnull;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.Callable;

import static com.simplaex.bedrock.ForEach.forEach;

@SuppressWarnings("RedundantCast")
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

  @Nonnull
  public static <S, T> Optional<ThrowingFunction<S, T>> getFactory(
    @Nonnull final Class<S> from,
    @Nonnull final Class<T> clazz
  ) {
    Objects.requireNonNull(from, "'from' must not be null");
    Objects.requireNonNull(clazz, "'clazz' must not be null");
    {
      final Optional<ThrowingFunction<S, T>> factory = getFactoryConstructor(from, clazz);
      if (factory.isPresent()) {
        return factory;
      }
    }
    {
      final Optional<ThrowingFunction<S, T>> factory = getFactoryMethod(from, clazz);
      if (factory.isPresent()) {
        return factory;
      }
    }
    final Class<?> boxedClass = getBoxedClassFor(clazz);
    if (boxedClass != clazz) {
      final Optional<? extends ThrowingFunction<S, ?>> factory = getFactory(from, boxedClass);
      @SuppressWarnings("unchecked") final Optional<ThrowingFunction<S, T>> theFactory =
        (Optional<ThrowingFunction<S, T>>) factory;
      return theFactory;
    }
    return Optional.empty();
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
    return boxedToPrimitiveClassesMap.getOrDefault(boxedClass, boxedClass);
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
    return primitiveToBoxedClassesMap.getOrDefault(primitiveClass, primitiveClass);
  }

  public static Seq<Class<?>> getParents(final Class<?> clazz) {
    Class<?> current = clazz;
    final SeqBuilder<Class<?>> seqBuilder = Seq.builder();
    do {
      seqBuilder.add(current);
      current = current.getSuperclass();
    } while (!Object.class.equals(current) && current != null);
    if (current != null) {
      seqBuilder.add(current);
    }
    return seqBuilder.result().reversed();
  }

  @Nonnull
  public static Optional<Class<?>> getCommonBaseClass(
    @Nonnull final Class<?> oneClass,
    @Nonnull final Class<?> anotherClass
  ) {
    Objects.requireNonNull(oneClass);
    Objects.requireNonNull(anotherClass);
    final Seq<Class<?>> commonAncestors = Seq.commonPrefixView(getParents(oneClass), getParents(anotherClass));
    return commonAncestors.lastOptional();
  }

  @Value
  @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
  public static class Property<T> implements Comparable<Property> {

    final String name;

    final Class<?> type;

    final Method getter;

    final Method setter;

    public Object get(final T instance) {
      try {
        return getter.invoke(instance);
      } catch (final Exception exc) {
        throw new RuntimeException("could not invoke getter for " + name + " on " + instance, exc);
      }
    }

    @SuppressWarnings("unchecked")
    public void set(final T instance, final Object value) {
      if (setter == null) {
        return;
      }
      try {
        if (value == null) {
          setter.invoke(instance, (Object) null);
        } else if (type.isAssignableFrom(value.getClass())) {
          setter.invoke(instance, value);
        } else {
          final Optional<ThrowingFunction<Object, Object>> maybeFactory =
            Reflections.getFactory((Class<Object>) value.getClass(), (Class<Object>) type);
          final Object finalValue = Try
            .fromOptional(maybeFactory)
            .map(f -> f.apply(value))
            .orElseThrowRuntime();
          setter.invoke(instance, finalValue);
        }
      } catch (final Exception exc) {
        throw new RuntimeException("could not invoke setter for " + name + " and " + value + " on " + instance, exc);
      }
    }

    @Override
    public int compareTo(final Property property) {
      return name.compareTo(property.name);
    }
  }

  @Nonnull
  public static <T> Set<Property<T>> getProperties(@Nonnull final Class<T> clazz) {
    Objects.requireNonNull(clazz);
    final SetBuilder<Property<T>> builder = Set.builder();
    try {
      forEach(Introspector.getBeanInfo(clazz).getPropertyDescriptors(), propertyDescriptor -> {
        builder.add(new Property<>(
          propertyDescriptor.getName(),
          propertyDescriptor.getPropertyType(),
          propertyDescriptor.getReadMethod(),
          propertyDescriptor.getWriteMethod()
        ));
      });
    } catch (final IntrospectionException exc) {
      throw new RuntimeException("could not get property descriptors using BeanInfo Introspector", exc);
    }
    return builder.result();
  }
}
