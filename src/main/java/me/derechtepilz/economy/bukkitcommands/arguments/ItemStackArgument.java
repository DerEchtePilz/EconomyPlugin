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
            case PLAYER -> {
                throw new IllegalArgumentTypeException();
            }
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