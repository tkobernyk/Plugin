package TeamCity.exception;


public class NoSupportedOSException extends RuntimeException {

    public NoSupportedOSException() {
    }

    public NoSupportedOSException(String message) {
        super(message);
    }

    public NoSupportedOSException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSupportedOSException(Throwable cause) {
        super(cause);
    }

    public NoSupportedOSException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
