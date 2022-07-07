package me.derechtepilz.economy.utility.exceptions;

@SuppressWarnings("unused")
public class InvalidRangeException extends RuntimeException {
    public InvalidRangeException() {
        super();
    }

    public InvalidRangeException(String message) {
        super(message);
    }

    public InvalidRangeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidRangeException(Throwable cause) {
        super(cause);
    }
}
