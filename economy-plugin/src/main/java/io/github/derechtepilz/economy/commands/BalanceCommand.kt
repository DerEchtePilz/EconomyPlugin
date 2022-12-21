package io.github.derechtepilz.economy.commands

import dev.jorel.commandapi.arguments.LiteralArgument.of
import dev.jorel.commandapi.kotlindsl.argument
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import io.github.derechtepilz.economy.Main

class BalanceCommand(private val main: Main) {

	fun register() {
		commandTree("balance") {
			argument(of("baltop").withPermission("economy.coin.baltop")) {

			}
			argument(of("set").withPermission("economy.coin.manage.set")) {

			}
			argument(of("add").withPermission("economy.coin.manage.add")) {

			}
			argument(of("remove").withPermission("economy.coin.manage.remove")) {

			}
		}
	}

}