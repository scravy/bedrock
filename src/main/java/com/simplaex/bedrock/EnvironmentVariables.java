package com.simplaex.bedrock;

import lombok.experimental.UtilityClass;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@UtilityClass
public class EnvironmentVariables {

  // visible + non-final for testing
  Function<String, String> ENVIRONMENT_VARIABLE_RETRIEVER = System::getenv;

  public static Optional<String> getenv(final String key) {
    Objects.requireNonNull(key);
    return Optional.ofNullable(ENVIRONMENT_VARIABLE_RETRIEVER.apply(key));
  }

  public static <T> T read(@Nonnull final Class<T> clazz) {
    try {
      final T instance = clazz.newInstance();
      readInto(instance);
      return instance;
    } catch (final Exception exc) {
      throw new RuntimeException(exc);
    }
  }

  public static <T> void readInto(@Nonnull final T instance) {
    Objects.requireNonNull(instance);
    @SuppressWarnings("unchecked") final Set<Reflections.Property<T>> properties =
      Reflections.getProperties((Class<T>) instance.getClass());
    properties.forEach(property -> {
      if (property.getSetter() == null) {
        return;
      }
      final String environmentVariableName = Strings.toUpperSnakeCase(property.getName());
      getenv(environmentVariableName).ifPresent(value -> property.set(instance, value));
    });
  }

}
