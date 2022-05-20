package me.derechtepilz.economy.bukkitcommands.api.executors;

import org.bukkit.entity.Player;

public interface PlayerCommandExecutor {
    void run(Player sender, Object[] args);
}
