package me.derechtepilz.economy.itemmanager;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.ItemStackArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemBuyOffer {
    public ItemBuyOffer() {
        new CommandAPICommand("buy")
                .executes((sender, args) -> {
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage(TranslatableChatComponent.read("itemBuyOffer.wrong_executor"));
                        return;
                    }
                    Main.getInstance().getItemBuyMenu().openBuyMenu(player);
                })
                .register();

        new CommandAPICommand("buy")
                .withArguments(new ItemStackArgument("item"))
                .executes((sender, args) -> {
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage(TranslatableChatComponent.read("itemBuyOffer.wrong_executor"));
                        return;
                    }
                    ItemStack item = (ItemStack) args[0];
                    Main.getInstance().getItemBuyMenu().openBuyMenu(player, item.getType());
                })
                .register();

        new CommandAPICommand("buy")
                .withArguments(new StringArgument("query").includeSuggestions(ArgumentSuggestions.strings(info -> new String[]{"special"})))
                .executes((sender, args) -> {
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage(TranslatableChatComponent.read("itemBuyOffer.wrong_executor"));
                        return;
                    }
                    String item = (String) args[0];
                    if (!item.equals("special")) {
                        player.sendMessage(TranslatableChatComponent.read("itemBuyOffer.wrong_argument").replace("%s", item));
                        return;
                    }
                    Main.getInstance().getItemBuyMenu().openBuyMenu(player, true);
                })
                .register();

    }
}
