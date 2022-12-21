package io.github.derechtepilz.economy.commands

import dev.jorel.commandapi.arguments.LiteralArgument.of
import dev.jorel.commandapi.kotlindsl.argument
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import io.github.derechtepilz.economy.Main

class FriendCommand(private val main: Main) {

	fun register() {
		commandTree("friend") {
			argument(of("add").withPermission("economy.friend.add")) {

			}
			argument(of("remove").withPermission("economy.friend.remove")) {

			}
			literalArgument("accept") {

			}
			literalArgument("deny") {

			}
			literalArgument("manage") {
				argument(of("add").withPermission("economy.friend.manage.add")) {

				}
				argument(of("remove").withPermission("economy.friend.manage.remove")) {
					
				}
			}
		}
	}

}