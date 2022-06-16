package me.derechtepilz.economy.economymanager;

import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.DoubleArgument;
import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.playermanager.Permission;
import me.derechtepilz.economy.utility.Argument;
import me.derechtepilz.economy.utility.ChatFormatter;
import me.derechtepilz.economy.utility.NamespacedKeys;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class TakeCoinsCommand {
    public TakeCoinsCommand() {
        new CommandTree("takecoins")
                .then(new DoubleArgument("amount", 0)
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
                        .then(new Argument<Player>(Argument.ArgumentType.ONE_PLAYER).getArgument()
                                .executesPlayer((player, args) -> {
                                    if (!Permission.hasPermission(player, Permission.TAKE_COINS)) {
                                        player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                                        return;
                                    }
                                    double amount = (double) args[0];
                                    Player target = (Player) args[1];

                                    if (!Main.getInstance().getBankAccounts().containsKey(target.getUniqueId())) {
                                        player.sendMessage(TranslatableChatComponent.read("command.player_executor.target.bank_account_missing").replace("%s", target.getName()));
                                        return;
                                    }

                                    double targetBalance = target.getPersistentDataContainer().get(NamespacedKeys.BALANCE.getKey(), PersistentDataType.DOUBLE);
                                    double startBalance = target.getPersistentDataContainer().get(NamespacedKeys.START_BALANCE.getKey(), PersistentDataType.DOUBLE);

                                    if (amount > targetBalance) {
                                        player.sendMessage(TranslatableChatComponent.read("takeCoinsCommand.player_executor.not_enough_coins_at_target").replace("%%s", ChatFormatter.valueOf(amount)).replace("%s", target.getName()));
                                        return;
                                    }

                                    if (amount > targetBalance - startBalance) {
                                        player.sendMessage(TranslatableChatComponent.read("takeCoinsCommand.player_executor.target_start_balance_safe").replace("%%s", target.getName()).replace("%s", ChatFormatter.valueOf(amount)));
                                        return;
                                    }

                                    BankManager bankManager = Main.getInstance().getBankAccounts().get(target.getUniqueId());
                                    bankManager.setBalance(targetBalance - amount);
                                    player.sendMessage(TranslatableChatComponent.read("takeCoinsCommand.player_executor.take_coins_from_target").replace("%%%s", ChatFormatter.valueOf(bankManager.getBalance())).replace("%%s", target.getName()).replace("%s", ChatFormatter.valueOf(amount)));
                                    target.sendMessage(TranslatableChatComponent.read("takeCoinsCommand.player_executor.take_coins").replace("%%s", ChatFormatter.valueOf(bankManager.getBalance())).replace("%s", ChatFormatter.valueOf(amount)));
                                })))
                .register();
    }

    private String[] getPlayers() {
        List<String> players = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            players.add(player.getName());
        }
        String[] suggestions = new String[players.size()];
        for (int i = 0; i < players.size(); i++) {
            suggestions[i] = players.get(i);
        }
        return suggestions;
    }
}
