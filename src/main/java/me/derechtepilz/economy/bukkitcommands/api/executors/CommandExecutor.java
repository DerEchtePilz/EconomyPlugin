package me.derechtepilz.economy.bukkitcommands.api.executors;

import org.bukkit.command.CommandSender;

public interface CommandExecutor {
    void run(CommandSender sender, Object[] args);
}
