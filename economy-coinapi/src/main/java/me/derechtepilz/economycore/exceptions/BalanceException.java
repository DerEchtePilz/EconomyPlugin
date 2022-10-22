package me.derechtepilz.economycore.exceptions;

public class BalanceException extends Exception {
	public BalanceException(String message) {
		super(message);
	}
	public BalanceException(Throwable cause) {
		super(cause);
	}
}
