package me.derechtepilz.economy.listeners;

import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class CheckPluginUpdate implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (Main.getInstance().isNewUpdateAvailable()) {
            Player player = event.getPlayer();
            if (player.isOp()) {
                player.sendMessage(TranslatableChatComponent.read("checkPluginUpdate.new_update_available"));
            }
        }
    }
}
