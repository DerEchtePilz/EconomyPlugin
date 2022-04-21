/**
 * MIT License
 *
 * Copyright (c) 2022 DerEchtePilz
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerArgument implements Argument<Player> {
    @Override
    public Player parse(String input) {
        return Bukkit.getPlayer(input);
    }

    @Override
    public List<String> suggests(ArgumentType type, String argument, List<String> additionalArguments) {
        List<String> suggestions = new ArrayList<>();
        if (additionalArguments != null) {
            if (argument.equals("")) {
                suggestions.addAll(additionalArguments);
            } else {
                for (String additionalArgument : additionalArguments) {
                    if (additionalArgument.startsWith(argument)) {
                        suggestions.add(additionalArgument);
                    }
                }
            }
        }
        switch (type) {
            case BLOCK, ITEM, STRING -> throw new IllegalArgumentTypeException();
            case PLAYER -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (argument.equals("")) {
                        if (player.getName().startsWith(argument)) {
                            suggestions.add(player.getName());
                        }
                    } else {
                        suggestions.add(player.getName());
                    }
                }
                return suggestions;
            }
        }
        return suggestions;
    }

}
