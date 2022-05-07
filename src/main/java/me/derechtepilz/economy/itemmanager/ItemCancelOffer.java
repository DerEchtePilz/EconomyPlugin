package me.derechtepilz.economy.itemmanager;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.playermanager.Permission;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import org.bukkit.entity.Player;

public class ItemCancelOffer {
    public ItemCancelOffer() {
        new CommandAPICommand("canceloffer")
                .executes((sender, args) -> {
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage(TranslatableChatComponent.read("itemCancelOffer.wrong_executor"));
                        return;
                    }
                    if (!Permission.hasPermission(player, Permission.CANCEL_OFFER)) {
                        player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                        return;
                    }
                    Main.getInstance().getItemCancelMenu().openOfferCancelMenu(player, Main.getInstance().getOfferingPlayers().get(player.getUniqueId()));
                    player.sendMessage(TranslatableChatComponent.read("itemCancelOffer.player_executor.prepare_cancelling"));
                })
                .register();
    }
}
