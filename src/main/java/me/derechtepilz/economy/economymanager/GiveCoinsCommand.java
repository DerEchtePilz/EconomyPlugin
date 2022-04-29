package me.derechtepilz.economy.economymanager;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.utility.ChatFormatter;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class GiveCoinsCommand {
    public GiveCoinsCommand() {
        new CommandAPICommand("givecoins")
                .withArguments(new DoubleArgument("amount", 0))
                .executes((sender, args) -> {
                    if (sender instanceof Player player) {
                        double amount = (double) args[0];
                        double balance = player.getPersistentDataContainer().get(Main.getInstance().getBalance(), PersistentDataType.DOUBLE);

                        if (!Main.getInstance().getBankAccounts().containsKey(player)) {
                            player.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.player_executor.bank_account_missing"));
                            return;
                        }
                        BankManager manager = new BankManager(player, amount + balance);
                        player.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.player_executor.give_coins_to_player").replace("%%s", ChatFormatter.valueOf(manager.getBalance())).replace("%s", ChatFormatter.valueOf(amount)));
                    } else {
                        sender.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.wrong_executor"));
                    }
                })
                .register();

        new CommandAPICommand("givecoins")
                .withArguments(new DoubleArgument("amount", 0))
                .withArguments(playerArgument)
                .executes((sender, args) -> {
                    if (sender instanceof Player player) {
                        double amount = (double) args[0];
                        Player target = (Player) args[1];
                        double balance = target.getPersistentDataContainer().get(Main.getInstance().getBalance(), PersistentDataType.DOUBLE);

                        if (!Main.getInstance().getBankAccounts().containsKey(player)) {
                            player.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.player_executor.target_bank_account_missing").replace("%s", target.getName()));
                            return;
                        }
                        BankManager manager = new BankManager(target, balance + amount);
                        player.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.player_executor.give_coins_to_other_player").replace("%%%s", ChatFormatter.valueOf(manager.getBalance())).replace("%%s", ChatFormatter.valueOf(amount)).replace("%s", target.getName()));
                        target.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.player_executor.receive_coins_from_player").replace("%%%s", ChatFormatter.valueOf(manager.getBalance())).replace("%%s", player.getName()) .replace("%s", ChatFormatter.valueOf(amount)));
                        return;
                    }
                    if (sender instanceof ConsoleCommandSender console) {
                        Player target = (Player) args[0];
                        double amount = (double) args[1];
                        double balance = target.getPersistentDataContainer().get(Main.getInstance().getBalance(), PersistentDataType.DOUBLE);

                        if (!Main.getInstance().getBankAccounts().containsKey(target)) {
                            console.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.console_executor.target_bank_account_missing").replace("%s", target.getName()));
                            return;
                        }
                        BankManager manager = new BankManager(target, balance + amount);
                        console.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.console_executor.give_coins_to_player").replace("%%%s", ChatFormatter.valueOf(manager.getBalance())).replace("%%s", ChatFormatter.valueOf(amount)).replace("%s", target.getName()));
                        target.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.console_executor.receive_coins_from_console").replace("%%s", ChatFormatter.valueOf(manager.getBalance())).replace("%s", ChatFormatter.valueOf(amount)));
                    }
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
