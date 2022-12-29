package io.github.derechtepilz.economy.commands

import dev.jorel.commandapi.ArgumentTree
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CommandArgument
import dev.jorel.commandapi.arguments.LiteralArgument.of
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.kotlindsl.*
import io.github.derechtepilz.economy.Main
import io.github.derechtepilz.economy.utils.LanguageManager
import io.github.derechtepilz.economy.utils.SuggestionProvider
import io.github.derechtepilz.economy.utils.hasAnyPermission
import org.bukkit.command.CommandSender
import org.bukkit.permissions.Permission

class ConfigCommand(private val main: Main) {

	private val commandExecution: CommandExecution = main.commandExecution

	fun register() {
		commandTree("config") {
			withRequirement { sender: CommandSender ->
				sender.hasAnyPermission(
					Permission("economy.config"),
					Permission("economy.config.modify"),
					Permission("economy.config.reset"),
					Permission("economy.config.reload")
				)
			}
			argument(of("set").withPermission("economy.config.modify")) {
				literalArgument("allowDirectDownloads") {
					booleanArgument("value") {
						playerExecutor { player, args ->
							commandExecution.setAllowDirectDownloads(player, args)
						}
					}
				}
				literalArgument("language") {
					languageArgument("language") {
						playerExecutor { player, args ->
							commandExecution.setLanguage(player, args)
						}
					}
				}
			}
			argument(of("reset").withPermission("economy.config.reset")) {
				playerExecutor { player, args ->
					commandExecution.resetConfig(player, args)
				}
			}
			argument(of("reload").withPermission("economy.config.reload")) {
				playerExecutor { player, args ->
					commandExecution.reloadConfig(player, args)
				}
			}
		}
	}

	private inline fun ArgumentTree.languageArgument(nodeName: String, block: ArgumentTree.() -> Unit = {}): ArgumentTree =
		argument(StringArgument(nodeName).replaceSuggestions(
			ArgumentSuggestions.strings { main.suggestionProvider.provideSuggestions(SuggestionProvider.SuggestionType.LANGUAGE) }
		), block)

}