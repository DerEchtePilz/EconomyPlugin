package me.derechtepilz.economy.bukkitcommands.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import me.derechtepilz.economy.bukkitcommands.api.executors.CommandExecutor;
import me.derechtepilz.economy.bukkitcommands.api.executors.ConsoleCommandExecutor;
import me.derechtepilz.economy.bukkitcommands.api.executors.PlayerCommandExecutor;
import me.derechtepilz.economy.bukkitcommands.arguments.Argument;
import me.derechtepilz.economy.bukkitcommands.arguments.ArgumentTypes;
import me.derechtepilz.economy.bukkitcommands.exceptions.IllegalArgumentLengthException;
import me.derechtepilz.economy.bukkitcommands.exceptions.IllegalArgumentTypeException;
import me.derechtepilz.economy.bukkitcommands.exceptions.IllegalExecutorException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandBase {
    private final String commandTree;

    private final CommandSender sender;
    private final String[] args;
    private Argument<?>[] arguments;

    public CommandBase(CommandSender sender, Command command, String label, String[] args, List<ArgumentTypes> argumentTypesList) throws IllegalArgumentLengthException {
        if (args.length != argumentTypesList.size()) {
            throw new IllegalArgumentLengthException("Found " + args.length + " arguments, should be " + argumentTypesList.size());
        }
        this.sender = sender;
        this.args = args;

        CommandTree commandTree = new CommandTree(argumentTypesList, args);
        this.commandTree = commandTree.getCommandTree();
    }

    public void executes(CommandExecutor executor) throws IllegalArgumentTypeException {
        validateArguments(args, commandTree);
        executor.run(sender, arguments);
    }

    public void executesPlayer(PlayerCommandExecutor executor) throws IllegalArgumentTypeException, IllegalExecutorException {
        validateArguments(args, commandTree);
        validateExecutor(sender.getClass(), Player.class);
        executor.run((Player) sender, arguments);
    }

    public void executesConsole(ConsoleCommandExecutor executor) throws IllegalArgumentTypeException, IllegalExecutorException {
        validateArguments(args, commandTree);
        validateExecutor(sender.getClass(), ConsoleCommandSender.class);
        executor.run((ConsoleCommandExecutor) sender, arguments);
    }

    void validateArguments(String[] args, String commandTree) throws IllegalArgumentTypeException {
        JsonElement element = JsonParser.parseString(commandTree);
        JsonArray array = element.getAsJsonArray();
        arguments = new Argument[args.length];

        for (int i = 0; i < args.length; i++) {
            String argumentTypeDefined = array.get(i).getAsJsonObject().getAsJsonPrimitive("type").getAsString();
            ArgumentTypes argumentType = ArgumentTypes.valueOf(argumentTypeDefined);
            Argument<?> argument = argumentType.getArgument();
            if (argument.parse(args[i]) == null) {
                throw new IllegalArgumentTypeException("Found wrong argument type! Should be " + argumentType.getType());
            }

            arguments[i] = argument;
        }
    }

    void validateExecutor(Class sender, Class requiredType) throws IllegalExecutorException {
        if (!sender.getCanonicalName().equals(requiredType.getCanonicalName())) {
            throw new IllegalExecutorException("This command has no implementation for " + sender.getCanonicalName());
        }
    }
}
