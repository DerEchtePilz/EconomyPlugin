package me.derechtepilz.economy.utility.exceptions;

@SuppressWarnings("unused")
public class UnsuccessfulAPIRequestException extends RuntimeException {
    public UnsuccessfulAPIRequestException() {
        super();
    }

    public UnsuccessfulAPIRequestException(String message) {
        super(message);
    }

    public UnsuccessfulAPIRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsuccessfulAPIRequestException(Throwable cause) {
        super(cause);
    }
}
