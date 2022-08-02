package me.derechtepilz.economy;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.DoubleArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.ItemStackArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import me.derechtepilz.economy.itemmanagement.Item;
import me.derechtepilz.economycore.EconomyAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;

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

                                })
                        )
                        .then(new LiteralArgument("create")
                                .then(new ItemStackArgument("item")
                                        .then(new IntegerArgument("amount", 1)
                                                .then(new DoubleArgument("price", 1)
                                                        .then(new IntegerArgument("duration", 1)
                                                                .executesPlayer((player, args) -> {
                                                                    ItemStack item = (ItemStack) args[0];
                                                                    int amount = (int) args[1];
                                                                    double price = (double) args[2];
                                                                    int duration = (int) args[3];

                                                                    // Run checks (does the player have this type, does the player have the amount)
                                                                    // Remove the offered amount from the inventory

                                                                    Item offer = new Item(main, item.getType(), amount, price, player.getUniqueId(), duration);
                                                                    player.sendMessage("§aYou created a new offer! It lasts §6" + duration + " §aseconds!");
                                                                })
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
                                .executesPlayer((player, args) -> {

                                })
                        )
                        .then(new LiteralArgument("take")
                                .executesPlayer((player, args) -> {

                                })
                        )
                        .then(new LiteralArgument("set")
                                .executesPlayer((player, args) -> {

                                })
                        )
                )
                .then(new LiteralArgument("permission")
                        .then(new LiteralArgument("group")
                                .then(new LiteralArgument("set")
                                        .executesPlayer((player, args) -> {

                                        })
                                )
                                .then(new LiteralArgument("remove")
                                        .executesPlayer((player, args) -> {

                                        })
                                )
                                .then(new LiteralArgument("get")
                                        .executesPlayer((player, args) -> {

                                        })
                                )
                        )
                        .then(new LiteralArgument("single")
                                .then(new LiteralArgument("set")
                                        .executesPlayer((player, args) -> {

                                        })
                                )
                                .then(new LiteralArgument("remove")
                                        .executesPlayer((player, args) -> {

                                        })
                                )
                                .then(new LiteralArgument("get")
                                        .executesPlayer((player, args) -> {

                                        })
                                )
                        )
                )
                .then(new LiteralArgument("config")
                        .then(new LiteralArgument("startBalance")
                                .then(new DoubleArgument("startBalance")
                                        .executesPlayer((player, args) -> {
                                            double newStartBalance = (double) args[0];
                                            EconomyAPI.setStartBalance(newStartBalance);
                                        })
                                )
                        )
                        .then(new LiteralArgument("interest"))
                )
                .then(new LiteralArgument("trade")
                        .executesPlayer((player, args) -> {

                        })
                )
                .register();
    }
}
