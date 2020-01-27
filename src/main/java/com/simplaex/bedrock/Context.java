package com.simplaex.bedrock;

import lombok.Value;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Optional;

@Value
@Unstable
public class Context {

  private final static ThreadLocal<Context> currentcontext = ThreadLocal.withInitial(() -> null);

  private final Context parent;
  private final ArrayMap<String, Object> bindings;

  public static Object get(final String id) {
    return getInstance().apply(id);
  }

  @Nonnull
  public static <T> Optional<T> getOptionally(@Nonnull final String id, @Nonnull final Class<T> clazz) {
    Objects.requireNonNull(id);
    Objects.requireNonNull(clazz);
    final Object obj = get(id);
    if (obj == null) {
      return Optional.empty();
    }
    if (!clazz.isAssignableFrom(obj.getClass())) {
      return Optional.empty();
    }
    @SuppressWarnings("unchecked") final T result = (T) obj;
    return Optional.of(result);
  }

  @Nonnull
  public static <T> T get(@Nonnull final String id, @Nonnull final Class<T> clazz) {
    Objects.requireNonNull(id);
    Objects.requireNonNull(clazz);
    final Object obj = get(id);
    if (obj == null) {
      throw new IllegalStateException("not in context: " + id);
    }
    if (!clazz.isAssignableFrom(obj.getClass())) {
      throw new IllegalArgumentException("context object is of incompatible type to the one requested.");
    }
    @SuppressWarnings("unchecked") final T result = (T) obj;
    return result;
  }

  private Object apply(@Nonnull final String id) {
    Context current = this;
    do {
      final Optional<Object> obj = current.bindings.get(id);
      if (obj.isPresent()) {
        return obj.get();
      }
      current = current.parent;
    } while (current != null);
    return null;
  }

  public static void withContext(final ArrayMap<String, Object> mappings, final ThrowingRunnable f) {
    final Context context = currentcontext.get();
    currentcontext.set(new Context(context, mappings));
    try {
      f.run();
    } finally {
      currentcontext.set(context);
    }
  }

  public static Context getInstance() {
    final Context context = currentcontext.get();
    if (context == null) {
      throw new IllegalStateException("no context set currently");
    }
    return currentcontext.get();
  }
}
