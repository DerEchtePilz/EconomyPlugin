package io.github.derechtepilz.economy.updatemanagement

import io.github.derechtepilz.economy.Main
import io.github.derechtepilz.economy.utils.TranslatableComponent
import io.github.derechtepilz.economy.utils.sendMessage
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class UpdateInformation(private val main: Main) : Listener {

	@EventHandler
	fun onJoin(event: PlayerJoinEvent) {
		val player: Player = event.player
		if (player.isOp) {
			player.sendMessage(TranslatableComponent("update_information.on_join.inform_admins"))
		}
	}

}