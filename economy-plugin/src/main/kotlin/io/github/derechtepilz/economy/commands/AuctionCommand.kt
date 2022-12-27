package io.github.derechtepilz.economy.commands

import dev.jorel.commandapi.arguments.LiteralArgument.of
import dev.jorel.commandapi.kotlindsl.argument
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import io.github.derechtepilz.economy.Main
import io.github.derechtepilz.economy.utils.hasAnyPermission
import org.bukkit.command.CommandSender
import org.bukkit.permissions.Permission

class AuctionCommand(private val main: Main) {

	fun register() {
		commandTree("auction") {
			withRequirement { sender: CommandSender ->
				sender.hasAnyPermission(
					Permission("economy.auction"),
					Permission("economy.auction.create"),
					Permission("economy.auction.modify")
				)
			}
			argument(of("create").withPermission("economy.auction.create")) {
				playerExecutor { player, args ->

				}
			}
			argument(of("modify").withPermission("economy.auction.modify")) {
				playerExecutor { player, args ->

				}
			}
		}
	}

}