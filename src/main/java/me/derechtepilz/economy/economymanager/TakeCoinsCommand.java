package me.derechtepilz.economy.economymanager;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.DoubleArgument;
import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.playermanager.Permission;
import me.derechtepilz.economy.utility.ChatFormatter;
import me.derechtepilz.economy.utility.NamespacedKeys;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import org.bukkit.persistence.PersistentDataType;

public class TakeCoinsCommand {
    public TakeCoinsCommand() {
        new CommandAPICommand("takecoins").
                withArguments(new DoubleArgument("amount", 0))
                .executesPlayer((player, args) -> {
                    if (!Permission.hasPermission(player, Permission.TAKE_COINS)) {
                        player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                        return;
                    }
                    double amount = (double) args[0];

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
                })
                .register();
    }
}
