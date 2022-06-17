package me.derechtepilz.economy.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class DiscordValuesSetEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;

    public DiscordValuesSetEvent(Player who) {
        this.player = who;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Player getPlayer() {
        return player;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
