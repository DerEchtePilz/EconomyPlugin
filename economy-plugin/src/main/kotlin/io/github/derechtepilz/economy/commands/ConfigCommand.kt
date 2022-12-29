package io.github.derechtepilz.economy.commands

import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.LiteralArgument.of
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.kotlindsl.*
import io.github.derechtepilz.economy.Main
import io.github.derechtepilz.economy.utils.LanguageManager
import io.github.derechtepilz.economy.utils.hasAnyPermission
import org.bukkit.command.CommandSender
import org.bukkit.permissions.Permission

class ConfigCommand(main: Main) {

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
					argument(StringArgument("language").replaceSuggestions(ArgumentSuggestions.strings { LanguageManager.Language.values().map { language: LanguageManager.Language -> language.name.lowercase() }.toTypedArray() })) {
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

}