package me.derechtepilz.economy.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.utility.NamespacedKeys;
import org.bukkit.entity.Player;

public class ConsoleCommands {

    private final Main main;

    public ConsoleCommands(Main main) {
        this.main = main;
    }

    public void register() {
        new CommandTree("sudo")
            .then(new LiteralArgument("removeInventoryData")
                .then(new PlayerArgument("player")
                    .executesConsole((console, args) -> {
                        Player player = (Player) args[0];
                        player.getPersistentDataContainer().remove(NamespacedKeys.INVENTORY_PAGE);
                        player.getPersistentDataContainer().remove(NamespacedKeys.INVENTORY_TYPE);
                    })))
            .register();
    }

}
