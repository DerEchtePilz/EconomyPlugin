package io.github.derechtepilz.economy.commands

import dev.jorel.commandapi.ArgumentTree
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.LiteralArgument.of
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.kotlindsl.*
import io.github.derechtepilz.economy.Main
import io.github.derechtepilz.economy.utils.SuggestionProvider
import io.github.derechtepilz.economy.utils.hasAnyPermission
import org.bukkit.command.CommandSender
import org.bukkit.permissions.Permission

class FriendCommand(private val main: Main) {

	private val commandExecution: CommandExecution = main.commandExecution

	fun register() {
		commandTree("friend") {
			argument(of("add").withPermission("economy.friend.add")) {
				playerArgument("target") {
					playerExecutor { player, args ->
						commandExecution.addFriend(player, args)
					}
				}
			}
			argument(of("remove").withPermission("economy.friend.remove")) {
				playerArgument("target") {
					playerExecutor { player, args ->
						commandExecution.removeFriend(player, args)
					}
				}
			}
			literalArgument("accept") {
				playerArgument("target") {
					playerExecutor { player, args ->
						commandExecution.acceptFriend(player, args)
					}
				}
			}
			literalArgument("deny") {
				playerArgument("target") {
					playerExecutor { player, args ->
						commandExecution.denyFriend(player, args)
					}
				}
			}
			literalArgument("manage") {
				argument(of("add").withPermission("economy.friend.manage.add")) {
					playerArgument("player") {
						playerArgument("target") {
							playerExecutor { player, args ->
								commandExecution.addFriendToPlayer(player, args)
							}
						}
					}
				}
				argument(of("remove").withPermission("economy.friend.manage.remove")) {
					playerArgument("player") {
						playerArgument("target") {
							playerExecutor { player, args ->
								commandExecution.removeFriendFromPlayer(player, args)
							}
						}
					}
				}
			}
		}
	}

	private inline fun ArgumentTree.playerArgument(nodeName: String, block: ArgumentTree.() -> Unit = {}): ArgumentTree =
		argument(PlayerArgument(nodeName).replaceSuggestions(ArgumentSuggestions.strings { main.suggestionProvider.provideSuggestions(SuggestionProvider.SuggestionType.PLAYER) }), block)

}