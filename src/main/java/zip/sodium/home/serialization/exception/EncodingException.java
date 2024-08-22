package zip.sodium.home.serialization.exception;

public final class EncodingException extends RuntimeException {
    public static EncodingException from(final Exception e) {
        return new EncodingException(e);
    }

    public static EncodingException from(final String e) {
        return new EncodingException(e);
    }

    private EncodingException(final String message) {
        super(message);
    }

    private EncodingException(final Throwable cause) {
        super(cause);
    }
}
