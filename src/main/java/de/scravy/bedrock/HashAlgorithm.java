package de.scravy.bedrock;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.Reference;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Function;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class HashAlgorithm<H extends Hash<H>> {

  private final int length;

  @Nonnull
  private final String algorithm;

  @Nonnull
  private final Function<byte[], H> constructor;

  @Nonnull
  private final Function<MessageDigest, byte[]> finisher;

  @Setter
  private H merkleNodeMarker = null;

  public H empty() {
    return constructor.apply(finisher.apply(getMessageDigest()));
  }

  public H zeroed() {
    return constructor.apply(new byte[length]);
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public class Builder {
    private final MessageDigest md = getMessageDigest();

    @Nonnull
    final Builder addHash(@Nonnull final H hash) {
      return addBytes(hash.getBytes());
    }

    @Nonnull
    final Builder addBytes(@Nonnull final byte[] bytes) {
      md.update(bytes);
      return this;
    }

    @Nonnull
    public final H build() {
      return constructor.apply(md.digest());
    }
  }

  public Builder builder() {
    return new Builder();
  }

  @Nonnull
  private MessageDigest getMessageDigest() {
    try {
      return MessageDigest.getInstance(algorithm);
    } catch (final NoSuchAlgorithmException exc) {
      throw new AssertionError("Your JVM/JRE does not support " + algorithm, exc);
    }
  }

  @Nonnull
  public H hashString(@Nonnull final String string) {
    Objects.requireNonNull(string, "'string' must not be null");
    final byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
    return hashBytes(bytes);
  }

  @Nonnull
  public H hashBytes(@Nonnull final byte[] bytes) {
    Objects.requireNonNull(bytes, "'bytes' must not be null");
    final MessageDigest md = getMessageDigest();
    final byte[] digest = md.digest(bytes);
    return constructor.apply(digest);
  }

  @Nonnull
  public H hashRawByte(final byte value) {
    return hashBytes(new byte[]{value});
  }

  @Nonnull
  public H hashRawShort(final short number) {
    return hashBytes(ByteBuffer.wrap(new byte[2]).putShort(number).array());
  }

  @Nonnull
  public H hashRawInt(final int number) {
    return hashBytes(ByteBuffer.wrap(new byte[4]).putInt(number).array());
  }

  @Nonnull
  public H hashRawLong(final long number) {
    return hashBytes(ByteBuffer.wrap(new byte[8]).putLong(number).array());
  }

  @Nonnull
  public H hashRawFloat(final float number) {
    return hashBytes(ByteBuffer.wrap(new byte[4]).putFloat(number).array());
  }

  @Nonnull
  public H hashRawDouble(final double number) {
    return hashBytes(ByteBuffer.wrap(new byte[8]).putDouble(number).array());
  }

  @Nonnull
  public H hashRawChar(final char character) {
    return hashBytes(ByteBuffer.wrap(new byte[2]).putChar(character).array());
  }

  @Nonnull
  public H hashByte(final byte value) {
    return hashBigDecimal(BigDecimal.valueOf(value));
  }

  @Nonnull
  public H hashShort(final short value) {
    return hashBigDecimal(BigDecimal.valueOf(value));
  }

  @Nonnull
  public H hashInt(final int value) {
    return hashBigDecimal(BigDecimal.valueOf(value));
  }

  @Nonnull
  public H hashLong(final long value) {
    return hashBigDecimal(BigDecimal.valueOf(value));
  }

  @Nonnull
  public H hashChar(final char value) {
    return hashBigDecimal(BigDecimal.valueOf(value));
  }

  @Nonnull
  public H hashFloat(final float value) {
    if (value == Float.POSITIVE_INFINITY) {
      return hashString("+Inf");
    } else if (value == Float.NEGATIVE_INFINITY) {
      return hashString("-Inf");
    } else if (Float.isNaN(value)) {
      return hashString("NaN");
    }
    return hashBigDecimal(BigDecimal.valueOf(value));
  }

  @Nonnull
  public H hashDouble(final double value) {
    if (value == Double.POSITIVE_INFINITY) {
      return hashString("+Inf");
    } else if (value == Double.NEGATIVE_INFINITY) {
      return hashString("-Inf");
    } else if (Double.isNaN(value)) {
      return hashString("NaN");
    }
    return hashBigDecimal(BigDecimal.valueOf(value));
  }

  @Nonnull
  public H hashBigDecimal(@Nullable final BigDecimal value) {
    if (value == null) {
      return zeroed();
    }
    return hashString(value.toPlainString().replaceAll("\\.0$", ""));
  }

  @Nonnull
  public H hashBigInteger(@Nullable final BigInteger value) {
    if (value == null) {
      return zeroed();
    }
    return hashBigDecimal(new BigDecimal(value));
  }

  @Nonnull
  public H hashSequence(@Nullable final Iterable<?> seq) {
    if (seq == null) {
      return zeroed();
    }
    final List<H> hashes = seq instanceof Collection ? new ArrayList<>(((Collection<?>) seq).size()) : new ArrayList<>();
    seq.forEach(obj -> hashes.add(hashObject(obj)));
    return merkleTree(hashes);
  }

  @Nonnull
  public H hashUnorderedCollection(@Nullable final Iterable<?> seq) {
    if (seq == null) {
      return zeroed();
    }
    final List<H> hashes = new ArrayList<>();
    seq.forEach(obj -> hashes.add(hashObject(obj)));
    Collections.sort(hashes);
    return merkleTree(hashes);
  }

  @Nonnull
  public H hashTreeMap(@Nullable final TreeMap<?, ?> ts) {
    if (ts == null) {
      return zeroed();
    }
    final List<H> hashes = new ArrayList<>(ts.size());
    ts.forEach((keyHash, valueHash) -> hashes.add(hash(keyHash, valueHash)));
    return merkleTree(hashes);
  }

  @Nonnull
  public H hashMap(@Nullable final Mapping<?, ?> map) {
    if (map == null) {
      return zeroed();
    }
    final TreeMap<H, H> ts = new TreeMap<>();
    map.forEach((key, obj) -> ts.put(hashObject(key), hashObject(obj)));
    return hashTreeMap(ts);
  }

  @Nonnull
  public <T> H hashPojo(@Nullable final T obj) {
    if (obj == null) {
      return zeroed();
    }
    final TreeMap<H, H> ts = new TreeMap<>();
    Reflections.getProperties(Obj.getClass(obj)).forEach(property -> {
      if (!property.getName().equals("class")) {
        final String key = property.getName();
        final H keyHash = hashString(key);
        final H valueHash = hashObject(property.get(obj));
        ts.put(keyHash, valueHash);
      }
    });
    return hashTreeMap(ts);
  }

  public H hash(final Object... objects) {
    final List<H> hashes = new ArrayList<>(objects.length);
    Arrays.stream(objects).forEach(obj -> hashes.add(hashObject(obj)));
    return merkleTree(hashes);
  }

  private boolean isUnorderedCollection(final Object obj) {
    return (obj instanceof java.util.Set && !(obj instanceof LinkedHashSet))
      || obj instanceof de.scravy.bedrock.Set;
  }

  @Nonnull
  public H hashObject(final Object obj) {
    if (obj == null) {
      return zeroed();
    } else if (obj instanceof String) {
      return hashString((String) obj);
    } else if (obj instanceof Object[]) {
      return hash((Object[]) obj);
    } else if (obj instanceof Hash) {
      return hashBytes(((Hash<?>) obj).getBytes());
    } else if (obj instanceof byte[]) {
      return hashBytes((byte[]) obj);
    } else if (obj instanceof Byte) {
      return hashByte((Byte) obj);
    } else if (obj instanceof Short) {
      return hashShort((Short) obj);
    } else if (obj instanceof Integer) {
      return hashInt((Integer) obj);
    } else if (obj instanceof Long) {
      return hashLong((Long) obj);
    } else if (obj instanceof Float) {
      return hashFloat((Float) obj);
    } else if (obj instanceof Double) {
      return hashDouble((Double) obj);
    } else if (obj instanceof BigDecimal) {
      return hashBigDecimal((BigDecimal) obj);
    } else if (obj instanceof BigInteger) {
      return hashBigInteger((BigInteger) obj);
    } else if (obj instanceof Character) {
      return hashChar((Character) obj);
    } else if (obj instanceof Tuple4) {
      return hash(((Tuple4) obj).getFirst(), ((Tuple4) obj).getSecond(), ((Tuple4) obj).getThird(), ((Tuple4) obj).getFourth());
    } else if (obj instanceof Tuple3) {
      return hash(((Tuple3) obj).getFirst(), ((Tuple3) obj).getSecond(), ((Tuple3) obj).getThird());
    } else if (obj instanceof Tuple2) {
      return hash(((Tuple2) obj).getFirst(), ((Tuple2) obj).getSecond());
    } else if (obj instanceof Map.Entry) {
      return hash(((Map.Entry) obj).getKey(), ((Map.Entry) obj).getValue());
    } else if (obj instanceof Mapping) {
      return hashMap((Mapping<?, ?>) obj);
    } else if (obj instanceof Map) {
      return hashMap(Mapping.wrap((Map<?, ?>) obj));
    } else if (isUnorderedCollection(obj)) {
      return hashUnorderedCollection((Iterable<?>) obj);
    } else if (obj instanceof Iterable) {
      return hashSequence((Iterable<?>) obj);
    } else if (obj instanceof Reference) {
      return hashObject(((Reference<?>) obj).get());
    } else if (obj instanceof ThreadLocal) {
      return hashObject(((ThreadLocal<?>) obj).get());
    }
    return hashPojo(obj);
  }

  public H merkleTree(@Nonnull final Collection<H> collection) {
    if (collection.isEmpty()) {
      return empty();
    }
    Collection<H> items = collection;
    while (items.size() > 1) {
      final List<H> nextItems = new ArrayList<>(items.size() / 2);
      final Iterator<H> it = items.iterator();
      while (it.hasNext()) {
        final H hashLeft = it.next();
        final H nodeHash;
        if (it.hasNext()) {
          final H hashRight = it.next();
          final Builder builder = builder().addHash(hashLeft).addHash(hashRight);
          if (merkleNodeMarker != null) {
            builder.addHash(merkleNodeMarker);
          }
          nodeHash = builder.build();
        } else {
          nodeHash = hashLeft;
        }
        nextItems.add(nodeHash);
      }
      items = nextItems;
    }
    return items.iterator().next();
  }

}
