package io.github.derechtepilz.economy.tests

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.EntitySelector
import dev.jorel.commandapi.arguments.LiteralArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import io.github.derechtepilz.economy.Main
import io.github.derechtepilz.economy.commands.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

class TestsCommand(main: Main) {

	private val inventoryTest = main.inventoryTest
	private val testsCommandExecution = TestsCommandExecution(inventoryTest)

	@Suppress("UNCHECKED_CAST")
	fun register() {
		commandTree("test") {
			literalArgument("inventory") {
				playerExecutor { player, args ->
					testsCommandExecution.inventory(player, args)
				}
				literalArgument("regenerate") {
					integerArgument("items", 0) {
						playerExecutor { player, args ->
							testsCommandExecution.regenerate(player, args)
						}
					}
				}
			}
			literalArgument("entity") {
				entitySelectorArgument("entity", EntitySelector.MANY_ENTITIES) {
					playerExecutor { player, args ->
						val collection: Collection<Entity> = args[0] as Collection<Entity>
						player.sendMessage(collection.toString())
					}
				}
			}
			literalArgument("console") {
				consoleExecutor { console, args ->
					console.sendMessage("Hello World!")
				}
			}
			literalArgument("block") {
				commandBlockExecutor { blockCommandSender, args ->
					Bukkit.broadcastMessage("Hello World!")
				}
			}
			literalArgument("proxy") {
				proxyExecutor { proxiedCommandSender, args ->
					proxiedCommandSender.sendMessage("Hello World, I am a " + proxiedCommandSender.callee)
				}
			}
			literalArgument("native") {
				nativeExecutor { nativeProxyCommandSender, args ->
					val nativeLocation = nativeProxyCommandSender.location
					val location = Location(nativeProxyCommandSender.world, nativeLocation.x + 1, nativeLocation.y - 1, nativeLocation.z)
					location.block.breakNaturally()
				}
			}
			literalArgument("any") {
				anyExecutor { commandSender, args ->
					commandSender.sendMessage("Hello World!")
				}
			}
		}

		commandTree("requirementAndInstantExecutionTest", { sender: CommandSender -> (sender is Player) && sender.isOp }) {
			playerExecutor { player, _ ->
				player.sendMessage("Test erfolgreich!")
			}
		}

		commandTree("requirementAndArgumentExecutionTest", { sender: CommandSender -> (sender is Player) && sender.isOp }) {
			argument(StringArgument("test1").replaceSuggestions(ArgumentSuggestions.strings("one", "two", "three"))) {
				playerExecutor { player, _ ->
					player.sendMessage("Test erfolgreich!")
				}
			}
		}

		commandTree("instantExecutionTest") {
			playerExecutor { player, _ ->
				player.sendMessage("Test erfolgreich!")
			}
		}

		commandAPICommand("commandAPICommand") {
			argument(LiteralArgument.of("test1"))
			requirement(StringArgument("string"), { sender: CommandSender -> sender.isOp })
			playerExecutor { player, args ->
				player.sendMessage("You entered the string ${args[0] as String}")
			}
		}

		commandAPICommand("commandAPICommand") {
			argument(LiteralArgument.of("test1"))
			requirement(StringArgument("string"), { sender: CommandSender -> sender.isOp })
			stringArgument("string2") {
				replaceSuggestions(ArgumentSuggestions.strings("one", "two", "three"))
			}
			playerExecutor { player, args ->
				player.sendMessage("You entered the string ${args[0] as String} as first string!")
				player.sendMessage("You entered the string ${args[1] as String} as second string!")
			}
		}

		CommandAPICommand("argumentrequirementtest").withArguments(LiteralArgument.of("test4")).withArguments(StringArgument("requirement").withRequirement { sender: CommandSender -> sender.isOp }).withArguments(StringArgument("test5")).executesPlayer(PlayerCommandExecutor { player, args ->
				player.sendMessage("Normaler Command!")
			}).register()

		commandAPICommand("commandapicommandrequirementtest", { sender: CommandSender -> sender.isOp }) {
			literalArgument("function")
			playerExecutor { player, args ->
				player.sendMessage("Test functioniert!")
			}
		}

		CommandTree("reqs").then(LiteralArgument.of("test1").withRequirement { sender: CommandSender -> sender.isOp }.then(
					LiteralArgument.of("test1arg").executesPlayer(PlayerCommandExecutor { player, args ->
							player.sendMessage("Requirement works!")
						})
				)
			).then(
				LiteralArgument.of("test2").then(
						LiteralArgument.of("test2arg").executesPlayer(PlayerCommandExecutor { player, args ->
								player.sendMessage("Requirement works!")
							})
					)
			).register()
	}

}