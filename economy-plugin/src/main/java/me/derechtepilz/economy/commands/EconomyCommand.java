package me.derechtepilz.economy.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.*;
import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.itemmanagement.Item;
import me.derechtepilz.economy.utility.DataHandler;
import me.derechtepilz.economy.permissionmanagement.Permission;
import me.derechtepilz.economycore.EconomyAPI;
import me.derechtepilz.economycore.exceptions.BalanceException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.ServerOperator;

import java.util.ArrayList;
import java.util.List;

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
                                                                                        if (player.getInventory().getItem(i) == null)
                                                                                            continue;

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
                                        return Permission.hasPermission(player, Permission.PAUSE_RESUME_AUCTIONS);
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
                                        return Permission.hasPermission(player, Permission.PAUSE_RESUME_AUCTIONS);
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
                                        return Permission.hasPermission(player, Permission.GIVE_COINS);
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
                                        return Permission.hasPermission(player, Permission.TAKE_COINS);
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
                                        return Permission.hasPermission(player, Permission.SET_COINS);
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
                )
                .then(new LiteralArgument("permission")
                        .withRequirement(ServerOperator::isOp)
                        .then(new LiteralArgument("clear")
                                .then(new PlayerArgument("player").replaceSuggestions(ArgumentSuggestions.strings(info -> {
                                                    List<String> players = new ArrayList<>();
                                                    Bukkit.getOnlinePlayers().forEach(player -> players.add(player.getName()));
                                                    return players.toArray(new String[0]);
                                                }))
                                                .executesPlayer((player, args) -> {
                                                    Permission.clearPermissions((Player) args[0]);
                                                    player.sendMessage("§cYou removed every permission from §b" + ((Player) args[0]).getName() + "§c!");
                                                })
                                )
                        )
                        .then(new LiteralArgument("single")
                                .then(new LiteralArgument("set")
                                        .then(new PlayerArgument("player").replaceSuggestions(ArgumentSuggestions.strings(info -> {
                                                            List<String> players = new ArrayList<>();
                                                            Bukkit.getOnlinePlayers().forEach(player -> players.add(player.getName()));
                                                            return players.toArray(new String[0]);
                                                        }))
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
                                        .then(new PlayerArgument("player").replaceSuggestions(ArgumentSuggestions.strings(info -> {
                                                            List<String> players = new ArrayList<>();
                                                            Bukkit.getOnlinePlayers().forEach(player -> players.add(player.getName()));
                                                            return players.toArray(new String[0]);
                                                        }))
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
                                        .then(new PlayerArgument("player").replaceSuggestions(ArgumentSuggestions.strings(info -> {
                                                            List<String> players = new ArrayList<>();
                                                            Bukkit.getOnlinePlayers().forEach(player -> players.add(player.getName()));
                                                            return players.toArray(new String[0]);
                                                        }))
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
                .register();
    }

    private final ListArgument<String> singlePermissionArgument = new ListArgumentBuilder<String>("permissions", ",").allowDuplicates(false).withList(Permission.getPermissions()).withStringMapper().build();

}
