package me.derechtepilz.economy.economymanager;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.DoubleArgument;
import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.playermanager.permission.Permission;
import me.derechtepilz.economy.utility.Argument;
import me.derechtepilz.economy.utility.ChatFormatter;
import me.derechtepilz.economy.utility.NamespacedKeys;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class GiveCoinsCommand {
    public void register() {
        new CommandTree("givecoins")
                .then(new DoubleArgument("amount", 0)
                        .executesPlayer((player, args) -> {
                            if (!Permission.hasPermission(player, Permission.GIVE_COINS)) {
                                player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                                return;
                            }
                            double amount = (double) args[0];
                            double balance = player.getPersistentDataContainer().get(NamespacedKeys.BALANCE.getKey(), PersistentDataType.DOUBLE);

                            if (!Main.getInstance().getBankAccounts().containsKey(player.getUniqueId())) {
                                player.sendMessage(TranslatableChatComponent.read("command.player_executor.target.bank_account_missing"));
                                return;
                            }
                            BankManager manager = new BankManager(player, amount + balance);
                            player.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.player_executor.give_coins_to_player").replace("%%s", ChatFormatter.valueOf(manager.getBalance())).replace("%s", ChatFormatter.valueOf(amount)));
                        })
                        .then(new Argument<Player>(Argument.ArgumentType.PLAYER_SINGLE).getArgument()
                                .executes((sender, args) -> {
                                    if (sender instanceof Player player) {
                                        if (!Permission.hasPermission(player, Permission.GIVE_COINS)) {
                                            player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                                            return;
                                        }
                                        double amount = (double) args[0];
                                        Player target = (Player) args[1];
                                        double balance = target.getPersistentDataContainer().get(NamespacedKeys.BALANCE.getKey(), PersistentDataType.DOUBLE);

                                        if (!Main.getInstance().getBankAccounts().containsKey(player.getUniqueId())) {
                                            player.sendMessage(TranslatableChatComponent.read("command.player_executor.target.bank_account_missing").replace("%s", target.getName()));
                                            return;
                                        }
                                        BankManager manager = new BankManager(target, balance + amount);
                                        player.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.player_executor.give_coins_to_other_player").replace("%%%s", ChatFormatter.valueOf(manager.getBalance())).replace("%%s", ChatFormatter.valueOf(amount)).replace("%s", target.getName()));
                                        target.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.player_executor.receive_coins_from_player").replace("%%%s", ChatFormatter.valueOf(manager.getBalance())).replace("%%s", player.getName()).replace("%s", ChatFormatter.valueOf(amount)));
                                        return;
                                    }
                                    if (sender instanceof ConsoleCommandSender console) {
                                        double amount = (double) args[0];
                                        Player target = (Player) args[1];
                                        double balance = target.getPersistentDataContainer().get(NamespacedKeys.BALANCE.getKey(), PersistentDataType.DOUBLE);

                                        if (!Main.getInstance().getBankAccounts().containsKey(target.getUniqueId())) {
                                            console.sendMessage(TranslatableChatComponent.read("command.console_executor.target_bank_account_missing").replace("%s", target.getName()));
                                            return;
                                        }
                                        BankManager manager = new BankManager(target, balance + amount);
                                        console.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.console_executor.give_coins_to_player").replace("%%%s", ChatFormatter.valueOf(manager.getBalance())).replace("%%s", ChatFormatter.valueOf(amount)).replace("%s", target.getName()));
                                        target.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.console_executor.receive_coins_from_console").replace("%%s", ChatFormatter.valueOf(manager.getBalance())).replace("%s", ChatFormatter.valueOf(amount)));
                                    }
                                })))
                .register();
    }
}
