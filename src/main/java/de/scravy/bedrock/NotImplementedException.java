package de.scravy.bedrock;

/**
 * Thrown when a method (for example when implementing an interface) is
 * not (yet) implemented by the implementation.
 */
public class NotImplementedException extends RuntimeException {
  public NotImplementedException() {
  }

  public NotImplementedException(final String message) {
    super(message);
  }
}
