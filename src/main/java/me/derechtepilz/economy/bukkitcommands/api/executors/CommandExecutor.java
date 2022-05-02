package me.derechtepilz.economy.bukkitcommands.api.executors;

import me.derechtepilz.economy.bukkitcommands.arguments.Argument;
import org.bukkit.command.CommandSender;

public interface CommandExecutor {
    void run(CommandSender sender, Argument<?>[] args);
}
