package me.derechtepilz.economy.bukkitcommands.arguments;

import me.derechtepilz.economy.bukkitcommands.api.Argument;
import me.derechtepilz.economy.bukkitcommands.exceptions.IntegerOutOfRangeException;

import java.util.List;

public class IntegerArgument implements Argument<Integer> {

    private final int min;
    private final int max;

    public IntegerArgument() {
        this.min = Integer.MIN_VALUE;
        this.max = Integer.MAX_VALUE;
    }

    public IntegerArgument(int min) {
        this.min = min;
        this.max = Integer.MAX_VALUE;
    }

    public IntegerArgument(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public Integer parse(String input) {
        try {
            int number = Integer.parseInt(input);
            if (number < min) {
                throw new IntegerOutOfRangeException("Provided number too small. Should be at least " + min + "!");
            }
            if (number > max) {
                throw new IntegerOutOfRangeException("Provided number too big. Should be " + max + " at maximum!");
            }
            return number;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    @Override
    public String getType() {
        return ArgumentTypes.INTEGER_ARGUMENT.getType();
    }

    public List<String> suggests(String argument, List<String> additionalSuggestions) {
        return null;
    }
}
