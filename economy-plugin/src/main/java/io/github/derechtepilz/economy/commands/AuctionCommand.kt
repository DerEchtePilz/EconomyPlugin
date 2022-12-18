package io.github.derechtepilz.economy.commands

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import io.github.derechtepilz.economy.Main

class AuctionCommand(private val main: Main) {

	fun register() {
		commandTree("auction") {
			literalArgument("create") {

			}
		}
	}

}