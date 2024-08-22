package zip.sodium.home.serialization.exception;

public final class DecodingException extends RuntimeException {
    public static DecodingException from(final Exception e) {
        return new DecodingException(e);
    }

    public static DecodingException from(final String e) {
        return new DecodingException(e);
    }

    private DecodingException(final String message) {
        super(message);
    }

    private DecodingException(final Throwable cause) {
        super(cause);
    }
}
