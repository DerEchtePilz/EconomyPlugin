package me.derechtepilz.economy.bukkitcommands.commands.commandapicommands;

import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.bukkitcommands.arguments.PlayerArgument;
import me.derechtepilz.economy.bukkitcommands.commands.CommandBase;
import me.derechtepilz.economy.economymanager.BankManager;
import me.derechtepilz.economy.playermanager.Permission;
import me.derechtepilz.economy.utility.ChatFormatter;
import me.derechtepilz.economy.utility.RangeValidator;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import me.derechtepilz.economy.utility.config.Config;
import me.derechtepilz.economy.utility.config.ConfigFields;
import me.derechtepilz.economy.utility.exceptions.InvalidRangeException;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class SetCoinsCommandExecutor extends CommandBase {
    public SetCoinsCommandExecutor(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 1, 2 -> sender.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_few_arguments").replace("%%s", ChatFormatter.valueOf(3)).replace("%s", ChatFormatter.valueOf(args.length)));
            case 3 -> {
                if (sender instanceof Player player) {
                    if (!Permission.hasPermission(player, Permission.SET_COINS)) {
                        player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                        return;
                    }
                    Player target = new PlayerArgument().parse(args[1]);
                    if (target == null) {
                        player.sendMessage(TranslatableChatComponent.read("fallbackCommand.player_not_recognized").replace("%s", args[2]));
                        return;
                    }
                    if (isNotDouble(args[2])) {
                        player.sendMessage(TranslatableChatComponent.read("fallbackCommand.double_required").replace("%s", ChatFormatter.valueOf(3)));
                        return;
                    }
                    double amount = Double.parseDouble(args[2]);

                    try {
                        double min = (Double) Config.get(ConfigFields.START_BALANCE);
                        new RangeValidator(min, Integer.MAX_VALUE, amount, "Could not process command because " + ChatFormatter.valueOf(amount) + " was not in the range from " + ChatFormatter.valueOf(min) + " to " + ChatFormatter.valueOf(Integer.MAX_VALUE) + "!");
                    } catch (InvalidRangeException e) {
                        sender.sendMessage(ChatColor.RED + e.getMessage());
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
                    Player target = new PlayerArgument().parse(args[1]);
                    if (target == null) {
                        console.sendMessage(TranslatableChatComponent.read("fallbackCommand.player_not_recognized").replace("%s", args[2]));
                        return;
                    }
                    if (isNotDouble(args[2])) {
                        console.sendMessage(TranslatableChatComponent.read("fallbackCommand.double_required").replace("%s", ChatFormatter.valueOf(3)));
                        return;
                    }
                    double amount = Double.parseDouble(args[2]);

                    try {
                        double min = (Double) Config.get(ConfigFields.START_BALANCE);
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
                }
            }
            case 4 -> sender.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_many_arguments").replace("%%s", ChatFormatter.valueOf(3)).replace("%s", ChatFormatter.valueOf(args.length)));
        }
    }
}
