package me.derechtepilz.economy.bukkitcommands.exceptions;

public class IntegerOutOfRangeException extends RuntimeException {
    public IntegerOutOfRangeException() {
        super();
    }

    public IntegerOutOfRangeException(String message) {
        super(message);
    }

    public IntegerOutOfRangeException(String message, Throwable cause) {
        super(message, cause);
    }

    public IntegerOutOfRangeException(Throwable cause) {
        super(cause);
    }
}
