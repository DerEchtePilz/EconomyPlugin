package me.derechtepilz.economy.economymanager;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.DoubleArgument;
import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.playermanager.permission.Permission;
import me.derechtepilz.economy.utility.Argument;
import me.derechtepilz.economy.utility.ChatFormatter;
import me.derechtepilz.economy.utility.RangeValidator;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import me.derechtepilz.economy.utility.config.Config;
import me.derechtepilz.economy.utility.exceptions.InvalidRangeException;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class SetCoinsCommand {
    public void register() {
        new CommandTree("setcoins")
                .then(new Argument<Player>(Argument.ArgumentType.PLAYER_SINGLE).getArgument()
                        .then(new DoubleArgument("amount")
                                .executes((sender, args) -> {
                                    if (sender instanceof Player player) {
                                        if (!Permission.hasPermission(player, Permission.SET_COINS)) {
                                            player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                                            return;
                                        }
                                        Player target = (Player) args[0];
                                        double amount = (double) args[1];

                                        try {
                                            double min = Double.parseDouble(Config.get("startBalance"));
                                            new RangeValidator(min, Integer.MAX_VALUE, amount, "Could not process command because " + ChatFormatter.valueOf(amount) + " was not in the range from " + ChatFormatter.valueOf(min) + " to " + ChatFormatter.valueOf(Integer.MAX_VALUE) + "!");
                                        } catch (InvalidRangeException e) {
                                            sender.sendMessage(ChatColor.RED + e.getMessage());
                                            return;
                                        }

                                        if (target.equals(player)) {
                                            if (!Main.getInstance().getBankAccounts().containsKey(player.getUniqueId())) {
                                                player.sendMessage(TranslatableChatComponent.read("command.self.bank_account_missing"));
                                                return;
                                            }
                                        } else {
                                            player.sendMessage(TranslatableChatComponent.read("command.player_executor.target.bank_account_missing").replace("%s", target.getName()));
                                            return;
                                        }
                                        BankManager manager = Main.getInstance().getBankAccounts().get(player.getUniqueId());
                                        manager.setBalance(amount);

                                        if (target.equals(player)) {
                                            player.sendMessage(TranslatableChatComponent.read("setCoinsCommand.balance_set").replace("%s", ChatFormatter.valueOf(manager.getBalance())));
                                        } else {
                                            player.sendMessage(TranslatableChatComponent.read("setCoinsCommand.player_balance_set").replace("%%s", ChatFormatter.valueOf(manager.getBalance())).replace("%s", target.getName()));
                                            target.sendMessage(TranslatableChatComponent.read("setCoinsCommand.balance_set").replace("%s", ChatFormatter.valueOf(manager.getBalance())));
                                        }
                                        return;
                                    }
                                    if (sender instanceof ConsoleCommandSender console) {
                                        Player target = (Player) args[0];
                                        double amount = (double) args[1];

                                        try {
                                            double min = Double.parseDouble(Config.get("startBalance"));
                                            new RangeValidator(min, Integer.MAX_VALUE, amount, "Could not process command because " + ChatFormatter.valueOf(amount) + " was not in the range from " + ChatFormatter.valueOf(min) + " to " + ChatFormatter.valueOf(Integer.MAX_VALUE) + "!");
                                        } catch (InvalidRangeException e) {
                                            sender.sendMessage(ChatColor.RED + e.getMessage());
                                        }

                                        if (!Main.getInstance().getBankAccounts().containsKey(target.getUniqueId())) {
                                            console.sendMessage(TranslatableChatComponent.read("command.console_executor.target.bank_account_missing").replace("%s", target.getName()));
                                            return;
                                        }
                                        BankManager manager = Main.getInstance().getBankAccounts().get(target.getUniqueId());
                                        manager.setBalance(amount);

                                        console.sendMessage(TranslatableChatComponent.read("setCoinsCommand.player_balance_set").replace("%%s", ChatFormatter.valueOf(manager.getBalance())).replace("%s", target.getName()));
                                        target.sendMessage(TranslatableChatComponent.read("setCoinsCommand.balance_set").replace("%s", ChatFormatter.valueOf(manager.getBalance())));
                                        return;
                                    }
                                    sender.sendMessage(TranslatableChatComponent.read("command.wrong_executor"));
                                })))
                .register();
    }
}
