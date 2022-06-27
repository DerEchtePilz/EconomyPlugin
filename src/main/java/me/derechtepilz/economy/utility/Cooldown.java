package me.derechtepilz.economy.utility;

import me.derechtepilz.economy.Main;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class Cooldown implements Runnable {
    private final Player player;
    private final long endTime;
    private BukkitTask toCancel;

    public Cooldown(Player player, long endTime) {
        this.player = player;
        this.endTime = endTime;
    }

    @Override
    public void run() {
        boolean cooldownEnded = Main.getInstance().getCooldownMap().get(player.getUniqueId()).checkDate(player, this);
        if (cooldownEnded) {
            toCancel.cancel();
        }
    }

    public Player player() {
        return player;
    }

    public long endTime() {
        return endTime;
    }

    public void setCancelTask(BukkitTask task) {
        this.toCancel = task;
    }
}
