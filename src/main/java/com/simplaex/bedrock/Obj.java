package com.simplaex.bedrock;

import lombok.experimental.UtilityClass;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@UtilityClass
public class Obj {

  @Nonnull
  public static <T> Class<T> getClass(@Nonnull final T obj) {
    @SuppressWarnings("unchecked") final Class<T> clazz = (Class<T>) obj.getClass();
    return clazz;
  }

  public static Sha256 sha256(@Nullable final Object obj) {
    return Sha256.singleHash().hashObject(obj);
  }

  public static Sha512 sha512(@Nullable final Object obj) {
    return Sha512.singleHash().hashObject(obj);
  }

  public static Sha256 sha256double(@Nullable final Object obj) {
    return Sha256.doubleHash().hashObject(obj);
  }

  public static Sha512 sha512double(@Nullable final Object obj) {
    return Sha512.doubleHash().hashObject(obj);
  }
}
