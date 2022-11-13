package io.github.derechtepilz.economy.commands

import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.ListArgumentBuilder
import dev.jorel.commandapi.arguments.LiteralArgument.of
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.arguments.StringArgument
import io.github.derechtepilz.economy.Main
import io.github.derechtepilz.economy.permissionmanagement.Permission
import io.github.derechtepilz.economy.permissionmanagement.PermissionGroup
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class EconomyCommand(main: Main) {
    private val commandExecution: EconomyCommandExecution = EconomyCommandExecution(main)

    fun register() {
        commandTree("economy") {
            literalArgument("auction") {
                literalArgument("buy") {
                    playerExecutor { player, args ->
                        commandExecution.buyAuction(player, args)
                    }
                    itemStackArgument("filter") {
                        playerExecutor { player, args ->
                            commandExecution.buyAuctionWithFilter(player, args)
                        }
                    }
                }
                literalArgument("create") {
                    itemStackArgument("item") {
                        integerArgument("amount", 1) {
                            doubleArgument("price", 1.0) {
                                integerArgument("hours", 0) {
                                    integerArgument("minutes", 0) {
                                        integerArgument("seconds", 0) {
                                            playerExecutor { player, args ->
                                                commandExecution.createAuction(player, args)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                literalArgument("cancel") {
                    playerExecutor { player, args ->
                        commandExecution.cancelAuction(player, args)
                    }
                }
                literalArgument("claim") {
                    playerExecutor { player, args ->
                        commandExecution.claimAuction(player, args)
                    }
                }
                requirement(of("pause"), { sender: CommandSender -> sender is Player && Permission.hasPermission(sender, Permission.PAUSE_RESUME_AUCTIONS) || sender.isOp }) {
                    playerExecutor { player, args ->
                        commandExecution.pauseAuction(player, args)
                    }
                }
                requirement(of("resume"), { sender: CommandSender -> sender is Player && Permission.hasPermission(sender, Permission.PAUSE_RESUME_AUCTIONS) || sender.isOp }) {
                    playerExecutor { player, args ->
                        commandExecution.resumeAuction(player, args)
                    }
                }
            }
            literalArgument("coins") {
                requirement(of("give"), { sender: CommandSender -> sender is Player && Permission.hasPermission(sender, Permission.GIVE_COINS) || sender.isOp }) {
                    playerArgument("target") {
                        doubleArgument("amount", 0.0) {
                            playerExecutor { player, args ->
                                commandExecution.giveCoins(player, args)
                            }
                        }
                    }
                }
                requirement(of("take"), { sender: CommandSender -> sender is Player && Permission.hasPermission(sender, Permission.TAKE_COINS) || sender.isOp }) {
                    playerArgument("target") {
                        doubleArgument("amount", 0.0) {
                            playerExecutor { player, args ->
                                commandExecution.takeCoins(player, args)
                            }
                        }
                    }
                }
                requirement(of("set"), { sender: CommandSender -> sender is Player && Permission.hasPermission(sender, Permission.SET_COINS) || sender.isOp }) {
                    playerArgument("target") {
                        doubleArgument("amount", 0.0) {
                            playerExecutor { player, args ->
                                commandExecution.setCoins(player, args)
                            }
                        }
                    }
                }
                literalArgument("baltop") {
                    playerExecutor { player, args ->
                        commandExecution.balTop(player, args)
                    }
                }
            }
            requirement(of("permission"), { obj: CommandSender -> obj.isOp }) {
                literalArgument("clear") {
                    argument(PlayerArgument("player").replaceSuggestions(ArgumentSuggestions.strings { Bukkit.getOnlinePlayers().stream().map { obj: Player -> obj.name }.toList().toTypedArray() })) {
                        playerExecutor { player, args ->
                            commandExecution.clearPermissions(player, args)
                        }
                    }
                }
                literalArgument("single") {
                    literalArgument("set") {
                        argument(PlayerArgument("player").replaceSuggestions(ArgumentSuggestions.strings{ Bukkit.getOnlinePlayers().stream().map { obj: Player -> obj.name }.toList().toTypedArray() })) {
                            argument(ListArgumentBuilder<String>("permission", ",").allowDuplicates(false).withList(Permission.getPermissions()).withStringMapper().buildGreedy()) {
                                playerExecutor { player, args ->
                                    commandExecution.setSinglePermission(player, args)
                                }
                            }
                        }
                    }
                    literalArgument("remove") {
                        argument(PlayerArgument("player").replaceSuggestions(ArgumentSuggestions.strings{ Bukkit.getOnlinePlayers().stream().map { obj: Player -> obj.name }.toList().toTypedArray() })) {
                            argument(ListArgumentBuilder<String>("permission", ",").allowDuplicates(false).withList(Permission.getPermissions()).withStringMapper().buildGreedy()) {
                                playerExecutor { player, args ->
                                    commandExecution.removeSinglePermission(player, args)
                                }
                            }
                        }
                    }
                }
                literalArgument("group") {
                    literalArgument("set") {
                        argument(PlayerArgument("player").replaceSuggestions(ArgumentSuggestions.strings { Bukkit.getOnlinePlayers().stream().map { obj: Player -> obj.name }.toList().toTypedArray() })) {
                            argument(ListArgumentBuilder<String>("permissionGroup", ",").allowDuplicates(false).withList(PermissionGroup.getPermissionGroups()).withStringMapper().buildGreedy()) {
                                playerExecutor { player, args ->
                                    commandExecution.setPermissionGroup(player, args)
                                }
                            }
                        }
                    }
                    literalArgument("remove") {
                        argument(PlayerArgument("player").replaceSuggestions(ArgumentSuggestions.strings { Bukkit.getOnlinePlayers().stream().map { obj: Player -> obj.name }.toList().toTypedArray() })) {
                            argument(ListArgumentBuilder<String>("permissionGroup", ",").allowDuplicates(false).withList(PermissionGroup.getPermissionGroups()).withStringMapper().buildGreedy()) {
                                playerExecutor { player, args ->
                                    commandExecution.removePermissionGroup(player, args)
                                }
                            }
                        }
                    }
                    literalArgument("register") {
                        stringArgument("name") {
                            argument(ListArgumentBuilder<String>("permissions", ",").allowDuplicates(false).withList(Permission.getPermissions()).withStringMapper().buildGreedy()) {
                                playerExecutor { player, args ->
                                    commandExecution.registerPermissionGroup(player, args)
                                }
                            }
                        }
                    }
                    literalArgument("delete") {
                        argument(StringArgument("permissionGroup").replaceSuggestions(ArgumentSuggestions.strings { PermissionGroup.getPermissionGroups().toTypedArray() })) {
                            playerExecutor { player, args ->
                                commandExecution.deletePermissionGroup(player, args)
                            }
                        }
                    }
                }
                literalArgument("get") {
                    argument(PlayerArgument("player").replaceSuggestions(ArgumentSuggestions.strings { Bukkit.getOnlinePlayers().stream().map { obj: Player -> obj.name }.toList().toTypedArray() })) {
                        playerExecutor { player, args ->
                            commandExecution.getSinglePermission(player, args)
                        }
                    }
                }
            }
            literalArgument("friend") {
                literalArgument("add") {
                    argument(PlayerArgument("target").replaceSuggestions(ArgumentSuggestions.strings {Bukkit.getOnlinePlayers().stream().map { player: Player -> player.name }.toList().toTypedArray()})) {
                        playerExecutor { player, args ->
                            commandExecution.addFriend(player, args)
                        }
                    }
                }
                literalArgument("remove") {
                    argument(PlayerArgument("target").replaceSuggestions(ArgumentSuggestions.strings {Bukkit.getOnlinePlayers().stream().map { player: Player -> player.name }.toList().toTypedArray()})) {
                        playerExecutor { player, args ->
                            commandExecution.removeFriend(player, args)
                        }
                    }
                }
                literalArgument("accept") {
                    argument(PlayerArgument("target").replaceSuggestions(ArgumentSuggestions.strings {Bukkit.getOnlinePlayers().stream().map { player: Player -> player.name }.toList().toTypedArray()})) {
                        playerExecutor { player, args ->
                            commandExecution.acceptFriend(player, args)
                        }
                    }
                }
                literalArgument("deny") {
                    argument(PlayerArgument("target").replaceSuggestions(ArgumentSuggestions.strings {Bukkit.getOnlinePlayers().stream().map { player: Player -> player.name }.toList().toTypedArray()})) {
                        playerExecutor { player, args ->
                            commandExecution.denyFriend(player, args)
                        }
                    }
                }
            }
            literalArgument("config") {
                requirement(of("allowDirectDownloads"), { sender: CommandSender -> sender is Player && Permission.hasPermission(sender, Permission.MODIFY_CONFIG) || sender.isOp }) {
                    booleanArgument("allowDirectDownloads") {
                        playerExecutor { player, args ->
                            commandExecution.allowDirectDownloads(player, args)
                        }
                    }
                }
                requirement(of("startBalance"), { sender: CommandSender -> sender is Player && Permission.hasPermission(sender, Permission.MODIFY_CONFIG) || sender.isOp }) {
                    doubleArgument("startBalance", 0.0) {
                        playerExecutor { player, args ->
                            commandExecution.startBalance(player, args)
                        }
                    }
                }
                requirement(of("interestRate"), { sender: CommandSender -> sender is Player && Permission.hasPermission(sender, Permission.MODIFY_CONFIG) || sender.isOp }) {
                    doubleArgument("interestRate", 0.0) {
                        playerExecutor { player, args ->
                            commandExecution.interestRate(player, args)
                        }
                    }
                }
                requirement(of("minimumDaysForInterest"), { sender: CommandSender -> sender is Player && Permission.hasPermission(sender, Permission.MODIFY_CONFIG) || sender.isOp }) {
                    integerArgument("minimumDaysForInterest", 1) {
                        playerExecutor { player, args ->
                            commandExecution.minimumDaysForInterest(player, args)
                        }
                    }
                }
                requirement(of("reset"), { sender: CommandSender -> (sender is Player) && Permission.hasPermission(sender, Permission.RESET_CONFIG) || sender.isOp }) {
                    playerExecutor { player, args ->
                        commandExecution.resetConfig(player, args)
                    }
                }
                requirement(of("reload"), { sender: CommandSender -> (sender is Player) && Permission.hasPermission(sender, Permission.RESET_CONFIG) || sender.isOp }) {
                    playerExecutor { player, args ->
                        commandExecution.reloadConfig(player, args)
                    }
                }
            }
        }
    }
}