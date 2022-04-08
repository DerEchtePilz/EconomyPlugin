package me.derechtepilz.economy.itemmanager;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import me.derechtepilz.economy.Main;

public class ItemCancelOffer {
    public ItemCancelOffer() {
        new CommandAPICommand("canceloffer")
                .executesPlayer((player, args) -> {
                    Main.getInstance().getItemCancelMenu().openOfferCancelMenu(player, Main.getInstance().getOfferingPlayers().get(player.getUniqueId()));
                    player.sendMessage("§aCancel offers in the menu!");
                })
                .register();
    }
}
