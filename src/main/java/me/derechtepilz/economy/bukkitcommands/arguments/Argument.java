package me.derechtepilz.economy.bukkitcommands.arguments;

public interface Argument<T> {
    T parse(String input);
}
