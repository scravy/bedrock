package com.simplaex.bedrock;

import lombok.experimental.UtilityClass;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@UtilityClass
public class Conversions {

  public static <From, To> To fromTo(
    @Nonnull final Class<From> from,
    @Nonnull final Class<To> to,
    @Nullable final From value
  ) {
    if (value == null) {
      return null;
    }
    if (to.isAssignableFrom(value.getClass())) {
      @SuppressWarnings("unchecked") final To toCasted = (To) to;
      return toCasted;
    }
    final Optional<To> attempt1 = Reflections.getFactory(from, to).flatMap(factory ->
      Try.execute(() -> factory.apply(value)).filter(Objects::nonNull).toOptional()
    );
    if (attempt1.isPresent()) {
      return attempt1.get();
    }
    final String stringValue = value.toString();
    final Optional<To> attempt2 = Reflections.getFactory(String.class, to).flatMap(factory ->
      Try.execute(() -> factory.apply(stringValue)).filter(Objects::nonNull).toOptional()
    );
    return attempt2.orElse(null);
  }

  @Nonnull
  public static <From, To> Function<From, To> fromTo(
    @Nonnull final Class<From> from,
    @Nonnull final Class<To> to
  ) {
    return value -> fromTo(from, to, value);
  }

}
