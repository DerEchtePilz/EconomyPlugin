package me.derechtepilz.economy.bukkitcommands.commands.commandapicommands;

import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.bukkitcommands.arguments.ItemStackArgument;
import me.derechtepilz.economy.bukkitcommands.commands.CommandBase;
import me.derechtepilz.economy.playermanager.Permission;
import me.derechtepilz.economy.utility.ChatFormatter;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BuyCommandExecutor extends CommandBase{
    public BuyCommandExecutor(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(TranslatableChatComponent.read("command.wrong_executor"));
            return;
        }
        switch (args.length) {
            case 1 -> {
                if (!Permission.hasPermission(player, Permission.BUY_OFFER)) {
                    player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                    return;
                }
                Main.getInstance().getItemBuyMenu().openBuyMenu(player);
            }
            case 2 -> {
                if (!Permission.hasPermission(player, Permission.BUY_OFFER)) {
                    player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                    return;
                }
                if (new ItemStackArgument().parse(args[1]) == null) {
                    String item = args[1];
                    if (!item.equals("special") && !item.equals("player")) {
                        player.sendMessage(TranslatableChatComponent.read("itemBuyOffer.wrong_argument").replace("%s", item));
                        return;
                    }
                    Main.getInstance().getItemBuyMenu().openBuyMenu(player, item);
                } else {
                    ItemStack item = new ItemStackArgument().parse(args[1]);
                    Main.getInstance().getItemBuyMenu().openBuyMenu(player, item.getType());
                }
            }
            default -> sender.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_many_arguments").replace("%%s", ChatFormatter.valueOf(2)).replace("%s", ChatFormatter.valueOf(args.length)));
        }
    }
}
