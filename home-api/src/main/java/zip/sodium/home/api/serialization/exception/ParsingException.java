package zip.sodium.home.api.serialization.exception;

public final class ParsingException extends RuntimeException {
  public static ParsingException from(final Exception e) {
    return new ParsingException(e);
  }

  public static ParsingException from(final String e) {
    return new ParsingException(e);
  }

  private ParsingException(final String message) {
    super(message);
  }

  private ParsingException(final Throwable cause) {
    super(cause);
  }
}
