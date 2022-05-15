package me.derechtepilz.economy.economymanager;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.playermanager.Permission;
import me.derechtepilz.economy.utility.ChatFormatter;
import me.derechtepilz.economy.utility.Config;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SetCoinsCommand {
    public SetCoinsCommand() {
        new CommandAPICommand("setcoins")
                .withArguments(playerArgument)
                .withArguments(new DoubleArgument("amount", (Double) Config.get("startBalance")))
                .executes((sender, args) -> {
                    if (sender instanceof Player player) {
                        if (!Permission.hasPermission(player, Permission.SET_COINS)) {
                            player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                            return;
                        }
                        Player target = (Player) args[0];
                        double amount = (double) args[1];
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
                })
                .register();
    }

    private final Argument playerArgument = new PlayerArgument("player").replaceSuggestions(ArgumentSuggestions.strings(getPlayers()));

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
