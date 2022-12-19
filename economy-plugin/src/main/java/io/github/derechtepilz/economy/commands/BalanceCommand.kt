package io.github.derechtepilz.economy.commands

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import io.github.derechtepilz.economy.Main

class BalanceCommand(private val main: Main) {

	fun register() {
		commandTree("balance") {
			literalArgument("baltop") {
				withPermission("economy.coin.baltop")
			}
			literalArgument("set") {
				withPermission("economy.coin.manage.set")
			}
			literalArgument("add") {
				withPermission("economy.coin.manage.add")
			}
			literalArgument("remove") {
				withPermission("economy.coin.manage.remove")
			}
		}
	}

}