package me.derechtepilz.economy.playermanager.permission;

import me.derechtepilz.economy.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class DefaultPermissionGroup implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!Main.getInstance().getCustomPermissionGroup().hasPlayerPermissionGroup("default", player)) {
            Main.getInstance().getCustomPermissionGroup().addPermissionGroupToPlayer("default", player);
        }
    }
}
