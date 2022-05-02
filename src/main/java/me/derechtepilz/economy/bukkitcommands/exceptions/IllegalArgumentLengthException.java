package me.derechtepilz.economy.bukkitcommands.exceptions;

public class IllegalArgumentLengthException extends Exception {
    public IllegalArgumentLengthException() {
        super();
    }

    public IllegalArgumentLengthException(String message) {
        super(message);
    }

    public IllegalArgumentLengthException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalArgumentLengthException(Throwable cause) {
        super(cause);
    }
}
