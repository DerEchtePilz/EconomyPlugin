package me.derechtepilz.economy.bukkitcommands.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import me.derechtepilz.economy.bukkitcommands.api.executors.CommandExecutor;
import me.derechtepilz.economy.bukkitcommands.api.executors.ConsoleCommandExecutor;
import me.derechtepilz.economy.bukkitcommands.api.executors.PlayerCommandExecutor;
import me.derechtepilz.economy.bukkitcommands.arguments.ArgumentTypes;
import me.derechtepilz.economy.bukkitcommands.exceptions.IllegalArgumentLengthException;
import me.derechtepilz.economy.bukkitcommands.exceptions.IllegalArgumentTypeException;
import me.derechtepilz.economy.bukkitcommands.exceptions.IllegalExecutorException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandBase {
    private final String commandTree;

    private final CommandSender sender;
    private final String[] args;
    private Object[] arguments;
    private final List<Argument<?>> argumentTypes = new ArrayList<>();

    public CommandBase(CommandSender sender, Command command, String label, String[] args) {
        this.sender = sender;
        this.args = args;

        CommandTree commandTree = new CommandTree(argumentTypes, args);
        this.commandTree = commandTree.getCommandTree();
    }

    public CommandBase withArguments(Argument<?> argument) {
        argumentTypes.add(argument);
        return this;
    }

    public void executes(CommandExecutor executor) throws IllegalArgumentTypeException, IllegalArgumentLengthException {
        validateArguments(args, commandTree);
        executor.run(sender, arguments);
    }

    public void executesPlayer(PlayerCommandExecutor executor) throws IllegalArgumentTypeException, IllegalExecutorException, IllegalArgumentLengthException {
        validateArguments(args, commandTree);
        validateExecutor(sender.getClass(), Player.class);
        executor.run((Player) sender, arguments);
    }

    public void executesConsole(ConsoleCommandExecutor executor) throws IllegalArgumentTypeException, IllegalExecutorException, IllegalArgumentLengthException {
        validateArguments(args, commandTree);
        validateExecutor(sender.getClass(), ConsoleCommandSender.class);
        executor.run((ConsoleCommandExecutor) sender, arguments);
    }

    void validateArguments(String[] args, String commandTree) throws IllegalArgumentTypeException, IllegalArgumentLengthException {
        JsonElement element = JsonParser.parseString(commandTree);
        JsonArray array = element.getAsJsonArray();

        if (argumentTypes.size() < args.length) {
            throw new IllegalArgumentLengthException("Too many arguments provided! Maximum allowed: " + argumentTypes.size() + ", you provided: " + args.length);
        }

        if (argumentTypes.size() > args.length) {
            throw new IllegalArgumentLengthException("Too few arguments provided! Arguments required: " + argumentTypes.size() + ", you provided: " + args.length);
        }

        arguments = new Object[args.length];

        for (int i = 0; i < args.length; i++) {
            String argumentTypeDefined = array.get(i).getAsJsonObject().getAsJsonPrimitive("type").getAsString();
            ArgumentTypes argumentType = ArgumentTypes.valueOf(argumentTypeDefined);
            Argument<?> argument = argumentType.getArgument();
            if (argument.parse(args[i]) == null) {
                throw new IllegalArgumentTypeException("Found wrong argument type! Should be " + argumentType.getType());
            }
            arguments[i] = argument.parse(args[i]);
        }
    }

    void validateExecutor(Class sender, Class requiredType) throws IllegalExecutorException {
        if (!sender.getCanonicalName().equals(requiredType.getCanonicalName())) {
            throw new IllegalExecutorException("This command has no implementation for " + sender.getCanonicalName());
        }
    }
}
