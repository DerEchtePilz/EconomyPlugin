package me.derechtepilz.economy.tests;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.ItemStackArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import me.derechtepilz.economy.utility.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerHeadTestCommand {
    public PlayerHeadTestCommand() {
        new CommandTree("test")
                .then(new LiteralArgument("head")
                        .executesPlayer((player, args) -> {
                            player.getInventory().addItem(new ItemBuilder(Material.PLAYER_HEAD).setTexture(player.getName()).build());
                        }))
                .then(new LiteralArgument("itemnames")
                        .then(new ItemStackArgument("items")
                                .executesPlayer((player, args) -> {
                                    ItemStack item = (ItemStack) args[0];
                                    ItemMeta meta = item.getItemMeta();
                                    player.sendMessage(meta.getLocalizedName());
                                })))
                .register();
    }
}
