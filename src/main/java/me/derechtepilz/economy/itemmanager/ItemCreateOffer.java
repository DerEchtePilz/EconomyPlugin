package me.derechtepilz.economy.itemmanager;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.ItemStackArgument;
import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemCreateOffer {
    public ItemCreateOffer() {
        new CommandAPICommand("createoffer")
                .withArguments(new ItemStackArgument("item"))
                .withArguments(new IntegerArgument("count", (Integer) Main.getInstance().getConfig().get("itemQuantities.minAmount"), (Integer) Main.getInstance().getConfig().get("itemQuantities.maxAmount")))
                .withArguments(new IntegerArgument("price", (Integer) Main.getInstance().getConfig().get("itemPrice.minAmount"), (Integer) Main.getInstance().getConfig().get("itemPrice.maxAmount")))
                .executes((sender, args) -> {
                    if (sender instanceof Player player) {
                        ItemStack item = (ItemStack) args[0];
                        int amount = (int) args[1];
                        int price = (int) args[2];

                        for (int i = 0; i < player.getInventory().getSize(); i++) {
                            ItemStack playerInventoryItem = player.getInventory().getItem(i);
                            if (playerInventoryItem == null) continue;
                            if (playerInventoryItem.isSimilar(item)) {
                                if (playerInventoryItem.getAmount() > amount) {
                                    playerInventoryItem.setAmount(playerInventoryItem.getAmount() - amount);
                                    player.getInventory().setItem(i, playerInventoryItem);

                                    new ItemConverter(playerInventoryItem, player, price);
                                } else {
                                    player.getInventory().remove(playerInventoryItem);
                                    new ItemConverter(playerInventoryItem, player, price);
                                }
                                return;
                            }
                        }
                        player.sendMessage(TranslatableChatComponent.read("itemCreateOffer.player_executor.missing_item"));
                        return;
                    }
                    if (sender instanceof ConsoleCommandSender console) {
                        ItemStack item = (ItemStack) args[0];
                        int amount = (int) args[1];
                        int price = (int) args[2];
                        item.setAmount(amount);

                        new ItemConverter(item, price);
                        console.sendMessage(TranslatableChatComponent.read("itemCreateOffer.console_executor.console_created_offer").replace("%s", item.getType().name()).replace("%%s", amount + ""));
                        Bukkit.getOnlinePlayers().forEach(p ->
                                p.sendMessage(TranslatableChatComponent.read("itemCreateOffer.console_executor.special_offer_available").replace("%s", item.getType().name()).replace("%%s", amount + "").replace("%%%s", price + ""))
                        );
                        return;
                    }
                    sender.sendMessage(TranslatableChatComponent.read("itemCreateOffer.wrong_executor"));
                })
                .register();
    }
}
