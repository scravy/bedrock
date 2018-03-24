package com.simplaex.bedrock;

import lombok.experimental.UtilityClass;
import lombok.val;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("WeakerAccess")
@UtilityClass
public class UUIDs {

  public static final UUID NAMESPACE_DNS = UUID.fromString("6ba7b810-9dad-11d1-80b4-00c04fd430c8");
  public static final UUID NAMESPACE_URL = UUID.fromString("6ba7b811-9dad-11d1-80b4-00c04fd430c8");
  public static final UUID NAMESPACE_OID = UUID.fromString("6ba7b812-9dad-11d1-80b4-00c04fd430c8");
  public static final UUID NAMESPACE_X500 = UUID.fromString("6ba7b814-9dad-11d1-80b4-00c04fd430c8");

  private byte[] namespacedBytes(final UUID namespace, final String name) {
    val namespaceBytes = toBytes(namespace);
    val nameBytes = name.getBytes(StandardCharsets.UTF_8);
    val buffer = ByteBuffer.wrap(new byte[namespaceBytes.length + nameBytes.length]);
    buffer.put(namespaceBytes);
    buffer.put(nameBytes);
    return buffer.array();
  }

  public static UUID v3(final UUID namespace, final String name) {
    return UUID.nameUUIDFromBytes(namespacedBytes(namespace, name));
  }

  public static UUID v4() {
    return UUID.randomUUID();
  }

  public static UUID v5(final UUID namespace, final String name) {
    val bytes = namespacedBytes(namespace, name);
    final MessageDigest md;
    try {
      md = MessageDigest.getInstance("SHA1");
    } catch (final NoSuchAlgorithmException exc) {
      throw new RuntimeException(exc);
    }
    final byte[] resultBytes = md.digest(bytes);
    resultBytes[6] &= 0x0f;
    resultBytes[6] |= 0x50;
    resultBytes[8] &= 0x3f;
    resultBytes[8] |= 0x80;
    return fromBytes(resultBytes);
  }

  public static byte[] toBytes(final UUID uuid) {
    Objects.requireNonNull(uuid);
    val buffer = ByteBuffer.wrap(new byte[16]);
    buffer.putLong(uuid.getMostSignificantBits());
    buffer.putLong(uuid.getLeastSignificantBits());
    return buffer.array();
  }

  public static UUID fromBytes(final byte[] bytes) {
    Objects.requireNonNull(bytes);
    val buffer = ByteBuffer.wrap(bytes);
    val mostSignificantBits = buffer.getLong();
    val leastSignificantBits = buffer.getLong();
    return new UUID(mostSignificantBits, leastSignificantBits);
  }

}
