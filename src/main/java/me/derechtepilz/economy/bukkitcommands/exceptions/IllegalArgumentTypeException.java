package me.derechtepilz.economy.bukkitcommands.exceptions;

public class IllegalArgumentTypeException extends Exception {
    public IllegalArgumentTypeException() {
        super();
    }

    public IllegalArgumentTypeException(String message) {
        super(message);
    }

    public IllegalArgumentTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalArgumentTypeException(Throwable cause) {
        super(cause);
    }
}
