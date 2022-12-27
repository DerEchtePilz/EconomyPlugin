package io.github.derechtepilz.economy.commands

import dev.jorel.commandapi.arguments.LiteralArgument.of
import dev.jorel.commandapi.kotlindsl.argument
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import io.github.derechtepilz.economy.Main
import io.github.derechtepilz.economy.utils.hasAnyPermission
import org.bukkit.command.CommandSender
import org.bukkit.permissions.Permission

class FriendCommand(private val main: Main) {

	fun register() {
		commandTree("friend") {
			withRequirement { sender: CommandSender ->
				sender.hasAnyPermission(
					Permission("economy.friend"),
					Permission("economy.friend.add"),
					Permission("economy.friend.remove"),
					Permission("economy.friend.manage"),
					Permission("economy.friend.manage.add"),
					Permission("economy.friend.manage.remove")
				)
			}
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