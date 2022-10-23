package io.github.derechtepilz.economy.coinmanagement

import io.github.derechtepilz.economy.Main
import io.github.derechtepilz.economy.utility.ChatFormatter
import io.github.derechtepilz.economycore.EconomyAPI
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class JoinCoinManagement(private val main: Main) : Listener {

    private val chatFormatter: ChatFormatter = ChatFormatter()

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player: Player = event.player
        if (main.earnedCoins.containsKey(player.uniqueId)) {
            val earnedCoins: Double = main.earnedCoins[player.uniqueId]!!
            EconomyAPI.addCoinsToBalance(player, earnedCoins)

            val displayEarnedCoins: String = chatFormatter.valueOf(earnedCoins)
            player.sendMessage("§aYou got §6$displayEarnedCoins coins §afrom selling items while you were offline!")
        }
    }

}