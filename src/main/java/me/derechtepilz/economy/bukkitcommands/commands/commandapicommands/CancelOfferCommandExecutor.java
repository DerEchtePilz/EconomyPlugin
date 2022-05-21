package me.derechtepilz.economy.bukkitcommands.commands.commandapicommands;

import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.bukkitcommands.commands.CommandBase;
import me.derechtepilz.economy.playermanager.Permission;
import me.derechtepilz.economy.utility.ChatFormatter;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CancelOfferCommandExecutor extends CommandBase {
    public CancelOfferCommandExecutor(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(TranslatableChatComponent.read("command.wrong_executor"));
            return;
        }
        if (args.length == 1) {
            if (!Permission.hasPermission(player, Permission.CANCEL_OFFER)) {
                player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                return;
            }
            if (Main.getInstance().getPlayerOffers().get(player.getUniqueId()) == null) {
                player.sendMessage(TranslatableChatComponent.read("itemCancelOffer.no_offers_available"));
                return;
            }
            Main.getInstance().getItemCancelMenu().openOfferCancelMenu(player, Main.getInstance().getPlayerOffers().get(player.getUniqueId()));
            player.sendMessage(TranslatableChatComponent.read("itemCancelOffer.player_executor.prepare_cancelling"));
        } else {
            sender.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_many_arguments").replace("%%s", ChatFormatter.valueOf(1)).replace("%s", ChatFormatter.valueOf(args.length)));
        }
    }
}
