package me.derechtepilz.economy.bukkitcommands.commands.commandapicommands;

import me.derechtepilz.economy.bukkitcommands.commands.CommandBase;
import me.derechtepilz.economy.playermanager.Permission;
import me.derechtepilz.economy.utility.ChatFormatter;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import me.derechtepilz.economy.utility.config.Config;
import me.derechtepilz.economy.utility.config.ConfigFields;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ConfigCommandExecutor extends CommandBase {
    public ConfigCommandExecutor(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(TranslatableChatComponent.read("command.wrong_executor"));
            return;
        }
        switch (args.length) {
            case 1 ->  {
                switch (args[0]) {
                    case "itemQuantities", "itemPrice", "startBalance", "interest", "language" -> player.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_few_arguments").replace("%%s", ChatFormatter.valueOf(2)).replace("%s", ChatFormatter.valueOf(args.length)));
                    case "reload" -> {
                        if (!Permission.hasPermission(player, Permission.MODIFY_CONFIG)) {
                            player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                            return;
                        }
                        Config.reloadConfig();
                        player.sendMessage(TranslatableChatComponent.read("configCommand.reload_config"));
                    }
                    case "reset" -> {
                        if (!Permission.hasPermission(player, Permission.RESET_CONFIG)) {
                            player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                            return;
                        }
                        Config.resetConfig();
                        player.sendMessage(TranslatableChatComponent.read("configCommand.reset_config"));
                    }
                }
            }
            case 2 -> {
                switch (args[0]) {
                    case "reload", "reset" -> player.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_many_arguments").replace("%%s", ChatFormatter.valueOf(1)).replace("%s", ChatFormatter.valueOf(args.length)));
                    case "itemQuantities", "itemPrice" -> player.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_few_arguments").replace("%%s", ChatFormatter.valueOf(4)).replace("%s", ChatFormatter.valueOf(args.length)));
                    case "startBalance", "interest", "language" -> player.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_few_arguments").replace("%%s", ChatFormatter.valueOf(3)).replace("%s", ChatFormatter.valueOf(args.length)));
                }
            }
            case 3 -> {
                switch (args[1]) {
                    case "reload", "reset" -> player.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_many_arguments").replace("%%s", ChatFormatter.valueOf(1)).replace("%s", ChatFormatter.valueOf(args.length)));
                    case "itemQuantities", "itemPrice" -> player.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_few_arguments").replace("%%s", ChatFormatter.valueOf(4)).replace("%s", ChatFormatter.valueOf(args.length)));
                    case "startBalance" -> {
                        if (!Permission.hasPermission(player, Permission.MODIFY_CONFIG)) {
                            player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                            return;
                        }
                        if (isNotDouble(args[2])) {
                            player.sendMessage(TranslatableChatComponent.read("fallbackCommand.double_required").replace("%s", ChatFormatter.valueOf(2)));
                            return;
                        }
                        double startBalance = Double.parseDouble(args[2]);
                        if (startBalance < 0) {
                            player.sendMessage(TranslatableChatComponent.read("fallbackCommand.value_too_small").replace("%s", ChatFormatter.valueOf(0)));
                            return;
                        }
                        Config.set(ConfigFields.START_BALANCE, String.valueOf(startBalance));
                        Config.reloadConfig();
                        player.sendMessage(TranslatableChatComponent.read("configCommand.start_balance").replace("%s", ChatFormatter.valueOf(startBalance)));
                    }
                    case "interest" -> {
                        if (!Permission.hasPermission(player, Permission.MODIFY_CONFIG)) {
                            player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                            return;
                        }
                        if (isNotDouble(args[2])) {
                            player.sendMessage(TranslatableChatComponent.read("fallbackCommand.double_required").replace("%s", ChatFormatter.valueOf(2)));
                            return;
                        }
                        double interest = Double.parseDouble(args[2]);
                        if (interest < -99) {
                            player.sendMessage(TranslatableChatComponent.read("fallbackCommand.value_too_small").replace("%s", ChatFormatter.valueOf(-99)));
                            return;
                        }
                        Config.set(ConfigFields.INTEREST, String.valueOf(interest));
                        Config.reloadConfig();
                        player.sendMessage(TranslatableChatComponent.read("configCommand.interest").replace("%s", ChatFormatter.valueOf(interest)));
                    }
                    case "language" -> {
                        if (!Permission.hasPermission(player, Permission.MODIFY_CONFIG)) {
                            player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                            return;
                        }
                        Config.set(ConfigFields.LANGUAGE, args[2]);
                        Config.reloadConfig();
                        player.sendMessage(TranslatableChatComponent.read("configCommand.language").replace("%s", args[2]));
                    }
                }
            }
            case 4 -> {
                switch (args[1]) {
                    case "reload", "reset" -> player.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_many_arguments").replace("%%s", ChatFormatter.valueOf(1)).replace("%s", ChatFormatter.valueOf(args.length)));
                    case "startBalance", "interest", "language" -> player.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_may_arguments").replace("%%s", ChatFormatter.valueOf(3)).replace("%s", ChatFormatter.valueOf(args.length)));
                    case "itemQuantities" -> {
                        switch (args[2]) {
                            case "minAmount" -> {
                                if (!Permission.hasPermission(player, Permission.MODIFY_CONFIG)) {
                                    player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                                    return;
                                }
                                if (isNotInt(args[3])) {
                                    player.sendMessage(TranslatableChatComponent.read("fallbackCommand.integer_required").replace("%s", ChatFormatter.valueOf(4)));
                                    return;
                                }
                                int value = Integer.parseInt(args[3]);
                                if (value < 0) {
                                    player.sendMessage(TranslatableChatComponent.read("fallbackCommand.value_too_small").replace("%s", ChatFormatter.valueOf(0)));
                                    return;
                                }
                                Config.set(ConfigFields.ITEM_QUANTITIES_MIN_AMOUNT, String.valueOf(value));
                                Config.reloadConfig();
                                player.sendMessage(TranslatableChatComponent.read("configCommand.item_quantities.min_amount").replace("%s", ChatFormatter.valueOf(value)));
                            }
                            case "maxAmount" -> {
                                if (!Permission.hasPermission(player, Permission.MODIFY_CONFIG)) {
                                    player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                                    return;
                                }
                                if (isNotInt(args[3])) {
                                    player.sendMessage(TranslatableChatComponent.read("fallbackCommand.integer_required").replace("%s", ChatFormatter.valueOf(4)));
                                    return;
                                }
                                int value = Integer.parseInt(args[3]);
                                if (value < 0) {
                                    player.sendMessage(TranslatableChatComponent.read("fallbackCommand.value_too_small").replace("%s", ChatFormatter.valueOf(0)));
                                    return;
                                }
                                Config.set(ConfigFields.ITEM_QUANTITIES_MAX_AMOUNT, String.valueOf(value));
                                Config.reloadConfig();
                                player.sendMessage(TranslatableChatComponent.read("configCommand.item_quantities.max_amount").replace("%s", ChatFormatter.valueOf(value)));
                            }
                        }
                    }
                    case "itemPrice" -> {
                        switch (args[2]) {
                            case "minAmount" -> {
                                if (!Permission.hasPermission(player, Permission.MODIFY_CONFIG)) {
                                    player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                                    return;
                                }
                                if (isNotInt(args[3])) {
                                    player.sendMessage(TranslatableChatComponent.read("fallbackCommand.integer_required").replace("%s", ChatFormatter.valueOf(4)));
                                    return;
                                }
                                int value = Integer.parseInt(args[3]);
                                if (value < 0) {
                                    player.sendMessage(TranslatableChatComponent.read("fallbackCommand.value_too_small").replace("%s", ChatFormatter.valueOf(0)));
                                    return;
                                }
                                Config.set(ConfigFields.ITEM_PRICE_MIN_AMOUNT, String.valueOf(value));
                                Config.reloadConfig();
                                player.sendMessage(TranslatableChatComponent.read("configCommand.item_price.min_amount").replace("%s", ChatFormatter.valueOf(value)));
                            }
                            case "maxAmount" -> {
                                if (!Permission.hasPermission(player, Permission.MODIFY_CONFIG)) {
                                    player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                                    return;
                                }
                                if (isNotInt(args[3])) {
                                    player.sendMessage(TranslatableChatComponent.read("fallbackCommand.integer_required").replace("%s", ChatFormatter.valueOf(4)));
                                    return;
                                }
                                int value = Integer.parseInt(args[3]);
                                if (value < 0) {
                                    player.sendMessage(TranslatableChatComponent.read("fallbackCommand.value_too_small").replace("%s", ChatFormatter.valueOf(0)));
                                    return;
                                }
                                Config.set(ConfigFields.ITEM_PRICE_MAX_AMOUNT, String.valueOf(value));
                                Config.reloadConfig();
                                player.sendMessage(TranslatableChatComponent.read("configCommand.item_price.max_amount").replace("%s", ChatFormatter.valueOf(value)));
                            }
                        }
                    }
                }
            }
            default -> sender.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_may_arguments").replace("%%s", ChatFormatter.valueOf(4)).replace("%s", ChatFormatter.valueOf(args.length)));
        }
    }
}
