package me.derechtepilz.economy.bukkitcommands.api.executors;

import me.derechtepilz.economy.bukkitcommands.arguments.Argument;

public interface ConsoleCommandExecutor {
    void run(ConsoleCommandExecutor console, Argument<?>[] args);
}
