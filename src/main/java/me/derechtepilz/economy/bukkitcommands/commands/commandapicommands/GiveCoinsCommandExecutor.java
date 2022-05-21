package me.derechtepilz.economy.bukkitcommands.commands.commandapicommands;

import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.bukkitcommands.arguments.PlayerArgument;
import me.derechtepilz.economy.bukkitcommands.commands.CommandBase;
import me.derechtepilz.economy.economymanager.BankManager;
import me.derechtepilz.economy.playermanager.Permission;
import me.derechtepilz.economy.utility.ChatFormatter;
import me.derechtepilz.economy.utility.NamespacedKeys;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class GiveCoinsCommandExecutor extends CommandBase {
    public GiveCoinsCommandExecutor(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 1 -> sender.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_few_arguments").replace("%%s", ChatFormatter.valueOf(2)).replace("%s", ChatFormatter.valueOf(args.length)));
            case 2 -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(TranslatableChatComponent.read("command.wrong_executor"));
                    return;
                }
                if (!Permission.hasPermission(player, Permission.GIVE_COINS)) {
                    player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                    return;
                }
                if (isNotDouble(args[1])) {
                    player.sendMessage(TranslatableChatComponent.read("fallbackCommand.double_required").replace("%s", ChatFormatter.valueOf(2)));
                    return;
                }
                double amount = Double.parseDouble(args[1]);
                if (amount < 0) {
                    player.sendMessage(TranslatableChatComponent.read("fallbackCommand.value_too_small").replace("%s", ChatFormatter.valueOf(0)));
                    return;
                }

                double balance = player.getPersistentDataContainer().get(NamespacedKeys.BALANCE.getKey(), PersistentDataType.DOUBLE);

                if (!Main.getInstance().getBankAccounts().containsKey(player.getUniqueId())) {
                    player.sendMessage(TranslatableChatComponent.read("command.player_executor.target.bank_account_missing"));
                    return;
                }
                BankManager manager = new BankManager(player, amount + balance);
                player.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.player_executor.give_coins_to_player").replace("%%s", ChatFormatter.valueOf(manager.getBalance())).replace("%s", ChatFormatter.valueOf(amount)));
            }
            case 3 -> {
                if (sender instanceof Player player) {
                    if (!Permission.hasPermission(player, Permission.GIVE_COINS)) {
                        player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                        return;
                    }
                    if (isNotDouble(args[1])) {
                        player.sendMessage(TranslatableChatComponent.read("fallbackCommand.double_required").replace("%s", ChatFormatter.valueOf(2)));
                        return;
                    }
                    double amount = Double.parseDouble(args[1]);
                    if (amount < 0) {
                        player.sendMessage(TranslatableChatComponent.read("fallbackCommand.value_too_small").replace("%s", ChatFormatter.valueOf(0)));
                        return;
                    }
                    Player target = new PlayerArgument().parse(args[2]);
                    if (target == null) {
                        player.sendMessage(TranslatableChatComponent.read("fallbackCommand.player_not_recognized").replace("%s", args[2]));
                        return;
                    }
                    double balance = target.getPersistentDataContainer().get(NamespacedKeys.BALANCE.getKey(), PersistentDataType.DOUBLE);

                    if (!Main.getInstance().getBankAccounts().containsKey(player.getUniqueId())) {
                        player.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.player_executor.target_bank_account_missing").replace("%s", target.getName()));
                        return;
                    }
                    BankManager manager = new BankManager(target, balance + amount);
                    player.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.player_executor.give_coins_to_other_player").replace("%%%s", ChatFormatter.valueOf(manager.getBalance())).replace("%%s", ChatFormatter.valueOf(amount)).replace("%s", target.getName()));
                    target.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.player_executor.receive_coins_from_player").replace("%%%s", ChatFormatter.valueOf(manager.getBalance())).replace("%%s", player.getName()).replace("%s", ChatFormatter.valueOf(amount)));
                    return;
                }
                if (sender instanceof ConsoleCommandSender console) {
                    if (isNotDouble(args[1])) {
                        console.sendMessage(TranslatableChatComponent.read("fallbackCommand.double_required").replace("%s", ChatFormatter.valueOf(2)));
                        return;
                    }
                    double amount = Double.parseDouble(args[1]);
                    if (amount < 0) {
                        console.sendMessage(TranslatableChatComponent.read("fallbackCommand.value_too_small").replace("%s", ChatFormatter.valueOf(0)));
                        return;
                    }
                    Player target = new PlayerArgument().parse(args[2]);
                    if (target == null) {
                        console.sendMessage(TranslatableChatComponent.read("fallbackCommand.player_not_recognized").replace("%s", args[2]));
                        return;
                    }
                    double balance = target.getPersistentDataContainer().get(NamespacedKeys.BALANCE.getKey(), PersistentDataType.DOUBLE);

                    if (!Main.getInstance().getBankAccounts().containsKey(target.getUniqueId())) {
                        console.sendMessage(TranslatableChatComponent.read("command.console_executor.target_bank_account_missing").replace("%s", target.getName()));
                        return;
                    }
                    BankManager manager = new BankManager(target, balance + amount);
                    console.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.console_executor.give_coins_to_player").replace("%%%s", ChatFormatter.valueOf(manager.getBalance())).replace("%%s", ChatFormatter.valueOf(amount)).replace("%s", target.getName()));
                    target.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.console_executor.receive_coins_from_console").replace("%%s", ChatFormatter.valueOf(manager.getBalance())).replace("%s", ChatFormatter.valueOf(amount)));
                }
            }
            default -> sender.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_many_arguments").replace("%%s", ChatFormatter.valueOf(3)).replace("%s", ChatFormatter.valueOf(args.length)));
        }
    }
}
