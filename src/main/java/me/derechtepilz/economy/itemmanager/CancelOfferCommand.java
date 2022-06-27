package me.derechtepilz.economy.itemmanager;

import dev.jorel.commandapi.CommandTree;
import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.playermanager.permission.Permission;
import me.derechtepilz.economy.utility.TranslatableChatComponent;

public class CancelOfferCommand {
    public void register() {
        new CommandTree("canceloffer")
                .executesPlayer((player, args) -> {
                    if (!Permission.hasPermission(player, Permission.CANCEL_OFFER)) {
                        player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                        return;
                    }
                    if (Main.getInstance().getPlayerOffers().get(player.getUniqueId()) == null) {
                        player.sendMessage(TranslatableChatComponent.read("itemCancelOffer.no_offers_available"));
                        return;
                    }
                    if (Main.getInstance().getPlayerOffers().get(player.getUniqueId()).length == 0) {
                        player.sendMessage(TranslatableChatComponent.read("itemCancelOffer.no_offers_available"));
                        return;
                    }
                    Main.getInstance().getItemCancelMenu().openOfferCancelMenu(player, Main.getInstance().getPlayerOffers().get(player.getUniqueId()));
                    player.sendMessage(TranslatableChatComponent.read("itemCancelOffer.player_executor.prepare_cancelling"));
                })
                .register();
    }
}
