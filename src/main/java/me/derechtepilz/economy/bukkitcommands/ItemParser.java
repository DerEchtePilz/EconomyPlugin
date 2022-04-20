package me.derechtepilz.economy.bukkitcommands;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemParser {
    private ItemParser() {

    }

    /**
     *
     * @param input The input a user makes by executing a command
     * @return the parsed ItemStack if available
     */
    public static ItemStack read(String input) {
        String minecraftItemStackId = input;
        if (minecraftItemStackId.startsWith("minecraft:")) {
            minecraftItemStackId = input.substring(10);
        }
        return new ItemStack(Material.getMaterial(minecraftItemStackId.toUpperCase()));
    }

    public static List<String> suggests(SuggestType type, String argument) {
        List<String> suggestions = new ArrayList<>();
        final String[] MINECRAFT_PREFIX = {"m", "mi", "min", "mine", "minec", "minecr", "minecra", "minecraf", "minecraft", "minecraft:"};
        switch (type) {
            case ITEM -> {
                for (Material material : Material.values()) {
                    if (material.isItem()) {
                        suggestions.add("minecraft:" + material.name().toLowerCase());
                    }
                }
                if (argument.equals("")) {
                    return suggestions;
                }
                String argumentItemId;
                List<String> updatedSuggestions = new ArrayList<>();
                if (startsWithAny(argument, MINECRAFT_PREFIX)) {
                    if (argument.length() >= 11) {
                        argumentItemId = argument.substring(10);
                        for (String suggestion : suggestions) {
                            String itemId = suggestion.substring(10);
                            if (itemId.startsWith(argumentItemId)) {
                                updatedSuggestions.add(suggestion);
                            }
                        }
                    }
                } else {
                    argumentItemId = argument;
                    for (String suggestion : suggestions) {
                        String itemId = suggestion.substring(10);
                        if (itemId.startsWith(argumentItemId)) {
                            updatedSuggestions.add(suggestion);
                        }
                    }
                }
                return updatedSuggestions;
            }
            case BLOCK -> {
                for (Material material : Material.values()) {
                    if (material.isBlock()) {
                        suggestions.add("minecraft:" + material.name().toLowerCase());
                    }
                }
            }
        }
        return suggestions;
    }

    private static boolean startsWithAny(String argument, String[] prefixes) {
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
