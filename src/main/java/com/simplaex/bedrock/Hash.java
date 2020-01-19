package com.simplaex.bedrock;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;
import java.nio.ByteBuffer;

public interface Hash<H extends Hash<H>> extends Comparable<H> {

  byte[] getBytes();

  @Override
  default int compareTo(final H hash) {
    return toString().compareTo(hash.toString());
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  @EqualsAndHashCode
  abstract class Hash256<H extends Hash<H>> implements Hash<H> {

    static final int LENGTH = 32;

    private final long v0;
    private final long v1;
    private final long v2;
    private final long v3;

    Hash256(final byte[] digest) {
      this(
        Numbers.longFromBytes(digest[0], digest[1], digest[2], digest[3], digest[4], digest[5], digest[6], digest[7]),
        Numbers.longFromBytes(digest[8], digest[9], digest[10], digest[11], digest[12], digest[13], digest[14], digest[15]),
        Numbers.longFromBytes(digest[16], digest[17], digest[18], digest[19], digest[20], digest[21], digest[22], digest[23]),
        Numbers.longFromBytes(digest[24], digest[25], digest[26], digest[27], digest[28], digest[29], digest[30], digest[31])
      );
    }

    @Nonnull
    public byte[] getBytes() {
      final byte[] result = new byte[LENGTH];
      final ByteBuffer bb = ByteBuffer.wrap(result);
      bb.putLong(v0);
      bb.putLong(v1);
      bb.putLong(v2);
      bb.putLong(v3);
      return result;
    }

    @Nonnull
    public String toString() {
      return String.format("%016x%016x%016x%016x", v0, v1, v2, v3);
    }
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  @EqualsAndHashCode
  abstract class Hash512<H extends Hash<H>> implements Hash<H> {

    static final int LENGTH = 64;

    private final long v0;
    private final long v1;
    private final long v2;
    private final long v3;
    private final long v4;
    private final long v5;
    private final long v6;
    private final long v7;

    Hash512(final byte[] digest) {
      this(
        Numbers.longFromBytes(digest[0], digest[1], digest[2], digest[3], digest[4], digest[5], digest[6], digest[7]),
        Numbers.longFromBytes(digest[8], digest[9], digest[10], digest[11], digest[12], digest[13], digest[14], digest[15]),
        Numbers.longFromBytes(digest[16], digest[17], digest[18], digest[19], digest[20], digest[21], digest[22], digest[23]),
        Numbers.longFromBytes(digest[24], digest[25], digest[26], digest[27], digest[28], digest[29], digest[30], digest[31]),
        Numbers.longFromBytes(digest[32], digest[33], digest[34], digest[35], digest[36], digest[37], digest[38], digest[39]),
        Numbers.longFromBytes(digest[40], digest[41], digest[42], digest[43], digest[44], digest[45], digest[46], digest[47]),
        Numbers.longFromBytes(digest[48], digest[49], digest[50], digest[51], digest[52], digest[53], digest[54], digest[55]),
        Numbers.longFromBytes(digest[56], digest[57], digest[58], digest[59], digest[60], digest[61], digest[62], digest[63])
      );
    }

    @Nonnull
    public byte[] getBytes() {
      final byte[] result = new byte[LENGTH];
      final ByteBuffer bb = ByteBuffer.wrap(result);
      bb.putLong(v0);
      bb.putLong(v1);
      bb.putLong(v2);
      bb.putLong(v3);
      bb.putLong(v4);
      bb.putLong(v5);
      bb.putLong(v6);
      bb.putLong(v7);
      return result;
    }

    @Nonnull
    public String toString() {
      return String.format("%016x%016x%016x%016x%016x%016x%016x%016x", v0, v1, v2, v3, v4, v5, v6, v7);
    }

    @Override
    public int compareTo(final H hash) {
      return toString().compareTo(hash.toString());
    }
  }
}
