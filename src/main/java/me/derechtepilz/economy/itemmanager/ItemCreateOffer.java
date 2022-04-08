package me.derechtepilz.economy.itemmanager;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.ItemStackArgument;
import me.derechtepilz.economy.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
                        player.sendMessage("§cYou do not have the specified item in your inventory!");
                        return;
                    }
                    if (sender instanceof ConsoleCommandSender console) {
                        ItemStack item = (ItemStack) args[0];
                        int amount = (int) args[1];
                        int price = (int) args[2];
                        item.setAmount(amount);

                        new ItemConverter(item, price);
                        console.sendMessage(ChatColor.GREEN + "You created a special offer for item " + ChatColor.GOLD + item.getType().name() + " " + ChatColor.GRAY + amount + "x" + ChatColor.GREEN + "!");
                        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage("§aA special offer was created: §6" + item.getType().name() + "§7x" + amount + " §e(Cost: " + price + " coins)§a!"));
                        return;
                    }
                    sender.sendMessage(ChatColor.RED + "You cannot execute this command!");
                })
                .register();
    }
}
