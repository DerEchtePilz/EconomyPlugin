package io.github.derechtepilz.economy.commands

import dev.jorel.commandapi.arguments.LiteralArgument.of
import dev.jorel.commandapi.kotlindsl.*
import io.github.derechtepilz.economy.Main
import io.github.derechtepilz.economy.utils.hasAnyPermission
import org.bukkit.command.CommandSender
import org.bukkit.permissions.Permission

class BalanceCommand(main: Main) {

	private val commandExecution: CommandExecution = main.commandExecution

	fun register() {
		commandTree("balance") {
			withRequirement { sender: CommandSender ->
				sender.hasAnyPermission(
					Permission("economy.coin"),
					Permission("economy.coin.baltop"),
					Permission("economy.coin.manage.set"),
					Permission("economy.coin.manage.add"),
					Permission("economy.coin.manage.remove")
				)
			}
			argument(of("baltop").withPermission("economy.coin.baltop")) {
				playerExecutor { player, args ->
					commandExecution.baltop(player, args)
				}
			}
			argument(of("set").withPermission("economy.coin.manage.set")) {
				playerArgument("target") {
					doubleArgument("amount") {
						playerExecutor { player, args ->
							commandExecution.setCoins(player, args)
						}
					}
				}
			}
			argument(of("add").withPermission("economy.coin.manage.add")) {
				playerArgument("target") {
					doubleArgument("amount") {
						playerExecutor { player, args ->
							commandExecution.addCoins(player, args)
						}
					}
				}
			}
			argument(of("remove").withPermission("economy.coin.manage.remove")) {
				playerArgument("target") {
					doubleArgument("amount") {
						playerExecutor { player, args ->
							commandExecution.removeCoins(player, args)
						}
					}
				}
			}
		}
	}

}