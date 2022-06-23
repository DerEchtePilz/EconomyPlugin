package me.derechtepilz.economy.playermanager;

import dev.jorel.commandapi.ArgumentTree;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.LiteralArgument;
import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.playermanager.permission.Permission;
import me.derechtepilz.economy.utility.Argument;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class TradeCommand {

    private final HashMap<UUID, UUID> requestingPlayers = new HashMap<>();

    public TradeCommand() {
        new CommandTree("trade")
                .then(new ArgumentTree(new Argument<Player>(Argument.ArgumentType.PLAYER_SINGLE).getArgument())
                        .executesPlayer((player, args) -> {
                            if (!Permission.hasPermission(player, Permission.TRADE)) {
                                player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                                return;
                            }
                            Player target = (Player) args[0];
                            if (target.equals(player)) {
                                player.sendMessage(TranslatableChatComponent.read("tradeCommand.cannot_trade_with_yourself"));
                                return;
                            }
                            if (requestingPlayers.containsKey(player.getUniqueId())) {
                                player.sendMessage(TranslatableChatComponent.read("tradeCommand.pending_request"));
                                return;
                            }
                            requestingPlayers.put(player.getUniqueId(), target.getUniqueId());
                            player.sendMessage(TranslatableChatComponent.read("tradeCommand.trade_request_sent").replace("%s", target.getName()));
                            target.sendMessage(TranslatableChatComponent.read("tradeCommand.incoming_trade_request").replace("%s", player.getName()));

                            TextComponent acceptTradeRequest = new TextComponent();
                            acceptTradeRequest.setText(TranslatableChatComponent.read("tradeCommand.accept_request"));
                            acceptTradeRequest.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(TranslatableChatComponent.read("tradeCommand.accept_request_hover").replace("%s", player.getName()))));
                            acceptTradeRequest.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/trade " + player.getName() + " accept"));

                            TextComponent denyTradeRequest = new TextComponent();
                            denyTradeRequest.setText(" " + TranslatableChatComponent.read("tradeCommand.deny_request"));
                            denyTradeRequest.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(TranslatableChatComponent.read("tradeCommand.deny_request_hover").replace("%s", player.getName()))));
                            denyTradeRequest.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/trade " + player.getName() + " deny"));

                            target.spigot().sendMessage(acceptTradeRequest, denyTradeRequest);
                        })
                        .then(new LiteralArgument("accept")
                                .executesPlayer((player, args) -> {
                                    if (!Permission.hasPermission(player, Permission.TRADE)) {
                                        player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                                        return;
                                    }
                                    Player target = (Player) args[0];
                                    if (!requestingPlayers.containsKey(target.getUniqueId())) {
                                        player.sendMessage(TranslatableChatComponent.read("tradeCommand.target_did_not_request").replace("%s", target.getName()));
                                        return;
                                    }

                                    // target sent a request, it is not sure that player is the player target wants to trade with
                                    if (!requestingPlayers.get(target.getUniqueId()).equals(player.getUniqueId())) {
                                        player.sendMessage(TranslatableChatComponent.read("tradeCommand.target_did_not_request_player").replace("%s", target.getName()));
                                        return;
                                    }

                                    // target sent a trade request to player, so we accept it
                                    Main.getInstance().getTradeMenu().openTradeMenu(target, player);
                                    requestingPlayers.remove(target.getUniqueId());
                                }))
                        .then(new LiteralArgument("deny")
                                .executesPlayer((player, args) -> {
                                    if (!Permission.hasPermission(player, Permission.TRADE)) {
                                        player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                                        return;
                                    }
                                    Player target = (Player) args[0];
                                    if (!requestingPlayers.containsKey(target.getUniqueId())) {
                                        player.sendMessage(TranslatableChatComponent.read("tradeCommand.target_did_not_request").replace("%s", target.getName()));
                                        return;
                                    }

                                    // target sent a request, it is not sure that player is the player target wants to trade with
                                    if (!requestingPlayers.get(target.getUniqueId()).equals(player.getUniqueId())) {
                                        player.sendMessage(TranslatableChatComponent.read("tradeCommand.target_did_not_request_player").replace("%s", target.getName()));
                                        return;
                                    }

                                    // target sent a trade request to player, so we deny it
                                    target.sendMessage(TranslatableChatComponent.read("tradeCommand.trade_was_denied").replace("%s", player.getName()));
                                    player.sendMessage(TranslatableChatComponent.read("tradeCommand.trade_denied"));

                                    requestingPlayers.remove(target.getUniqueId());
                                })))
                .register();
    }
}