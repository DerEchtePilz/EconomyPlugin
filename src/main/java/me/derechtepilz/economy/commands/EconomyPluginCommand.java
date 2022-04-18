package me.derechtepilz.economy.commands;

import me.derechtepilz.economy.commands.nms.NMS;
import org.bukkit.Bukkit;

public class EconomyPluginCommand {

    private final NMS NMS;

    public EconomyPluginCommand() {
        NMS = EconomyPluginVersionHandler.getNMS(Bukkit.getBukkitVersion().split("-")[0]);
    }

}
