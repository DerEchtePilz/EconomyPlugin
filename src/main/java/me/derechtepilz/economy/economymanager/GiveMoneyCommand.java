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
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class GiveMoneyCommand {
    public GiveMoneyCommand(String commandName) {
        new CommandAPICommand(commandName)
                .withArguments(new DoubleArgument("amount"))
                .executes((sender, args) -> {
                    if (sender instanceof Player player) {
                        double amount = (double) args[0];
                        double balance;
                        if (player.getPersistentDataContainer().has(Main.getInstance().getBalance(), PersistentDataType.DOUBLE)) {
                            balance = player.getPersistentDataContainer().get(Main.getInstance().getBalance(), PersistentDataType.DOUBLE);
                        } else {
                            balance = 0.0D;
                        }
                        BankManager manager = new BankManager(player, amount + balance);
                        player.sendMessage(TranslatableChatComponent.read("giveMoneyCommand.player_executor.give_coins_to_player").replace("%s", String.valueOf(amount)).replace("%%s", String.valueOf(manager.getBalance())));
                        return;
                    }
                    if (sender instanceof ConsoleCommandSender console) {
                        console.sendMessage("");
                    }
                })
                .register();
    }
}
