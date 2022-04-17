/**
 * MIT License
 *
 * Copyright (c) 2022 DerEchtePilz
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.derechtepilz.economy.economymanager;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import me.derechtepilz.economy.Main;
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
                        player.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.player_executor.give_coins_to_player").replace("%%s", String.valueOf(manager.getBalance())).replace("%s", String.valueOf(amount)));
                    } else {
                        sender.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.wrong_executor"));
                    }
                })
                .register();

        new CommandAPICommand("givecoins")
                .withArguments(playerArgument)
                .withArguments(new DoubleArgument("amount", 0))
                .executes((sender, args) -> {
                    if (sender instanceof Player player) {
                        Player target = (Player) args[0];
                        double amount = (double) args[1];
                        double balance = target.getPersistentDataContainer().get(Main.getInstance().getBalance(), PersistentDataType.DOUBLE);

                        if (!Main.getInstance().getBankAccounts().containsKey(player)) {
                            player.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.player_executor.target_bank_account_missing").replace("%s", target.getName()));
                            return;
                        }
                        BankManager manager = new BankManager(target, balance + amount);
                        player.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.player_executor.give_coins_to_other_player").replace("%%%s", String.valueOf(manager.getBalance())).replace("%%s", String.valueOf(amount)).replace("%s", target.getName()));
                        target.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.player_executor.receive_coins_from_player").replace("%%%s", String.valueOf(manager.getBalance())).replace("%%s", player.getName()) .replace("%s", String.valueOf(amount)));
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
                        console.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.console_executor.give_coins_to_player").replace("%%%s", String.valueOf(manager.getBalance())).replace("%%s", String.valueOf(amount)).replace("%s", target.getName()));
                        target.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.console_executor.receive_coins_from_console").replace("%%s", String.valueOf(manager.getBalance())).replace("%s", String.valueOf(amount)));
                    }
                })
                .register();
    }

    private final Argument playerArgument = new PlayerArgument("player").replaceSuggestions(ArgumentSuggestions.strings((args) -> {
        List<String> players = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            players.add(player.getName());
        }
        return (String[]) players.toArray();
    }));
}
