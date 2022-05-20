package me.derechtepilz.economy.itemmanager;

import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.ItemStackArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.playermanager.Permission;
import me.derechtepilz.economy.utility.TranslatableChatComponent;

public class ItemBuyOffer {
    public ItemBuyOffer() {
        new CommandTree("buy")
                .withPermission(CommandPermission.NONE)
                .executesPlayer((player, args) -> {
                    if (!Permission.hasPermission(player, Permission.BUY_OFFER)) {
                        player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                        return;
                    }
                    Main.getInstance().getItemBuyMenu().openBuyMenu(player);
                })
                .then(new ItemStackArgument("item")
                        .executesPlayer((player, args) -> {
                            if (!Permission.hasPermission(player, Permission.BUY_OFFER)) {
                                player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                                return;
                            }
                            Main.getInstance().getItemBuyMenu().openBuyMenu(player);
                        }))
                .then(new StringArgument("query").includeSuggestions(ArgumentSuggestions.strings(info -> new String[]{"special", "player"}))
                        .executesPlayer((player, args) -> {
                            if (!Permission.hasPermission(player, Permission.BUY_OFFER)) {
                                player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                                return;
                            }
                            String item = (String) args[0];
                            if (!item.equals("special") && !item.equals("player")) {
                                player.sendMessage(TranslatableChatComponent.read("itemBuyOffer.wrong_argument").replace("%s", item));
                                return;
                            }
                            Main.getInstance().getItemBuyMenu().openBuyMenu(player, item);
                        }))
                .register();
    }
}
