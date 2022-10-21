package me.derechtepilz.economy.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.*;
import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.permissionmanagement.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;

import static dev.jorel.commandapi.arguments.LiteralArgument.of;

public class EconomyCommand {

    private final EconomyCommandExecution commandExecution;

    public EconomyCommand(Main main) {
        commandExecution = new EconomyCommandExecution(main);
    }

    public void register() {
        new CommandTree("economy")
            .then(of("auction")
                .then(of("buy")
                    .executesPlayer(commandExecution::buyAuction)
                    .then(new ItemStackArgument("filter")
                        .executesPlayer(commandExecution::buyAuctionWithFilter)
                    )
                )
                .then(of("create")
                    .then(new ItemStackArgument("item")
                        .then(new IntegerArgument("amount", 1)
                            .then(new DoubleArgument("price", 1)
                                .then(new IntegerArgument("hours", 0)
                                    .then(new IntegerArgument("minutes", 0)
                                        .then(new IntegerArgument("seconds", 0)
                                            .executesPlayer(commandExecution::createAuction)
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
                .then(of("cancel")
                    .executesPlayer(commandExecution::cancelAuction)
                )
                .then(of("claim")
                    .executesPlayer(commandExecution::claimAuction)
                )
                .then(of("pause")
                    .withRequirement(sender -> {
                        if (sender instanceof Player player) {
                            return Permission.hasPermission(player, Permission.PAUSE_RESUME_AUCTIONS) || player.isOp();
                        }
                        return false;
                    })
                    .executesPlayer(commandExecution::pauseAuction)
                )
                .then(of("resume")
                    .withRequirement(sender -> {
                        if (sender instanceof Player player) {
                            return Permission.hasPermission(player, Permission.PAUSE_RESUME_AUCTIONS) || player.isOp();
                        }
                        return false;
                    })
                    .executesPlayer(commandExecution::resumeAuction)
                )
            )
            .then(of("coins")
                .then(of("give")
                    .withRequirement(sender -> {
                        if (sender instanceof Player player) {
                            return Permission.hasPermission(player, Permission.GIVE_COINS) || player.isOp();
                        }
                        return false;
                    })
                    .then(new PlayerArgument("target")
                        .then(new DoubleArgument("amount", 0)
                            .executesPlayer(commandExecution::giveCoins)
                        )
                    )
                )
                .then(of("take")
                    .withRequirement(sender -> {
                        if (sender instanceof Player player) {
                            return Permission.hasPermission(player, Permission.TAKE_COINS) || player.isOp();
                        }
                        return false;
                    })
                    .then(new PlayerArgument("target")
                        .then(new DoubleArgument("amount", 0)
                            .executesPlayer(commandExecution::takeCoins)
                        )
                    )
                )
                .then(of("set")
                    .withRequirement(sender -> {
                        if (sender instanceof Player player) {
                            return Permission.hasPermission(player, Permission.SET_COINS) || player.isOp();
                        }
                        return false;
                    })
                    .then(new PlayerArgument("target")
                        .then(new DoubleArgument("amount", 0)
                            .executesPlayer(commandExecution::setCoins)
                        )
                    )
                )
                .then(of("baltop")
                    .executesPlayer(commandExecution::balTop)
                )
            )
            .then(of("permission")
                .withRequirement(ServerOperator::isOp)
                .then(of("clear")
                    .then(new PlayerArgument("player").replaceSuggestions(ArgumentSuggestions.strings(suggestionInfo -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toArray(String[]::new)))
                        .executesPlayer(commandExecution::clearPermissions)
                    )
                )
                .then(of("single")
                    .then(of("set")
                        .then(new PlayerArgument("player").replaceSuggestions(ArgumentSuggestions.strings(suggestionInfo -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toArray(String[]::new)))
                            .then(new ListArgumentBuilder<String>("permissions", ",").allowDuplicates(false).withList(Permission.getPermissions()).withStringMapper().build()
                                .withRequirement(ServerOperator::isOp)
                                .executesPlayer(commandExecution::setSinglePermission)
                            )
                        )
                    )
                    .then(of("remove")
                        .then(new PlayerArgument("player").replaceSuggestions(ArgumentSuggestions.strings(suggestionInfo -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toArray(String[]::new)))
                            .then(new ListArgumentBuilder<String>("permissions", ",").allowDuplicates(false).withList(Permission.getPermissions()).withStringMapper().build()
                                .withRequirement(ServerOperator::isOp)
                                .executesPlayer(commandExecution::removeSinglePermission)
                            )
                        )
                    )
                    .then(of("get")
                        .then(new PlayerArgument("player").replaceSuggestions(ArgumentSuggestions.strings(suggestionInfo -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toArray(String[]::new)))
                            .then(new ListArgumentBuilder<String>("permissions", ",").allowDuplicates(false).withList(Permission.getPermissions()).withStringMapper().build()
                                .withRequirement(ServerOperator::isOp)
                                .executesPlayer(commandExecution::getSinglePermission)
                            )
                        )
                    )
                )
            )
            .then(of("friend")
                .then(of("add")
                    .then(new PlayerArgument("target").replaceSuggestions(ArgumentSuggestions.strings(suggestionInfo -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toArray(String[]::new)))
                        .executesPlayer(commandExecution::addFriend)
                    )
                )
                .then(of("remove")
                    .then(new PlayerArgument("target").replaceSuggestions(ArgumentSuggestions.strings(suggestionInfo -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toArray(String[]::new)))
                        .executesPlayer(commandExecution::removeFriend)
                    )
                )
                .then(of("accept")
                    .then(new PlayerArgument("target").replaceSuggestions(ArgumentSuggestions.strings(suggestionInfo -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toArray(String[]::new)))
                        .executesPlayer(commandExecution::acceptFriend)
                    )
                )
                .then(of("deny")
                    .then(new PlayerArgument("target").replaceSuggestions(ArgumentSuggestions.strings(suggestionInfo -> Bukkit.getOnlinePlayers().stream().map(Player::getName).toArray(String[]::new)))
                        .executesPlayer(commandExecution::denyFriend)
                    )
                )
            )
            .then(of("config")
                .then(of("allowDirectDownloads")
                    .withRequirement(sender -> {
                        if (sender instanceof Player player) {
                            return Permission.hasPermission(player, Permission.MODIFY_CONFIG) || player.isOp();
                        }
                        return false;
                    })
                    .then(new BooleanArgument("allowDirectDownloads")
                        .executesPlayer(commandExecution::allowDirectDownloads)
                    )
                )
                .then(of("startBalance")
                    .withRequirement(sender -> {
                        if (sender instanceof Player player) {
                            return Permission.hasPermission(player, Permission.MODIFY_CONFIG) || player.isOp();
                        }
                        return false;
                    })
                    .then(new DoubleArgument("startBalance", 0.0)
                        .executesPlayer(commandExecution::startBalance)
                    )
                )
                .then(of("interestRate")
                    .withRequirement(sender -> {
                        if (sender instanceof Player player) {
                            return Permission.hasPermission(player, Permission.MODIFY_CONFIG) || player.isOp();
                        }
                        return false;
                    })
                    .then(new DoubleArgument("interestRate", 0.0)
                        .executesPlayer(commandExecution::interestRate)
                    )
                )
                .then(of("minimumDaysForInterest")
                    .withRequirement(sender -> {
                        if (sender instanceof Player player) {
                            return Permission.hasPermission(player, Permission.MODIFY_CONFIG) || player.isOp();
                        }
                        return false;
                    })
                    .then(new IntegerArgument("minimumDaysForInterest", 1)
                        .executesPlayer(commandExecution::minimumDaysForInterest)
                    )
                )
                .then(of("reset")
                    .withRequirement(sender -> {
                        if (sender instanceof Player player) {
                            return Permission.hasPermission(player, Permission.RESET_CONFIG) || player.isOp();
                        }
                        return false;
                    })
                    .executesPlayer(commandExecution::resetConfig)
                )
                .then(of("reload")
                    .withRequirement(sender -> {
                        if (sender instanceof Player player) {
                            return Permission.hasPermission(player, Permission.RESET_CONFIG) || player.isOp();
                        }
                        return false;
                    })
                    .executesPlayer(commandExecution::reloadConfig)
                )
            )
            .register();
    }
}
