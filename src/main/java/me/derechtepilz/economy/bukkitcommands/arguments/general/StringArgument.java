package me.derechtepilz.economy.bukkitcommands.arguments.general;

import me.derechtepilz.economy.bukkitcommands.arguments.Argument;

import java.util.ArrayList;
import java.util.List;

public class StringArgument implements Argument<String> {
    @Override
    public String parse(String input) {
        return input;
    }

    /**
     *
     * @param argument The typed argument to only suggest relevant strings
     * @param additionalArguments Every argument which is relevant
     * @return The list of suggestions
     */
    public List<String> suggests(String argument, List<String> additionalArguments) {
        List<String> suggestions = new ArrayList<>();
            for (String additionalArgument : additionalArguments) {
                if (additionalArgument.startsWith(argument)) {
                    suggestions.add(additionalArgument);
                }
            }
        return suggestions;
    }
}
