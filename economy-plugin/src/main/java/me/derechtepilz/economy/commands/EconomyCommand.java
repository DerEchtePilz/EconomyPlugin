package me.derechtepilz.economy.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.*;
import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.inventorymanagement.InventoryHandler;
import me.derechtepilz.economy.itemmanagement.Item;
import me.derechtepilz.economy.utility.DataHandler;
import me.derechtepilz.economy.utility.NamespacedKeys;
import me.derechtepilz.economycore.EconomyAPI;
import me.derechtepilz.economycore.exceptions.BalanceException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class EconomyCommand {

    private final Main main;

    public EconomyCommand(Main main) {
        this.main = main;
    }

    public void register() {
        new CommandTree("economy")
                .then(new LiteralArgument("offers")
                        .then(new LiteralArgument("buy")
                                .executesPlayer((player, args) -> {
                                    if (!main.getInventoryHandler().isTimerRunning()) {
                                        player.sendMessage("§cThe auctions are currently paused. Try again later!");
                                        return;
                                    }
                                    DataHandler.setBuyMenuData(player);
                                    player.sendMessage("§aYou opened the buy menu!");
                                })
                        )
                        .then(new LiteralArgument("create")
                                .then(new ItemStackArgument("item")
                                        .then(new IntegerArgument("amount", 1)
                                                .then(new DoubleArgument("price", 1)
                                                        .then(new IntegerArgument("hours", 0)
                                                                .then(new IntegerArgument("minutes", 0)
                                                                        .then(new IntegerArgument("seconds", 0)
                                                                                .executesPlayer((player, args) -> {
                                                                                    ItemStack item = (ItemStack) args[0];
                                                                                    int amount = (int) args[1];
                                                                                    double price = (double) args[2];
                                                                                    int duration = (int) args[3] * 60 * 60 + (int) args[4] * 60 + (int) args[5];
                                                                                    item.setAmount(amount);

                                                                                    for (int i = 0; i < player.getInventory().getSize(); i++) {
                                                                                        if (player.getInventory().getItem(i) == null) continue;

                                                                                        ItemStack currentItem = player.getInventory().getItem(i);
                                                                                        assert currentItem != null;
                                                                                        if (currentItem.isSimilar(item)) {
                                                                                            if (currentItem.getAmount() >= amount) {
                                                                                                Item offer = new Item(main, item.getType(), amount, price, player.getUniqueId(), duration);
                                                                                                offer.register();

                                                                                                ItemStack updatedItem = currentItem;
                                                                                                updatedItem.setAmount(updatedItem.getAmount() - item.getAmount());
                                                                                                player.getInventory().setItem(i, updatedItem);

                                                                                                player.sendMessage("§aYou created a new offer for §6" + amount + " §aitems of type §6minecraft:" + item.getType().name().toLowerCase() + "§a! It will last §6" + duration + " §aseconds!");
                                                                                            } else {
                                                                                                player.sendMessage("§cYou have to few items of type §6minecraft:" + item.getType().name().toLowerCase() + " §cin your inventory to offer §6" + amount + " §citems!");
                                                                                            }
                                                                                            return;
                                                                                        }
                                                                                    }
                                                                                    player.sendMessage("§cYou do not have the specified item in your inventory.");
                                                                                })
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                        .then(new LiteralArgument("cancel")
                                .executesPlayer((player, args) -> {

                                })
                        )
                        .then(new LiteralArgument("pause")
                                .withRequirement(sender -> sender.hasPermission("economy.pause.auction"))
                                .executesPlayer((player, args) -> {
                                    if (!main.getInventoryHandler().isTimerRunning()) {
                                        player.sendMessage("§cThe auctions are already paused!");
                                        return;
                                    }
                                    main.getInventoryHandler().setTimerRunning(false);
                                    Bukkit.broadcastMessage("§cThe auctions are now paused!");
                                })
                        )
                        .then(new LiteralArgument("resume")
                                .withRequirement(sender -> sender.hasPermission("economy.pause.auction"))
                                .executesPlayer((player, args) -> {
                                    if (main.getInventoryHandler().isTimerRunning()) {
                                        player.sendMessage("§cThe auctions are already running!");
                                        return;
                                    }
                                    main.getInventoryHandler().setTimerRunning(true);
                                    Bukkit.broadcastMessage("§aThe auctions are now running!");
                                })
                        )
                )
                .then(new LiteralArgument("coins")
                        .then(new LiteralArgument("give")
                                .withRequirement(sender -> sender.hasPermission("economy.coins.give"))
                                .then(new PlayerArgument("target")
                                        .then(new DoubleArgument("amount", 0)
                                                .executesPlayer((player, args) -> {
                                                    try {
                                                        Player target = (Player) args[0];
                                                        double amount = (double) args[1];
                                                        boolean success = EconomyAPI.addCoinsToBalance(target, amount);

                                                        if (success) {
                                                            player.sendMessage("§aYou gave §b" + target.getName() + " §6" + amount + " §acoins!");
                                                            target.sendMessage("§aYou were given §6" + amount + " §acoins!");
                                                            return;
                                                        }
                                                        player.sendMessage("§cSomething went wrong! Maybe tell §b" + target.getName() + " §cto re-join the server!");
                                                    } catch (BalanceException exception) {
                                                        player.sendMessage(exception.getMessage());
                                                    }
                                                })
                                        )
                                )
                        )
                        .then(new LiteralArgument("take")
                                .withRequirement(sender -> sender.hasPermission("economy.coins.take"))
                                .then(new PlayerArgument("target")
                                        .then(new DoubleArgument("amount", 0)
                                                .executesPlayer((player, args) -> {
                                                    try {
                                                        Player target = (Player) args[0];
                                                        double amount = (double) args[1];
                                                        boolean success = EconomyAPI.removeCoinsFromBalance(target, amount);

                                                        if (success) {
                                                            player.sendMessage("§aYou took §6" + amount + " §acoins from §b" + target.getName() + "§a!");
                                                            target.sendMessage("§aYou have been taken §6" + amount + " §acoins!");
                                                            return;
                                                        }
                                                        player.sendMessage("§cSomething went wrong! Maybe tell §b" + target.getName() + " §cto re-join the server!");
                                                    } catch (BalanceException exception) {
                                                        player.sendMessage(exception.getMessage());
                                                    }
                                                })
                                        )
                                )
                        )
                        .then(new LiteralArgument("set")
                                .withRequirement(sender -> sender.hasPermission("economy.coins.set"))
                                .then(new PlayerArgument("target")
                                        .then(new DoubleArgument("amount", 0)
                                                .executesPlayer((player, args) -> {
                                                    try {
                                                        Player target = (Player) args[0];
                                                        double amount = (double) args[1];
                                                        boolean success = EconomyAPI.setBalance(target, amount);

                                                        if (success) {
                                                            player.sendMessage("§aYou set §b" + target.getName() + "§a's balance to §6" + amount + " §acoins!");
                                                            target.sendMessage("§aYour balance has been set to §6" + amount + " §acoins!");
                                                            return;
                                                        }
                                                        player.sendMessage("§cSomething went wrong! Maybe tell §b" + target.getName() + " §cto re-join the server!");
                                                    } catch (BalanceException exception) {
                                                        player.sendMessage(exception.getMessage());
                                                    }
                                                })
                                        )
                                )
                        )
                )
                .then(new LiteralArgument("permission")
                        .then(new LiteralArgument("group")
                                .then(new LiteralArgument("set")
                                        .withRequirement(sender -> sender.hasPermission("economy.manage.permissions"))
                                        .executesPlayer((player, args) -> {

                                        })
                                )
                                .then(new LiteralArgument("remove")
                                        .withRequirement(sender -> sender.hasPermission("economy.manage.permissions"))
                                        .executesPlayer((player, args) -> {

                                        })
                                )
                                .then(new LiteralArgument("get")
                                        .withRequirement(sender -> sender.hasPermission("economy.manage.permissions"))
                                        .executesPlayer((player, args) -> {

                                        })
                                )
                        )
                        .then(new LiteralArgument("single")
                                .then(new LiteralArgument("set")
                                        .withRequirement(sender -> sender.hasPermission("economy.manage.permissions"))
                                        .executesPlayer((player, args) -> {

                                        })
                                )
                                .then(new LiteralArgument("remove")
                                        .withRequirement(sender -> sender.hasPermission("economy.manage.permissions"))
                                        .executesPlayer((player, args) -> {

                                        })
                                )
                                .then(new LiteralArgument("get")
                                        .withRequirement(sender -> sender.hasPermission("economy.manage.permissions"))
                                        .executesPlayer((player, args) -> {

                                        })
                                )
                        )
                )
                .then(new LiteralArgument("config")
                        .then(new LiteralArgument("startBalance")
                                .then(new DoubleArgument("startBalance")
                                        .withRequirement(sender -> sender.hasPermission("economy.config.startbalance"))
                                        .executesPlayer((player, args) -> {
                                            double newStartBalance = (double) args[0];
                                            EconomyAPI.setStartBalance(newStartBalance);
                                            player.sendMessage("§aYou set the new start balance to §6" + newStartBalance + "§a!");
                                        })
                                )
                        )
                        .then(new LiteralArgument("interest")
                                .then(new DoubleArgument("interest")
                                        .withRequirement(sender -> sender.hasPermission("economy.config.interest"))
                                        .executesPlayer((player, args) -> {
                                            double newInterest = (double) args[0];
                                            EconomyAPI.setInterest(newInterest);
                                            player.sendMessage("§aYou set the new interest rate to §6" + newInterest + "§a!");
                                        })
                                )
                        )
                )
                .then(new LiteralArgument("trade")
                        .executesPlayer((player, args) -> {

                        })
                )
                .register();
    }
}
