package de.scravy.bedrock;

import javax.annotation.Nonnull;
import java.security.MessageDigest;

public class Sha512 extends Hash.Hash512<Sha512> {

  private static final String ALGORITHM = "SHA-512";

  private Sha512(final byte[] digest) {
    super(digest);
  }

  @Nonnull
  public static HashAlgorithm<Sha512> singleHash() {
    return new HashAlgorithm<>(LENGTH, ALGORITHM, Sha512::new, MessageDigest::digest);
  }

  @Nonnull
  public static HashAlgorithm<Sha512> doubleHash() {
    return new HashAlgorithm<>(LENGTH, ALGORITHM, Sha512::new, md -> md.digest(md.digest()));
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
