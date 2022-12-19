package io.github.derechtepilz.economy.commands

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import io.github.derechtepilz.economy.Main

class FriendCommand(private val main: Main) {

	fun register() {
		commandTree("friend") {
			literalArgument("add") {
				withPermission("economy.friend.add")
			}
			literalArgument("remove") {
				withPermission("economy.friend.remove")
			}
			literalArgument("accept") {

			}
			literalArgument("deny") {

			}
			literalArgument("manage") {
				literalArgument("add") {
					withPermission("economy.friend.manage.add")
				}
				literalArgument("remove") {
					withPermission("economy.friend.manage.remove")
				}
			}
		}
	}

}