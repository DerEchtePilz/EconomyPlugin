package me.derechtepilz.economy.bukkitcommands.arguments;

import java.util.List;

public interface Argument<T> {
    T parse(String input);
    List<String> suggests(ArgumentType type, String argument, List<String> additionalArguments);
}
