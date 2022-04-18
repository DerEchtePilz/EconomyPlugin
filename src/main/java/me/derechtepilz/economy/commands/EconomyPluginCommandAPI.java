package me.derechtepilz.economy.commands;

import me.derechtepilz.economy.commands.nms.NMS;
import org.bukkit.Bukkit;

public class EconomyPluginCommandAPI {

    private final NMS NMS;

    private static EconomyPluginCommandAPI economyPluginCommandAPI;

    public EconomyPluginCommandAPI() {
        economyPluginCommandAPI = this;
        NMS = EconomyPluginCommandAPIVersionHandler.getNMS(Bukkit.getBukkitVersion().split("-")[0]);
    }

    public static EconomyPluginCommandAPI getInstance() {
        return economyPluginCommandAPI;
    }

    public NMS getNMS() {
        return NMS;
    }
}
