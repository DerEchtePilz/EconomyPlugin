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