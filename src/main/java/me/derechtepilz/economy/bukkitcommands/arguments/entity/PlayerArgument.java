package me.derechtepilz.economy.bukkitcommands.arguments.entity;

import me.derechtepilz.economy.bukkitcommands.arguments.Argument;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerArgument implements Argument<Player> {
    @Override
    public Player parse(String input) {
        return Bukkit.getPlayer(input);
    }

    public List<String> suggests(EntityType type, String argument, List<String> additionalArguments) {
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
