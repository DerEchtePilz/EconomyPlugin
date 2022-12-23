package io.github.derechtepilz.economy.commands

import dev.jorel.commandapi.ArgumentTree
import dev.jorel.commandapi.CommandPermission
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.ListArgument
import dev.jorel.commandapi.arguments.ListArgumentBuilder
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.kotlindsl.argument
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import io.github.derechtepilz.economy.Main
import io.github.derechtepilz.economy.utils.SuggestionProvider
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class PermissionCommand(private val main: Main) {

	private val commandExecution: CommandExecution = main.executeCommand

	fun register() {
		commandTree("permission") {
			withPermission(CommandPermission.OP)
			literalArgument("set") {
				playerArgument("target") {
					listArgument("permissions") {
						playerExecutor { player, args ->

						}
					}
				}
			}
			literalArgument("get") {
				playerArgument("target") {
					playerExecutor { player, args ->

					}
				}
			}
			literalArgument("remove") {
				playerArgument("target") {
					listArgument("permissions") {
						playerExecutor { player, args ->

						}
					}
				}
			}
		}
	}

	private inline fun ArgumentTree.playerArgument(nodeName: String, block: ArgumentTree.() -> Unit = {}): ArgumentTree =
		argument(PlayerArgument(nodeName).replaceSuggestions(ArgumentSuggestions.strings { main.suggestionProvider.provideSuggestions(SuggestionProvider.SuggestionType.PLAYER) }), block)

	private inline fun ArgumentTree.listArgument(nodeName: String, block: ArgumentTree.() -> Unit = {}): ArgumentTree =
		argument(permissionListArgument(nodeName), block)

	private fun permissionListArgument(nodeName: String): ListArgument<String> {
		return ListArgumentBuilder<String>(nodeName, ",")
			.allowDuplicates(false)
			.withList(*main.suggestionProvider.provideSuggestions(SuggestionProvider.SuggestionType.PERMISSION))
			.withStringMapper()
			.buildGreedy()
	}

}