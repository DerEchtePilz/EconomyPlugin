package me.derechtepilz.economy.itemmanager;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ItemStackArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import me.derechtepilz.economy.Main;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemBuyOffer {
    public ItemBuyOffer() {
        new CommandAPICommand("buy")
                .executes((sender, args) -> {
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage("§cYou cannot execute this command!");
                        return;
                    }
                    Main.getInstance().getItemBuyMenu().openBuyMenu(player);
                })
                .register();

        new CommandAPICommand("buy")
                .withArguments(new ItemStackArgument("item"))
                .executes((sender, args) -> {
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage("§cYou cannot execute this command!");
                        return;
                    }
                    ItemStack item = (ItemStack) args[0];
                    Main.getInstance().getItemBuyMenu().openBuyMenu(player, item.getType());
                })
                .register();

        new CommandAPICommand("buy")
                .withArguments(new StringArgument("item").includeSuggestions(info -> new String[]{"special"}))
                .executes((sender, args) -> {
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage("§cYou cannot execute this command!");
                        return;
                    }
                    String item = (String) args[0];
                    if (!item.equals("special")) {
                        player.sendMessage("§cThe argument '" + item + "' is not a valid query!");
                        return;
                    }
                    Main.getInstance().getItemBuyMenu().openBuyMenu(player, true);
                })
                .register();

    }
}
