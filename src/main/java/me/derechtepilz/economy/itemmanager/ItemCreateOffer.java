package me.derechtepilz.economy.itemmanager;

import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.ItemStackArgument;
import me.derechtepilz.economy.playermanager.Permission;
import me.derechtepilz.economy.utility.ChatFormatter;
import me.derechtepilz.economy.utility.RangeValidator;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import me.derechtepilz.economy.utility.config.Config;
import me.derechtepilz.economy.utility.exceptions.InvalidRangeException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemCreateOffer {
    public ItemCreateOffer() {
        new CommandTree("createoffer")
                .withPermission(CommandPermission.NONE)
                .then(new ItemStackArgument("item")
                        .then(new IntegerArgument("count")
                                .then(new IntegerArgument("price")
                                        .executes((sender, args) -> {
                                            if (sender instanceof Player player) {
                                                if (!Permission.hasPermission(player, Permission.CREATE_OFFER)) {
                                                    player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                                                    return;
                                                }
                                                ItemStack item = (ItemStack) args[0];
                                                int amount = (int) args[1];
                                                int price = (int) args[2];

                                                try {
                                                    int itemQuantitiesMinAmount = Integer.parseInt(Config.get("itemQuantitiesMinAmount"));
                                                    int itemQuantitiesMaxAmount = Integer.parseInt(Config.get("itemQuantitiesMaxAmount"));
                                                    new RangeValidator(itemQuantitiesMinAmount, itemQuantitiesMaxAmount, amount, "Could not process command because " + ChatFormatter.valueOf(amount) + " was not in the range from " + ChatFormatter.valueOf(itemQuantitiesMinAmount) + " to " + ChatFormatter.valueOf(itemQuantitiesMaxAmount) + "!");

                                                    int itemPriceMinAmount = Integer.parseInt(Config.get("itemPriceMinAmount"));
                                                    int itemPriceMaxAmount = Integer.parseInt(Config.get("itemPriceMaxAmount"));
                                                    new RangeValidator(itemPriceMinAmount, itemPriceMaxAmount, price, "Could not process command because " + ChatFormatter.valueOf(price) + " was not in the range from " + ChatFormatter.valueOf(itemPriceMinAmount) + " to " + ChatFormatter.valueOf(itemPriceMaxAmount) + "!");
                                                } catch (InvalidRangeException e) {
                                                    player.sendMessage(ChatColor.RED + e.getMessage());
                                                    return;
                                                }

                                                item.setAmount(amount);

                                                for (int i = 0; i < player.getInventory().getSize(); i++) {
                                                    if (player.getInventory().getItem(i) == null) continue;
                                                    if (player.getInventory().getItem(i).isSimilar(item)) {
                                                        if (player.getInventory().getItem(i).getAmount() >= amount) {
                                                            ItemUtils.createSalableItem(player.getName(), item, price);

                                                            ItemStack updatedItem = player.getInventory().getItem(i);
                                                            updatedItem.setAmount(updatedItem.getAmount() - item.getAmount());
                                                            player.getInventory().setItem(i, updatedItem);

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

                                                try {
                                                    int itemQuantitiesMinAmount = Integer.parseInt(Config.get("itemQuantitiesMinAmount"));
                                                    int itemQuantitiesMaxAmount = Integer.parseInt(Config.get("itemQuantitiesMaxAmount"));
                                                    new RangeValidator(itemQuantitiesMinAmount, itemQuantitiesMaxAmount, amount, "Could not process command because " + ChatFormatter.valueOf(amount) + " was not in the range from " + ChatFormatter.valueOf(itemQuantitiesMinAmount) + " to " + ChatFormatter.valueOf(itemQuantitiesMaxAmount) + "!");
                                                    
                                                    int itemPriceMinAmount = Integer.parseInt(Config.get("itemPriceMinAmount"));
                                                    int itemPriceMaxAmount = Integer.parseInt(Config.get("itemPriceMaxAmount"));
                                                    new RangeValidator(itemPriceMinAmount, itemPriceMaxAmount, price, "Could not process command because " + ChatFormatter.valueOf(price) + " was not in the range from " + ChatFormatter.valueOf(itemPriceMinAmount) + " to " + ChatFormatter.valueOf(itemPriceMaxAmount) + "!");
                                                } catch (InvalidRangeException e) {
                                                    console.sendMessage(ChatColor.RED + e.getMessage());
                                                    return;
                                                }

                                                item.setAmount(amount);

                                                ItemUtils.createSalableItem("console", item, price);
                                                console.sendMessage(TranslatableChatComponent.read("itemCreateOffer.console_executor.console_created_offer").replace("%%s", amount + "").replace("%s", "minecraft:" + item.getType().name().toLowerCase()));
                                                Bukkit.getOnlinePlayers().forEach(p ->
                                                        p.sendMessage(TranslatableChatComponent.read("itemCreateOffer.console_executor.special_offer_available").replace("%%%s", price + "").replace("%%s", amount + "").replace("%s", item.getType().name()))
                                                );
                                                return;
                                            }
                                            sender.sendMessage(TranslatableChatComponent.read("itemCreateOffer.wrong_executor"));
                                        }))))
                .register();
    }
}
