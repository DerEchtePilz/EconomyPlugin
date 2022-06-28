package me.derechtepilz.economy.utility.config;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.*;
import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.events.DiscordValuesSetEvent;
import me.derechtepilz.economy.playermanager.permission.Permission;
import me.derechtepilz.economy.utility.ChatFormatter;
import me.derechtepilz.economy.utility.Language;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ConfigCommand {
    public void register() {
        new CommandTree("config")
                .then(new LiteralArgument("itemQuantities")
                        .then(new LiteralArgument("maxAmount")
                                .then(new IntegerArgument("maxAmount", 0)
                                        .executesPlayer((player, args) -> {
                                            if (!Permission.hasPermission(player, Permission.MODIFY_CONFIG)) {
                                                player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                                                return;
                                            }
                                            Config.set("itemQuantitiesMaxAmount", String.valueOf(args[0]));
                                            Config.reloadConfig();
                                            player.sendMessage(TranslatableChatComponent.read("configCommand.item_quantities.max_amount").replace("%s", ChatFormatter.valueOf((Integer) args[0])));
                                        })))
                        .then(new LiteralArgument("minAmount")
                                .then(new IntegerArgument("minAmount", 0)
                                        .executesPlayer((player, args) -> {
                                            if (!Permission.hasPermission(player, Permission.MODIFY_CONFIG)) {
                                                player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                                                return;
                                            }
                                            Config.set("itemQuantitiesMinAmount", String.valueOf(args[0]));
                                            Config.reloadConfig();
                                            player.sendMessage(TranslatableChatComponent.read("configCommand.item_quantities.min_amount").replace("%s", ChatFormatter.valueOf((Integer) args[0])));
                                        }))))
                .then(new LiteralArgument("itemPrice")
                        .then(new LiteralArgument("maxAmount")
                                .then(new IntegerArgument("maxAmount", 0)
                                        .executesPlayer((player, args) -> {
                                            if (!Permission.hasPermission(player, Permission.MODIFY_CONFIG)) {
                                                player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                                                return;
                                            }
                                            Config.set("itemPriceMaxAmount", String.valueOf(args[0]));
                                            Config.reloadConfig();
                                            player.sendMessage(TranslatableChatComponent.read("configCommand.item_price.max_amount").replace("%s", ChatFormatter.valueOf((Integer) args[0])));
                                        })))
                        .then(new LiteralArgument("minAmount")
                                .then(new IntegerArgument("minAmount", 0)
                                        .executesPlayer((player, args) -> {
                                            if (!Permission.hasPermission(player, Permission.MODIFY_CONFIG)) {
                                                player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                                                return;
                                            }
                                            Config.set("itemPriceMinAmount", String.valueOf(args[0]));
                                            Config.reloadConfig();
                                            player.sendMessage(TranslatableChatComponent.read("configCommand.item_price.min_amount").replace("%s", ChatFormatter.valueOf((Integer) args[0])));
                                        }))))
                .then(new LiteralArgument("startBalance")
                        .then(new DoubleArgument("startBalance", 0)
                                .executesPlayer((player, args) -> {
                                    if (!Permission.hasPermission(player, Permission.MODIFY_CONFIG)) {
                                        player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                                        return;
                                    }
                                    double startBalance = (double) args[0];
                                    Config.set("startBalance", String.valueOf(startBalance));
                                    Config.reloadConfig();
                                    player.sendMessage(TranslatableChatComponent.read("configCommand.start_balance").replace("%s", ChatFormatter.valueOf(startBalance)));
                                })))
                .then(new LiteralArgument("interest")
                        .then(new DoubleArgument("interest", -99)
                                .executesPlayer((player, args) -> {
                                    if (!Permission.hasPermission(player, Permission.MODIFY_CONFIG)) {
                                        player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                                        return;
                                    }
                                    double interest = (double) args[0];
                                    Config.set("interest", String.valueOf(interest));
                                    Config.reloadConfig();
                                    player.sendMessage(TranslatableChatComponent.read("configCommand.interest").replace("%s", ChatFormatter.valueOf(interest)));
                                })))
                .then(new LiteralArgument("language")
                        .then(new StringArgument("language").replaceSuggestions(ArgumentSuggestions.strings("en_us", "de_de"))
                                .executesPlayer((player, args) -> {
                                    if (!Permission.hasPermission(player, Permission.MODIFY_CONFIG)) {
                                        player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                                        return;
                                    }
                                    Config.set("language", args[0].toString().toUpperCase());
                                    Config.reloadConfig();
                                    Main.getInstance().setLanguage(Language.valueOf(args[0].toString().toUpperCase()));
                                    player.sendMessage(TranslatableChatComponent.read("configCommand.language").replace("%s", (String) args[0]));
                                })))
                .then(new LiteralArgument("discord")
                        .then(new StringArgument("guildId")
                                .then(new StringArgument("discordToken")
                                        .executesPlayer((player, args) -> {
                                            if (!Permission.hasPermission(player, Permission.MODIFY_CONFIG)) {
                                                player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                                                return;
                                            }
                                            Config.set("guildId", (String) args[0]);
                                            Config.set("discordToken", (String) args[1]);
                                            Config.reloadConfig();
                                            player.sendMessage(TranslatableChatComponent.read("configCommand.guildId").replace("%s", (String) args[0]));
                                            player.sendMessage(TranslatableChatComponent.read("configCommand.discordToken").replace("%s", (String) args[1]));

                                            Bukkit.getPluginManager().callEvent(new DiscordValuesSetEvent(player));
                                        }))))
                .then(new LiteralArgument("reset")
                        .executes((sender, args) -> {
                            if (sender instanceof Player player) {
                                if (!Permission.hasPermission(player, Permission.RESET_CONFIG)) {
                                    player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                                    return;
                                }
                                Config.resetConfig();
                                player.sendMessage(TranslatableChatComponent.read("configCommand.reset_config"));
                            } else {
                                Config.resetConfig();
                            }
                        }))
                .then(new LiteralArgument("reload")
                        .executes((sender, args) -> {
                            if (sender instanceof Player player) {
                                if (!Permission.hasPermission(player, Permission.MODIFY_CONFIG)) {
                                    player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                                    return;
                                }
                                Config.reloadConfig();
                                player.sendMessage(TranslatableChatComponent.read("configCommand.reload_config"));
                            } else {
                                Config.reloadConfig();
                            }
                        }))
                .register();
    }
}