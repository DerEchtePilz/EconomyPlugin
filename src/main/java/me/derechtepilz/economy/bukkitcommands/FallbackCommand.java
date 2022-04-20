package me.derechtepilz.economy.bukkitcommands;

import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class FallbackCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (Main.getInstance().isWasCommandAPILoaded()) {
            return false;
        }
        if (sender instanceof Player player) {
            if (args.length == 1) {
                switch (args[0].toLowerCase()) {
                    case "canceloffer" -> {
                        Main.getInstance().getItemCancelMenu().openOfferCancelMenu(player, Main.getInstance().getOfferingPlayers().get(player.getUniqueId()));
                        player.sendMessage(TranslatableChatComponent.read("itemCancelOffer.player_executor.prepare_cancelling"));
                        return false;
                    }
                    case "buy" -> {
                        Main.getInstance().getItemBuyMenu().openBuyMenu(player);
                        return false;
                    }
                }
                return false;
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("canceloffer")) {
                    player.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_many_arguments").replace("%%s", "1").replace("%s", String.valueOf(args.length)));
                    return false;
                }
                if (args[0].equalsIgnoreCase("buy")) {
                    if (args[1].equalsIgnoreCase("special")) {
                        Main.getInstance().getItemBuyMenu().openBuyMenu(player, true);
                        return false;
                    }

                }
            }
            return false;
        }
        if (sender instanceof ConsoleCommandSender console) {
            switch (args.length) {
                case 1 -> {
                    switch (args[0].toLowerCase()) {
                        case "canceloffer" -> {
                            sender.sendMessage(TranslatableChatComponent.read("itemCancelOffer.wrong_executor"));
                            return false;
                        }
                    }
                    return false;
                }
            }
            return false;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args[0].equalsIgnoreCase("createoffer")) {
            return ItemParser.suggests(SuggestType.ITEM, args[1]);
        }
        if (args[0].equalsIgnoreCase("buy")) {
            return ItemParser.suggests(SuggestType.ITEM, args[1]);
        }
        return null;
    }
}
