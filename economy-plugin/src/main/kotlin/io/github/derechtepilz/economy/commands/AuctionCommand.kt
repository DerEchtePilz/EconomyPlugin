package io.github.derechtepilz.economy.commands

import dev.jorel.commandapi.arguments.LiteralArgument.of
import dev.jorel.commandapi.kotlindsl.*
import io.github.derechtepilz.economy.Main
import io.github.derechtepilz.economy.utils.hasAnyPermission
import org.bukkit.command.CommandSender
import org.bukkit.permissions.Permission

class AuctionCommand(main: Main) {

	private val commandExecution: CommandExecution = main.commandExecution

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
				itemStackArgument("item") {
					doubleArgument("price") {
						integerArgument("hour") {
							integerArgument("minute") {
								integerArgument("second") {
									playerExecutor { player, args ->
										commandExecution.createAuction(player, args)
									}
								}
							}
						}
					}
				}
			}
			argument(of("modify").withPermission("economy.auction.modify")) {
				playerExecutor { player, args ->
					commandExecution.modifyAuction(player, args)
				}
			}
		}
	}

}