package me.derechtepilz.economy.datamanagement

import me.derechtepilz.economy.Main
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class Converter(private val main: Main) : Listener {

    @EventHandler(priority = EventPriority.LOW)
    fun onJoin(event: PlayerJoinEvent) {

    }

}