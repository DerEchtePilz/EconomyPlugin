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
                        suggestions.add(player.getName());
                    } else {
                        if (player.getName().startsWith(argument)) {
                            suggestions.add(player.getName());
                        }
                    }
                }
                return suggestions;
            }
        }
        return suggestions;
    }

}
