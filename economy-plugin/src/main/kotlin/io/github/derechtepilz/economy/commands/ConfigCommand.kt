package io.github.derechtepilz.economy.commands

import dev.jorel.commandapi.arguments.LiteralArgument.of
import dev.jorel.commandapi.kotlindsl.argument
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerExecutor
import io.github.derechtepilz.economy.Main
import io.github.derechtepilz.economy.utils.hasAnyPermission
import org.bukkit.command.CommandSender
import org.bukkit.permissions.Permission

class ConfigCommand(private val main: Main) {

	fun register() {
		commandTree("config") {
			withRequirement { sender: CommandSender ->
				sender.hasAnyPermission(
					Permission("economy.config"),
					Permission("economy.config.modify"),
					Permission("economy.config.reset"),
					Permission("economy.config.reload")
				)
			}
			argument(of("set").withPermission("economy.config.modify")) {
				playerExecutor { player, args ->

				}
			}
			argument(of("reset").withPermission("economy.config.reset")) {
				playerExecutor { player, args ->

				}
			}
			argument(of("reload").withPermission("economy.config.reload")) {
				playerExecutor { player, args ->

				}
			}
		}
	}

}