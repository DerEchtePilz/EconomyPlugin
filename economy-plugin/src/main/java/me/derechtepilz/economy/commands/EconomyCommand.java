package me.derechtepilz.economy.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.*;
import me.derechtepilz.database.Database;
import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.itemmanagement.Item;
import me.derechtepilz.economy.permissionmanagement.Permission;
import me.derechtepilz.economy.utility.DataHandler;
import me.derechtepilz.economycore.EconomyAPI;
import me.derechtepilz.economycore.exceptions.BalanceException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.ServerOperator;

import java.sql.Connection;
import java.util.*;

@SuppressWarnings("unchecked")
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
                                .then(new ItemStackArgument("filter")
                                        .executesPlayer((player, args) -> {
                                            if (!main.getInventoryHandler().isTimerRunning()) {
                                                player.sendMessage("§cThe auctions are currently paused. Try again later!");
                                                return;
                                            }
                                            ItemStack filter = (ItemStack) args[0];
                                            DataHandler.setBuyMenuData(player, filter);
                                            player.sendMessage("§aYou opened the buy menu!");
                                        })
                                )
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
                                                                                        if (player.getInventory().getItem(i) == null) {
                                                                                            continue;
                                                                                        }
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
                                    if (!main.getInventoryHandler().isTimerRunning()) {
                                        player.sendMessage("§cThe auctions are currently paused. Try again later!");
                                        return;
                                    }
                                    boolean canOpenCancelMenu = main.getOfferingPlayerUuids().contains(player.getUniqueId());
                                    if (!canOpenCancelMenu) {
                                        player.sendMessage("§cDidn't open cancel menu because you didn't auction an item!");
                                        return;
                                    }
                                    DataHandler.setCancelMenuData(player);
                                    player.sendMessage("§aYou opened the cancel menu!");
                                })
                        )
                        .then(new LiteralArgument("claim")
                                .executesPlayer((player, args) -> {
                                    if (!main.getExpiredItems().containsKey(player.getUniqueId())) {
                                        player.sendMessage("§cYou cannot claim any items back because no expired auction could be found that you created!");
                                        return;
                                    }
                                    int expiredItems = main.getExpiredItems().get(player.getUniqueId()).size();
                                    int freeSlots = main.getExpiredOfferMenu().getFreeSlots(player);
                                    if (freeSlots == 0) {
                                        player.sendMessage("§cPlease make sure you have at least §6" + expiredItems + " §cslots free!");
                                        return;
                                    }
                                    main.getExpiredOfferMenu().openInventory(player);
                                })
                        )
                        .then(new LiteralArgument("pause")
                                .withRequirement(sender -> {
                                    if (sender instanceof Player player) {
                                        return Permission.hasPermission(player, Permission.PAUSE_RESUME_AUCTIONS) || player.isOp();
                                    }
                                    return false;
                                })
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
                                .withRequirement(sender -> {
                                    if (sender instanceof Player player) {
                                        return Permission.hasPermission(player, Permission.PAUSE_RESUME_AUCTIONS) || player.isOp();
                                    }
                                    return false;
                                })
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
                                .withRequirement(sender -> {
                                    if (sender instanceof Player player) {
                                        return Permission.hasPermission(player, Permission.GIVE_COINS) || player.isOp();
                                    }
                                    return false;
                                })
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
                                .withRequirement(sender -> {
                                    if (sender instanceof Player player) {
                                        return Permission.hasPermission(player, Permission.TAKE_COINS) || player.isOp();
                                    }
                                    return false;
                                })
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
                                .withRequirement(sender -> {
                                    if (sender instanceof Player player) {
                                        return Permission.hasPermission(player, Permission.SET_COINS) || player.isOp();
                                    }
                                    return false;
                                })
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
                        .then(new LiteralArgument("baltop")
                                .executesPlayer((player, args) -> {
                                    Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
                                        player.sendMessage("§7Loading baltop... Please wait!");
                                        Map<Double, UUID> playerBalances = main.getDatabase().getServerBalances(main.getDatabase().getConnection());
                                        List<Double> balances = main.getDatabase().getBalances(main.getDatabase().getConnection());
                                        balances.sort(Comparator.naturalOrder());
                                        player.sendMessage("§6This is the current baltop list:");
                                        for (int i = 0; i < balances.size() && i <= 9; i++) {
                                            player.sendMessage("§6" + (i + 1) + ". §7- §a" + playerBalances.get(balances.get(i)));
                                        }
                                    });
                                })
                        )
                )
                .then(new LiteralArgument("permission")
                        .withRequirement(ServerOperator::isOp)
                        .then(new LiteralArgument("clear")
                                .then(new PlayerArgument("player").replaceSuggestions(ArgumentSuggestions.strings(info -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toArray(String[]::new)))
                                        .executesPlayer((player, args) -> {
                                            Permission.clearPermissions((Player) args[0]);
                                            player.sendMessage("§cYou removed every permission from §b" + ((Player) args[0]).getName() + "§c!");
                                        })
                                )
                        )
                        .then(new LiteralArgument("single")
                                .then(new LiteralArgument("set")
                                        .then(new PlayerArgument("player").replaceSuggestions(ArgumentSuggestions.strings(info -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toArray(String[]::new)))
                                                .then(singlePermissionArgument
                                                        .withRequirement(ServerOperator::isOp)
                                                        .executesPlayer((player, args) -> {
                                                            Player target = (Player) args[0];
                                                            List<String> permissions = (List<String>) args[1];
                                                            for (String permissionName : permissions) {
                                                                Permission permissionToAssign = null;
                                                                for (Permission permission : Permission.values()) {
                                                                    if (permission.getPermission().equals(permissionName)) {
                                                                        permissionToAssign = permission;
                                                                    }
                                                                }
                                                                if (permissionToAssign == null) {
                                                                    player.sendMessage("§cThe permission §6" + permissionName + " §cwas not found!");
                                                                    if (permissions.indexOf(permissionName) != permissions.size() - 1) {
                                                                        continue;
                                                                    }
                                                                    return;
                                                                }
                                                                if (Permission.hasPermission(target, permissionToAssign)) {
                                                                    player.sendMessage("§cThe player §b" + target.getName() + " §calready has the permission §6" + permissionName + "§c!");
                                                                    if (permissions.indexOf(permissionName) != permissions.size() - 1) {
                                                                        continue;
                                                                    }
                                                                    return;
                                                                }
                                                                Permission.addPermission(target, permissionToAssign);
                                                                if (target.equals(player)) {
                                                                    player.sendMessage("§aYou have got the permission §6" + permissionToAssign.getPermission() + "§a!");
                                                                } else {
                                                                    target.sendMessage("§aYou have got the permission §6" + permissionToAssign.getPermission() + "§a!");
                                                                    player.sendMessage("§aSUCCESS! The permission §6" + permissionToAssign.getPermission() + " &awas given to §b" + target.getName() + "§a!");
                                                                }
                                                            }
                                                        })
                                                )
                                        )
                                )
                                .then(new LiteralArgument("remove")
                                        .then(new PlayerArgument("player").replaceSuggestions(ArgumentSuggestions.strings(info -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toArray(String[]::new)))
                                                .then(singlePermissionArgument
                                                        .withRequirement(ServerOperator::isOp)
                                                        .executesPlayer((player, args) -> {
                                                            Player target = (Player) args[0];
                                                            List<String> permissions = (List<String>) args[1];
                                                            for (String permissionName : permissions) {
                                                                Permission permissionToRemove = null;
                                                                for (Permission permission : Permission.values()) {
                                                                    if (permission.getPermission().equals(permissionName)) {
                                                                        permissionToRemove = permission;
                                                                    }
                                                                }
                                                                if (permissionToRemove == null) {
                                                                    player.sendMessage("§cThe permission §6" + permissionName + " §cwas not found!");
                                                                    if (permissions.indexOf(permissionName) != permissions.size() - 1) {
                                                                        continue;
                                                                    }
                                                                    return;
                                                                }
                                                                if (!Permission.hasPermission(target, permissionToRemove)) {
                                                                    player.sendMessage("§cThe player §b" + target.getName() + " §cdoes not have the permission §6" + permissionName + "§c!");
                                                                    if (permissions.indexOf(permissionName) != permissions.size() - 1) {
                                                                        continue;
                                                                    }
                                                                    return;
                                                                }
                                                                Permission.removePermission(player, permissionToRemove);
                                                                if (target.equals(player)) {
                                                                    player.sendMessage("§cYou have been taken the permission §6" + permissionToRemove.getPermission() + "§c!");
                                                                } else {
                                                                    target.sendMessage("§cYou have been taken the permission §6" + permissionToRemove.getPermission() + "§c!");
                                                                    player.sendMessage("§cThe permission §6" + permissionToRemove.getPermission() + " §cwas taken from §b" + target.getName() + "§c!");
                                                                }
                                                            }
                                                        })
                                                )
                                        )
                                )
                                .then(new LiteralArgument("get")
                                        .then(new PlayerArgument("player").replaceSuggestions(ArgumentSuggestions.strings(info -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toArray(String[]::new)))
                                                .then(singlePermissionArgument
                                                        .withRequirement(ServerOperator::isOp)
                                                        .executesPlayer((player, args) -> {
                                                            Player target = (Player) args[0];
                                                            String[] permissions = Permission.getPermissions(target);
                                                            player.sendMessage("§b" + target.getName() + " §ahas the following permissions:");
                                                            for (String permission : permissions) {
                                                                player.sendMessage("§6- §a" + permission);
                                                            }
                                                        })
                                                )
                                        )
                                )
                        )
                )
                .then(new LiteralArgument("config")
                        .then(new LiteralArgument("allowDirectDownloads")
                                .withRequirement(sender -> {
                                    if (sender instanceof Player player) {
                                        return Permission.hasPermission(player, Permission.MODIFY_CONFIG) || player.isOp();
                                    }
                                    return false;
                                })
                                .then(new BooleanArgument("allowDirectDownloads")
                                        .executesPlayer((player, args) -> {
                                            boolean allowDirectDownloads = (boolean) args[0];
                                            main.getPluginConfig().set("allowDirectDownloads", String.valueOf(allowDirectDownloads));

                                            player.sendMessage("§7You just set §6allowDirectDownloads §7to §6" + allowDirectDownloads + "§7!");
                                            if (allowDirectDownloads) {
                                                player.sendMessage("§7If a new update is available, the plugin will be automatically updated!");
                                                return;
                                            }
                                            player.sendMessage("§7If a new update is available, you will have to download it yourself!");
                                        })
                                )
                        )
                        .then(new LiteralArgument("startBalance")
                                .withRequirement(sender -> {
                                    if (sender instanceof Player player) {
                                        return Permission.hasPermission(player, Permission.MODIFY_CONFIG) || player.isOp();
                                    }
                                    return false;
                                })
                                .then(new DoubleArgument("startBalance", 0.0)
                                        .executesPlayer((player, args) -> {
                                            double startBalance = (double) args[0];
                                            EconomyAPI.setStartBalance(startBalance);
                                            player.sendMessage("§7You set the start balance to §6" + startBalance + "§7!");
                                        })
                                )
                        )
                        .then(new LiteralArgument("interestRate")
                                .withRequirement(sender -> {
                                    if (sender instanceof Player player) {
                                        return Permission.hasPermission(player, Permission.MODIFY_CONFIG) || player.isOp();
                                    }
                                    return false;
                                })
                                .then(new DoubleArgument("interestRate", 0.0)
                                        .executesPlayer((player, args) -> {
                                            double interestRate = (double) args[0];
                                            EconomyAPI.setInterestRate(interestRate);
                                            player.sendMessage("§7You set the interest rate to §6" + interestRate + "§7!");
                                        })
                                )
                        )
                        .then(new LiteralArgument("minimumDaysForInterest")
                                .withRequirement(sender -> {
                                    if (sender instanceof Player player) {
                                        return Permission.hasPermission(player, Permission.MODIFY_CONFIG) || player.isOp();
                                    }
                                    return false;
                                })
                                .then(new IntegerArgument("minimumDaysForInterest", 1)
                                        .executesPlayer((player, args) -> {
                                            int minimumDaysForInterest = (int) args[0];
                                            EconomyAPI.setMinimumDaysForInterest(minimumDaysForInterest);
                                            player.sendMessage("§7You set the minimum days to get interest to §6" + minimumDaysForInterest + "§7!");
                                        })
                                )
                        )
                        .then(new LiteralArgument("reset")
                                .withRequirement(sender -> {
                                    if (sender instanceof Player player) {
                                        return Permission.hasPermission(player, Permission.RESET_CONFIG) || player.isOp();
                                    }
                                    return false;
                                })
                                .executesPlayer((player, args) -> {
                                    main.getPluginConfig().resetConfig();
                                    player.sendMessage("§7The config has been reset!");
                                })
                        )
                        .then(new LiteralArgument("reload")
                                .withRequirement(sender -> {
                                    if (sender instanceof Player player) {
                                        return Permission.hasPermission(player, Permission.RESET_CONFIG) || player.isOp();
                                    }
                                    return false;
                                })
                                .executesPlayer((player, args) -> {
                                    main.getPluginConfig().reloadConfig();
                                    player.sendMessage("§7The config has been reloaded!");
                                })
                        )
                )
                .register();
    }

    private final ListArgument<String> singlePermissionArgument = new ListArgumentBuilder<String>("permissions", ",").allowDuplicates(false).withList(Permission.getPermissions()).withStringMapper().build();

}
