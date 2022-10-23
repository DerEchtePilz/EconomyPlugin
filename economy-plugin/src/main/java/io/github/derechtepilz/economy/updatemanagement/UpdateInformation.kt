package io.github.derechtepilz.economy.updatemanagement

import io.github.derechtepilz.economy.Main
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class UpdateInformation(private val main: Main) : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player: Player = event.player
        if (player.isOp) {
            player.sendMessage("§bThere is a new update available for the EconomyPlugin! Please download the latest version at §ehttps:/github.com/DerEchtePilz/EconomyPlugin/releases/latest")
        }
    }

}