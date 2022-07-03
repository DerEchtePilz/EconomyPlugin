package me.derechtepilz.economy.utility;

import org.bukkit.entity.Player;

public interface ICooldown {
    boolean checkDate(Player player, Player target, Cooldown cooldown);
}
