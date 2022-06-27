package me.derechtepilz.economy.playermanager;

import dev.jorel.commandapi.ArgumentTree;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.LiteralArgument;
import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.playermanager.permission.Permission;
import me.derechtepilz.economy.utility.Argument;
import me.derechtepilz.economy.utility.Cooldown;
import me.derechtepilz.economy.utility.ICooldown;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class TradeCommand implements ICooldown {

    private final HashMap<UUID, UUID> requestingPlayers = new HashMap<>();
    private final HashMap<UUID, Long> cooldown = new HashMap<>();

    public void register() {
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

                            Date date = Calendar.getInstance().getTime();
                            long time = date.toInstant().plusSeconds(300).toEpochMilli();

                            cooldown.put(player.getUniqueId(), time);
                            Main.getInstance().getCooldownMap().put(player.getUniqueId(), this);

                            Cooldown cooldown = new Cooldown(player, time);
                            cooldown.setCancelTask(Bukkit.getScheduler().runTaskTimer(Main.getInstance(), cooldown, 0, 20));
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

    @Override
    public boolean checkDate(Player player, Cooldown cooldown) {
        if (System.currentTimeMillis() >= cooldown.endTime()) {
            this.cooldown.remove(cooldown.player().getUniqueId());

            Player target = Bukkit.getPlayer(requestingPlayers.get(player.getUniqueId()));
            requestingPlayers.remove(player.getUniqueId());
            Main.getInstance().getCooldownMap().remove(player.getUniqueId());

            player.sendMessage(TranslatableChatComponent.read("tradeCommand.request_expired"));
            target.sendMessage(TranslatableChatComponent.read("tradeCommand.request_expired"));

            return true;
        }
        return false;
    }
}