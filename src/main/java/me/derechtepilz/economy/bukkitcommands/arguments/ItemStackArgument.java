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
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemStackArgument implements Argument<ItemStack> {
    /**
     *
     * @param input The input a user makes by executing a command
     * @return the parsed ItemStack if available
     */
    @Override
    public ItemStack parse(String input) {
        String minecraftItemStackId = input;
        if (minecraftItemStackId.startsWith("minecraft:")) {
            minecraftItemStackId = input.substring(10);
        }
        return (Material.getMaterial(minecraftItemStackId.toUpperCase()) != null) ? new ItemStack(Material.getMaterial(minecraftItemStackId.toUpperCase())) : null;
    }

    /**
     *
     * @param type The type of suggestions to be sent to the player
     * @param argument The already provided argument to only suggest relevant items
     * @param additionalArguments A list which contains arguments that should be suggested along with Minecraft item ids
     * @return The list of suggestions
     */
    @Override
    public List<String> suggests(ArgumentType type, String argument, List<String> additionalArguments) {
        List<String> minecraftSuggestions = new ArrayList<>();
        List<String> otherSuggestions = new ArrayList<>();
        if (additionalArguments != null) {
            if (argument.equals("")) {
                otherSuggestions.addAll(additionalArguments);
            } else {
                for (String additionalArgument : additionalArguments) {
                    if (additionalArgument.startsWith(argument)) {
                        otherSuggestions.add(additionalArgument);
                    }
                }
            }
        }
        final String[] MINECRAFT_PREFIX = {"m", "mi", "min", "mine", "minec", "minecr", "minecra", "minecraf", "minecraft", "minecraft:"};
        switch (type) {
            case ITEM -> {
                for (Material material : Material.values()) {
                    if (material.isItem()) {
                        minecraftSuggestions.add("minecraft:" + material.name().toLowerCase());
                    }
                }
                if (argument.equals("")) {
                    if (additionalArguments != null) {
                        minecraftSuggestions.addAll(otherSuggestions);
                    }
                    return minecraftSuggestions;
                }
                String argumentItemId;
                List<String> updatedSuggestions = new ArrayList<>();
                if (startsWithAny(argument, MINECRAFT_PREFIX)) {
                    if (argument.length() >= 11) {
                        argumentItemId = argument.substring(10);
                        for (String suggestion : minecraftSuggestions) {
                            String itemId = suggestion.substring(10);
                            if (itemId.startsWith(argumentItemId)) {
                                updatedSuggestions.add(suggestion);
                            }
                        }
                    }
                } else {
                    argumentItemId = argument;
                    for (String suggestion : minecraftSuggestions) {
                        String itemId = suggestion.substring(10);
                        if (itemId.startsWith(argumentItemId)) {
                            updatedSuggestions.add(suggestion);
                        }
                    }
                }
                updatedSuggestions.addAll(otherSuggestions);
                return updatedSuggestions;
            }
            case BLOCK -> {
                for (Material material : Material.values()) {
                    if (material.isBlock()) {
                        minecraftSuggestions.add("minecraft:" + material.name().toLowerCase());
                    }
                }
            }
            case PLAYER, STRING -> throw new IllegalArgumentTypeException();
        }
        return minecraftSuggestions;
    }

    private boolean startsWithAny(String argument, String[] prefixes) {
        if (argument.length() <= 10) {
            for (String prefix : prefixes) {
                if (argument.equals(prefix)) {
                    return true;
                }
            }
        } else {
            return argument.startsWith("minecraft:");
        }
        return false;
    }
}
