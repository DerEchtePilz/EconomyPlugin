/**
 * MIT License
 * <p>
 * Copyright (c) 2022 DerEchtePilz
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
