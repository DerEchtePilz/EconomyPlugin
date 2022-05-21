package me.derechtepilz.economy.bukkitcommands.commands.commandapicommands;

import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.bukkitcommands.commands.CommandBase;
import me.derechtepilz.economy.economymanager.BankManager;
import me.derechtepilz.economy.playermanager.Permission;
import me.derechtepilz.economy.utility.ChatFormatter;
import me.derechtepilz.economy.utility.NamespacedKeys;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class TakeCoinsCommandExecutor extends CommandBase {
    public TakeCoinsCommandExecutor(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(TranslatableChatComponent.read("command.wrong_executor"));
            return;
        }
        switch (args.length) {
            case 1 -> sender.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_few_arguments").replace("%%s", ChatFormatter.valueOf(2)).replace("%s", ChatFormatter.valueOf(args.length)));
            case 2 -> {
                if (!Permission.hasPermission(player, Permission.TAKE_COINS)) {
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
                double playerBalance = player.getPersistentDataContainer().get(NamespacedKeys.BALANCE.getKey(), PersistentDataType.DOUBLE);
                double startBalance = player.getPersistentDataContainer().get(NamespacedKeys.START_BALANCE.getKey(), PersistentDataType.DOUBLE);
                if (amount > playerBalance) {
                    player.sendMessage(TranslatableChatComponent.read("takeCoinsCommand.player_executor.not_enough_coins").replace("%s", ChatFormatter.valueOf(amount)));
                    return;
                }
                if (amount > playerBalance - startBalance) {
                    player.sendMessage(TranslatableChatComponent.read("takeCoinsCommand.player_executor.start_balance_safe"));
                    return;
                }

                BankManager manager = Main.getInstance().getBankAccounts().get(player.getUniqueId());
                manager.setBalance(playerBalance - amount);
                player.sendMessage(TranslatableChatComponent.read("takeCoinsCommand.player_executor.take_coins").replace("%%s", ChatFormatter.valueOf(manager.getBalance())).replace("%s", ChatFormatter.valueOf(amount)));
            }
            default -> sender.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_many_arguments").replace("%%s", ChatFormatter.valueOf(2)).replace("%s", ChatFormatter.valueOf(args.length)));
        }
    }
}
