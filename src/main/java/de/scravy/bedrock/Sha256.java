package de.scravy.bedrock;

import javax.annotation.Nonnull;
import java.security.MessageDigest;

public class Sha256 extends Hash.Hash256<Sha256> {

  private static final String ALGORITHM = "SHA-256";

  private Sha256(final byte[] digest) {
    super(digest);
  }

  @Nonnull
  public static HashAlgorithm<Sha256> singleHash() {
    return new HashAlgorithm<>(LENGTH, ALGORITHM, Sha256::new, MessageDigest::digest);
  }

  @Nonnull
  public static HashAlgorithm<Sha256> doubleHash() {
    return new HashAlgorithm<>(LENGTH, ALGORITHM, Sha256::new, md -> md.digest(md.digest()));
  }

  public static boolean isSupported() {
    try {
      singleHash().empty();
      return true;
    } catch (final AssertionError exc) {
      return false;
    }
  }
}
