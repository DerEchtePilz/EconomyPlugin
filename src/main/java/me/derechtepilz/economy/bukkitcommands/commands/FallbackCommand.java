package me.derechtepilz.economy.bukkitcommands.commands;

import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.bukkitcommands.arguments.ArgumentType;
import me.derechtepilz.economy.bukkitcommands.arguments.ItemStackArgument;
import me.derechtepilz.economy.bukkitcommands.arguments.PlayerArgument;
import me.derechtepilz.economy.bukkitcommands.arguments.StringArgument;
import me.derechtepilz.economy.playermanager.Permission;
import me.derechtepilz.economy.utility.ChatFormatter;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FallbackCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (Main.getInstance().isWasCommandAPILoaded()) {
            return false;
        }
        if (args.length >= 1) {
            switch (args[0].toLowerCase()) {
                case "buy" -> new CommandHandler(sender, command, label, args).executeBuyCommand();
                case "canceloffer" -> new CommandHandler(sender, command, label, args).executeCancelOfferCommand();
                case "config" -> new CommandHandler(sender, command, label, args).executeConfigCommand();
                case "createoffer" -> new CommandHandler(sender, command, label, args).executeCreateOfferCommand();
                case "givecoins" -> new CommandHandler(sender, command, label, args).executeGiveCoinsCommand();
                case "permission" -> new CommandHandler(sender, command, label, args).executePermissionCommand();
                case "setcoins" -> new CommandHandler(sender, command, label, args).executeSetCoinsCommand();
                case "takecoins" -> new CommandHandler(sender, command, label, args).executeTakeCoinsCommand();
                default -> sender.sendMessage(TranslatableChatComponent.read("fallbackCommand.wrong_argument"));
            }
        } else {
            sender.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_few_arguments").replace("%%s", ChatFormatter.valueOf(1)).replace("%s", ChatFormatter.valueOf(args.length)));
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 1 -> {
                return new StringArgument().suggests(ArgumentType.STRING, args[0], Arrays.asList("buy", "canceloffer", "config", "createoffer", "givecoins", "permission", "setcoins", "takecoins"));
            }
            case 2 -> {
                if (args[0].equalsIgnoreCase("buy")) {
                    return new ItemStackArgument().suggests(ArgumentType.ITEM, args[1], List.of("special", "player"));
                }
                if (args[0].equalsIgnoreCase("canceloffer")) {
                    return new ArrayList<>();
                }
                if (args[0].equalsIgnoreCase("config")) {
                    return new StringArgument().suggests(ArgumentType.STRING, args[1], List.of("itemQuantities", "itemPrice", "startBalance", "interest", "language", "reload", "reset"));
                }
                if (args[0].equalsIgnoreCase("createoffer")) {
                    return new ItemStackArgument().suggests(ArgumentType.ITEM, args[1], null);
                }
                if (args[0].equalsIgnoreCase("givecoins")) {
                    return new ArrayList<>();
                }
                if (args[0].equalsIgnoreCase("permission")) {
                    return new StringArgument().suggests(ArgumentType.STRING, args[1], List.of("set", "get", "remove"));
                }
                if (args[0].equalsIgnoreCase("setcoins")) {
                    return new PlayerArgument().suggests(ArgumentType.PLAYER, args[1], null);
                }
            }
            case 3 -> {
                if (args[0].equalsIgnoreCase("config")) {
                    if (args[1].equalsIgnoreCase("itemQuantities")) {
                        return new StringArgument().suggests(ArgumentType.STRING, args[2], List.of("minAmount", "maxAmount"));
                    }
                    if (args[1].equalsIgnoreCase("itemPrice")) {
                        return new StringArgument().suggests(ArgumentType.STRING, args[2], List.of("minAmount", "maxAmount"));
                    }
                }
                if (args[0].equalsIgnoreCase("givecoins")) {
                    return new PlayerArgument().suggests(ArgumentType.PLAYER, args[2], null);
                }
                if (args[0].equalsIgnoreCase("permission")) {
                    return new PlayerArgument().suggests(ArgumentType.PLAYER, args[2], null);
                }
            }
            case 4 -> {
                if (args[0].equalsIgnoreCase("permission")) {
                    if (args[1].equalsIgnoreCase("set")) {
                        return new StringArgument().suggests(ArgumentType.STRING, args[3], getPermissions());
                    }
                    if (args[1].equalsIgnoreCase("remove")) {
                        return new StringArgument().suggests(ArgumentType.STRING, args[3], getPermissions());
                    }
                }
            }
            default -> {
                return new ArrayList<>();
            }
        }
        return null;
    }

    private List<String> getPermissions() {
        List<String> permissions = new ArrayList<>();
        for (Permission permission : Permission.values()) {
            permissions.add(permission.getName());
        }
        return permissions;
    }
}
