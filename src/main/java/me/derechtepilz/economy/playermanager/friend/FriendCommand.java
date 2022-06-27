package me.derechtepilz.economy.playermanager.friend;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
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
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class FriendCommand implements ICooldown {

    private final HashMap<UUID, UUID> requestingPlayers = new HashMap<>();
    private final HashMap<UUID, Long> cooldown = new HashMap<>();

    public void register() {
        new CommandTree("friend")
                .withAliases("f")
                .then(new LiteralArgument("add")
                        .then(new Argument<Player>(Argument.ArgumentType.PLAYER_SINGLE).getArgument()
                                .executesPlayer((player, args) -> {
                                    if (!Permission.hasPermission(player, Permission.FRIEND)) {
                                        player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                                        return;
                                    }
                                    Player target = (Player) args[0];
                                    if (target.equals(player)) {
                                        player.sendMessage(TranslatableChatComponent.read("friendCommand.cannot_friend_yourself"));
                                        return;
                                    }
                                    if (requestingPlayers.containsKey(player.getUniqueId())) {
                                        player.sendMessage(TranslatableChatComponent.read("friendCommand.pending_request"));
                                        return;
                                    }

                                    requestingPlayers.put(player.getUniqueId(), target.getUniqueId());

                                    player.sendMessage(TranslatableChatComponent.read("friendCommand.sent_friend_request").replace("%s", target.getName()));
                                    target.sendMessage(TranslatableChatComponent.read("friendCommand.received_friend_request").replace("%s", player.getName()));

                                    TextComponent acceptFriendRequest = new TextComponent();
                                    acceptFriendRequest.setText(TranslatableChatComponent.read("friendCommand.accept_friend_request"));
                                    acceptFriendRequest.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(TranslatableChatComponent.read("friendCommand.accept_friend_request_hover").replace("%s", player.getName()))));
                                    acceptFriendRequest.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend accept " + player.getName()));

                                    TextComponent denyFriendRequest = new TextComponent();
                                    denyFriendRequest.setText(" " + TranslatableChatComponent.read("friendCommand.deny_friend_request"));
                                    denyFriendRequest.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(TranslatableChatComponent.read("friendCommand.deny_friend_request_hover").replace("%s", player.getName()))));
                                    denyFriendRequest.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend deny " + player.getName()));

                                    target.spigot().sendMessage(acceptFriendRequest, denyFriendRequest);

                                    Date date = Calendar.getInstance().getTime();
                                    long time = date.toInstant().plusSeconds(300).toEpochMilli();

                                    cooldown.put(player.getUniqueId(), time);
                                    Main.getInstance().getCooldownMap().put(player.getUniqueId(), this);

                                    Cooldown cooldown = new Cooldown(player, time);
                                    cooldown.setCancelTask(Bukkit.getScheduler().runTaskTimer(Main.getInstance(), cooldown, 0, 20));
                                })))
                .then(new LiteralArgument("remove")
                        .then(new OfflinePlayerArgument("friend")
                                .executesPlayer((player, args) -> {
                                    OfflinePlayer offlinePlayer = (OfflinePlayer) args[0];
                                    if (!Main.getInstance().getFriend().isFriend(player, offlinePlayer)) {
                                        player.sendMessage(TranslatableChatComponent.read("friendCommand.friend_not_found").replace("%s", offlinePlayer.getName()));
                                        return;
                                    }
                                    Main.getInstance().getFriend().removeFriend(player, offlinePlayer);
                                    player.sendMessage(TranslatableChatComponent.read("friendCommand.removed_friend").replace("%s", offlinePlayer.getName()));
                                })))
                .then(new LiteralArgument("list")
                        .executesPlayer((player, args) -> {

                        }))
                .then(new LiteralArgument("accept")
                        .then(new Argument<Player>(Argument.ArgumentType.PLAYER_SINGLE).getArgument()
                                .executesPlayer((player, args) -> {

                                })))
                .then(new LiteralArgument("deny")
                        .then(new Argument<Player>(Argument.ArgumentType.PLAYER_SINGLE).getArgument()
                                .executesPlayer((player, args) -> {

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

            player.sendMessage(TranslatableChatComponent.read("friendCommand.request_expired"));
            target.sendMessage(TranslatableChatComponent.read("friendCommand.request_expired"));

            return true;
        }
        return false;
    }
}
