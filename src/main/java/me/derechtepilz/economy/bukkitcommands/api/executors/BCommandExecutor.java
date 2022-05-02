package me.derechtepilz.economy.bukkitcommands.api.executors;

import me.derechtepilz.economy.bukkitcommands.api.CommandBase;
import me.derechtepilz.economy.bukkitcommands.arguments.ArgumentTypes;
import me.derechtepilz.economy.bukkitcommands.exceptions.IllegalArgumentLengthException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class BCommandExecutor {

    ArgumentTypes[] argumentTypes;

    public BCommandExecutor(int argumentLength, int requiredArgumentLength, ArgumentTypes... argumentTypes) throws IllegalArgumentLengthException {
        this.argumentTypes = argumentTypes;

        if (argumentLength != requiredArgumentLength) {
            throw new IllegalArgumentLengthException("Found " + argumentLength + " arguments, should be " + requiredArgumentLength);
        }
    }

    public CommandBase onCommand(CommandSender sender, Command command, String label, String[] args) throws IllegalArgumentLengthException {
        return new CommandBase(sender, command, label, args, List.of(argumentTypes));
    }
}
