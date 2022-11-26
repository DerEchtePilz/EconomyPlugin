package io.github.derechtepilz.economy.tradesystem

import io.github.derechtepilz.economy.Main
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class TradeListener(private val main: Main) : Listener {

	@EventHandler
	fun onClick(event: InventoryClickEvent) {
		if (event.view.title != "Trade Menu") return
		val player: Player = event.whoClicked as Player
		val trade: Trade = main.trades[player.uniqueId]!!
		val tradeInitiator: Player? = if (trade.getTradeInitiator() == player.uniqueId) player else null
		val tradeTarget: Player? = if (trade.getTradeTarget() == player.uniqueId) player else null

	}

}