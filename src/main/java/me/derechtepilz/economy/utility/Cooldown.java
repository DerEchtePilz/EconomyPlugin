package me.derechtepilz.economy.utility;

import me.derechtepilz.economy.Main;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class Cooldown implements Runnable {
    private final Player player;
    private final long endTime;
    private final ICooldown iCooldown;
    private BukkitTask toCancel;

    public Cooldown(Player player, long endTime, ICooldown iCooldown) {
        this.player = player;
        this.endTime = endTime;
        this.iCooldown = iCooldown;
    }

    @Override
    public void run() {
        boolean cooldownEnded = iCooldown.checkDate(player, this);
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
