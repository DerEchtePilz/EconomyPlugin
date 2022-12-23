package io.github.derechtepilz.economy.commands

import dev.jorel.commandapi.arguments.LiteralArgument.of
import dev.jorel.commandapi.kotlindsl.argument
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import io.github.derechtepilz.economy.Main

class BalanceCommand(private val main: Main) {

	fun register() {
		commandTree("balance") {
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