package me.derechtepilz.economy.bukkitcommands.api.executors;

import me.derechtepilz.economy.bukkitcommands.arguments.Argument;
import org.bukkit.entity.Player;

public interface PlayerCommandExecutor {
    void run(Player sender, Argument<?>[] args);
}
