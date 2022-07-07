package me.derechtepilz.economy.utility;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class Cooldown implements Runnable {
    private final Player player;
    private final Player target;
    private final long endTime;
    private final ICooldown iCooldown;
    private BukkitTask toCancel;

    public Cooldown(Player player, Player target, long endTime, ICooldown iCooldown) {
        this.player = player;
        this.target = target;
        this.endTime = endTime;
        this.iCooldown = iCooldown;
    }

    @Override
    public void run() {
        boolean cooldownEnded = iCooldown.checkDate(player, target, this);
        if (cooldownEnded) {
            toCancel.cancel();
        }
    }

    public Player player() {
        return player;
    }

    public Player target() {
        return target;
    }

    public long endTime() {
        return endTime;
    }

    public void setCancelTask(BukkitTask task) {
        this.toCancel = task;
    }

    public BukkitTask getToCancel() {
        return toCancel;
    }
}
