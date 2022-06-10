package me.derechtepilz.economy.playermanager;

import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TradeCommand {

    private final HashMap<UUID, UUID> requestingPlayers = new HashMap<>();

    public TradeCommand() {
        new CommandTree("trade")
                .withPermission(CommandPermission.NONE)
                .then(new PlayerArgument("player").replaceSuggestions(ArgumentSuggestions.strings(getPlayers()))
                        .executesPlayer((player, args) -> {
                            if (!Permission.hasPermission(player, Permission.TRADE)) {
                                player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                                return;
                            }
                            Player target = (Player) args[0];
                            if (requestingPlayers.containsKey(player.getUniqueId())) {
                                player.sendMessage(TranslatableChatComponent.read("tradeCommand.pending_request"));
                                return;
                            }
                            requestingPlayers.put(player.getUniqueId(), target.getUniqueId());
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

                                }))
                        .then(new LiteralArgument("deny")
                                .executesPlayer((player, args) -> {
                                    if (!Permission.hasPermission(player, Permission.TRADE)) {
                                        player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                                        return;
                                    }
                                })))
                .register();
    }

    private String[] getPlayers() {
        List<String> players = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            players.add(player.getName());
        }
        String[] suggestions = new String[players.size()];
        for (int i = 0; i < players.size(); i++) {
            suggestions[i] = players.get(i);
        }
        return suggestions;
    }
}