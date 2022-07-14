package me.derechtepilz.economy.modules.discord;

import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.events.DiscordValuesSetEvent;
import me.derechtepilz.economy.utility.config.Config;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class StartUpBot implements Listener {
    @EventHandler
    public void onValuesSet(DiscordValuesSetEvent event) {
        Main.getInstance().stopDiscordBot();
        Main.getInstance().startDiscordBot(Config.get("discordToken"));
    }
}
