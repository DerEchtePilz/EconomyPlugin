package me.derechtepilz.economy.itemmanager;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.ItemStackArgument;
import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.playermanager.Permission;
import me.derechtepilz.economy.utility.ChatFormatter;
import me.derechtepilz.economy.utility.Config;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class ItemCreateOffer {
    public ItemCreateOffer() {
        new CommandAPICommand("createoffer")
                .withArguments(new ItemStackArgument("item"))
                .withArguments(new IntegerArgument("count", (Integer) Config.get("itemQuantities.minAmount"), (Integer) Config.get("itemQuantities.maxAmount")))
                .withArguments(new IntegerArgument("price", (Integer) Config.get("itemPrice.minAmount"), (Integer) Config.get("itemPrice.maxAmount")))
                .executes((sender, args) -> {
                    if (sender instanceof Player player) {
                        if (!Permission.hasPermission(player, Permission.CREATE_OFFER)) {
                            player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                            return;
                        }
                        ItemStack item = (ItemStack) args[0];
                        int amount = (int) args[1];
                        int price = (int) args[2];
                        item.setAmount(amount);

                        for (int i = 0; i < player.getInventory().getSize(); i++) {
                            if (player.getInventory().getItem(i) == null) continue;
                            if (player.getInventory().getItem(i).isSimilar(item)) {
                                if (player.getInventory().getItem(i).getAmount() >= amount) {
                                    ItemUtils.createSalableItem(player.getName(), item, price);
                                    player.getInventory().remove(item);
                                    player.sendMessage(TranslatableChatComponent.read("itemCreateOffer.player_executor.player_created_offer").replace("%%s", ChatFormatter.valueOf(amount)).replace("%s", "minecraft:" + item.getType().name().toLowerCase()));
                                } else {
                                    player.sendMessage(TranslatableChatComponent.read("itemCreateOffer.player_executor.too_few_items").replace("%%s", String.valueOf(amount)).replace("%s", "minecraft:" + item.getType().name().toLowerCase()));
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

                        ItemUtils.createSalableItem("console", item, price);
                        console.sendMessage(TranslatableChatComponent.read("itemCreateOffer.console_executor.console_created_offer").replace("%%s", amount + "").replace("%s", "minecraft:" + item.getType().name().toLowerCase()));
                        Bukkit.getOnlinePlayers().forEach(p ->
                                p.sendMessage(TranslatableChatComponent.read("itemCreateOffer.console_executor.special_offer_available").replace("%%%s", price + "").replace("%%s", amount + "").replace("%s", item.getType().name()))
                        );
                        return;
                    }
                    sender.sendMessage(TranslatableChatComponent.read("itemCreateOffer.wrong_executor"));
                })
                .register();
    }
}
