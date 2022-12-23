package io.github.derechtepilz.economy.commands

import dev.jorel.commandapi.arguments.LiteralArgument.of
import dev.jorel.commandapi.kotlindsl.argument
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import io.github.derechtepilz.economy.Main

class FriendCommand(private val main: Main) {

	fun register() {
		commandTree("friend") {
			argument(of("add").withPermission("economy.friend.add")) {
				playerExecutor { player, args ->

				}
			}
			argument(of("remove").withPermission("economy.friend.remove")) {
				playerExecutor { player, args ->

				}
			}
			literalArgument("accept") {
				playerExecutor { player, args ->

				}
			}
			literalArgument("deny") {
				playerExecutor { player, args ->

				}
			}
			literalArgument("manage") {
				argument(of("add").withPermission("economy.friend.manage.add")) {
					playerExecutor { player, args ->

					}
				}
				argument(of("remove").withPermission("economy.friend.manage.remove")) {
					playerExecutor { player, args ->

					}
				}
			}
		}
	}

}