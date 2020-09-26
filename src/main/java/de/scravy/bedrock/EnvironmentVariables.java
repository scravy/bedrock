package de.scravy.bedrock;

import lombok.experimental.UtilityClass;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@UtilityClass
public class EnvironmentVariables {

  private Function<String, String> defaultEnvironmentVariableRetriever = System::getenv;

  @SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
  public static Function<String, String> setDefaultEnvironmentVariableRetriever(@Nonnull final Function<String, String> retriever) {
    Objects.requireNonNull(retriever, "'retriever' must not be null");
    final Function<String, String> oldRetriever = defaultEnvironmentVariableRetriever;
    defaultEnvironmentVariableRetriever = retriever;
    return oldRetriever;
  }

  @Nonnull
  public static Optional<String> getenv(@Nullable final Function<String, String> retriever, @Nonnull final String key) {
    Objects.requireNonNull(key, "'key' must not be null");
    return Optional.ofNullable(Optional.ofNullable(retriever).orElse(defaultEnvironmentVariableRetriever).apply(key));
  }

  public static <T> T read(@Nonnull final Class<T> clazz) {
    return read(null, clazz);
  }

  public static <T> T read(@Nullable final Function<String, String> retriever, @Nonnull final Class<T> clazz) {
    try {
      final T instance = clazz.newInstance();
      readInto(retriever, instance);
      return instance;
    } catch (final Exception exc) {
      throw new RuntimeException(exc);
    }
  }

  public static <T> void readInto(@Nonnull final T instance) {
    readInto(null, instance);
  }

  public static <T> void readInto(@Nullable final Function<String, String> retriever, @Nonnull final T instance) {
    Objects.requireNonNull(instance);
    @SuppressWarnings("unchecked") final Set<Reflections.Property<T>> properties =
      Reflections.getProperties((Class<T>) instance.getClass());
    properties.forEach(property -> {
      if (property.getSetter() == null) {
        return;
      }
      final String environmentVariableName = Strings.toUpperSnakeCase(property.getName());
      getenv(retriever, environmentVariableName).ifPresent(value -> property.set(instance, value));
    });
  }

}
