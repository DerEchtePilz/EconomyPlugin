package me.derechtepilz.economy.bukkitcommands.api;

public interface Argument<T> {
    T parse(String input);

    String getType();
}
