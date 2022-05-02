package me.derechtepilz.economy.bukkitcommands.exceptions;

public class IllegalExecutorException extends Exception {
    public IllegalExecutorException() {
        super();
    }

    public IllegalExecutorException(String message) {
        super(message);
    }

    public IllegalExecutorException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalExecutorException(Throwable cause) {
        super(cause);
    }
}
