package me.derechtepilz.economy.itemmanager;

import dev.jorel.commandapi.CommandAPICommand;
import me.derechtepilz.economy.Main;
import org.bukkit.entity.Player;

public class ItemBuyOffer {
    public ItemBuyOffer() {
        new CommandAPICommand("buy")
                .executes((sender, args) -> {
                    if (!(sender instanceof Player player)) {
                        sender.sendMessage("§cYou cannot execute this command!");
                        return;
                    }

                })
                .register();
    }
}
