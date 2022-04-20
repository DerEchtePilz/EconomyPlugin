package me.derechtepilz.economy.utility;

import me.derechtepilz.economy.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FallbackCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!Main.getInstance().isWasCommandAPILoaded()) {
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
        }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}
