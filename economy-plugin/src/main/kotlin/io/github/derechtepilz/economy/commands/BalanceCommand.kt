package io.github.derechtepilz.economy.commands

import dev.jorel.commandapi.arguments.LiteralArgument.of
import dev.jorel.commandapi.kotlindsl.argument
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerExecutor
import io.github.derechtepilz.economy.Main
import io.github.derechtepilz.economy.utils.hasAnyPermission
import org.bukkit.command.CommandSender
import org.bukkit.permissions.Permission

class BalanceCommand(private val main: Main) {

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

				}
			}
			argument(of("set").withPermission("economy.coin.manage.set")) {
				playerExecutor { player, args ->

				}
			}
			argument(of("add").withPermission("economy.coin.manage.add")) {
				playerExecutor { player, args ->

				}
			}
			argument(of("remove").withPermission("economy.coin.manage.remove")) {
				playerExecutor { player, args ->

				}
			}
		}
	}

}