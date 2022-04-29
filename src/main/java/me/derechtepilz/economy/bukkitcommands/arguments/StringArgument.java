package me.derechtepilz.economy.bukkitcommands.arguments;

import me.derechtepilz.economy.bukkitcommands.exceptions.IllegalArgumentTypeException;

import java.util.ArrayList;
import java.util.List;

public class StringArgument implements Argument<String> {
    @Override
    public String parse(String input) {
        return input;
    }

    /**
     *
     * @param type The type of suggestions sent to the player
     * @param argument The typed argument to only suggest relevant strings
     * @param additionalArguments Every argument which is relevant
     * @return The list of suggestions
     */
    @Override
    public List<String> suggests(ArgumentType type, String argument, List<String> additionalArguments) {
        List<String> suggestions = new ArrayList<>();
        switch (type) {
            case PLAYER, ITEM, BLOCK -> throw new IllegalArgumentTypeException();
            case STRING -> {
                for (String additionalArgument : additionalArguments) {
                    if (additionalArgument.startsWith(argument)) {
                        suggestions.add(additionalArgument);
                    }
                }
            }
        }
        return suggestions;
    }
}
