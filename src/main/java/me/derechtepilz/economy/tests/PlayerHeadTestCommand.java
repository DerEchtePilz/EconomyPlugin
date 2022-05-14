package me.derechtepilz.economy.tests;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.LiteralArgument;
import me.derechtepilz.economy.utility.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PlayerHeadTestCommand {
    public PlayerHeadTestCommand() {
        new CommandTree("test")
                .then(new LiteralArgument("head")
                        .executesPlayer((player, args) -> {
                            player.getInventory().addItem(new ItemBuilder(Material.PLAYER_HEAD).setTexture(player.getName()).build());
                        }))
                .register();
    }
}
