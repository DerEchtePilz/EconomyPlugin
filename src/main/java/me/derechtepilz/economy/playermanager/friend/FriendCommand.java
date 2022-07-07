package me.derechtepilz.economy.playermanager.friend;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.IntegerArgument;
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

import java.util.*;

public class FriendCommand implements ICooldown {

    private final Map<UUID, List<UUID>> requestingPlayers = new HashMap<>();
    private final Map<UUID, List<UUID>> receivingPlayers = new HashMap<>();
    private final Map<UUID, List<List<String>>> formattedFriendPages = new HashMap<>();
    private final Map<UUID, List<Cooldown>> currentCooldowns = new HashMap<>();

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
                                    if (currentCooldowns.containsKey(player.getUniqueId())) {
                                        for (Cooldown cooldown : currentCooldowns.get(player.getUniqueId())) {
                                            if (cooldown.target().equals(target)) {
                                                return;
                                            }
                                        }
                                    }

                                    List<UUID> requestedFriends = (requestingPlayers.containsKey(player.getUniqueId())) ? requestingPlayers.get(player.getUniqueId()) : new ArrayList<>();
                                    requestedFriends.add(target.getUniqueId());
                                    requestingPlayers.put(player.getUniqueId(), requestedFriends);

                                    List<UUID> requestingFriends = (receivingPlayers.containsKey(target.getUniqueId())) ? receivingPlayers.get(target.getUniqueId()) : new ArrayList<>();
                                    requestingFriends.add(player.getUniqueId());
                                    receivingPlayers.put(target.getUniqueId(), requestingFriends);

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

                                    Cooldown cooldown = new Cooldown(player, target, Calendar.getInstance().getTime().toInstant().plusSeconds(300).toEpochMilli(), this);
                                    cooldown.setCancelTask(Bukkit.getScheduler().runTaskTimer(Main.getInstance(), cooldown, 0, 20));

                                    List<Cooldown> cooldownList = new ArrayList<>();
                                    cooldownList.add(cooldown);
                                    currentCooldowns.put(player.getUniqueId(), cooldownList);
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
                            formatFriendPages(Main.getInstance().getFriend().getFriends(player), player);

                            TextComponent currentPage = new TextComponent(" " + TranslatableChatComponent.read("friendCommand.friend_pages_title").replace("%s", String.valueOf(1)) + " ");

                            TextComponent nextPage = new TextComponent(">>");
                            nextPage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(TranslatableChatComponent.read("friendCommand.next_page"))));
                            nextPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend list " + 1));

                            if (formattedFriendPages.get(player.getUniqueId()).size() - 1 != 0) {
                                player.spigot().sendMessage(currentPage, nextPage);
                                for (String friend : formattedFriendPages.get(player.getUniqueId()).get(0)) {
                                    player.sendMessage("§6" + friend);
                                }
                                return;
                            }
                            if (formattedFriendPages.get(player.getUniqueId()).size() - 1 == 0) {
                                player.spigot().sendMessage(currentPage);
                                for (String friend : formattedFriendPages.get(player.getUniqueId()).get(0)) {
                                    player.sendMessage("§6" + friend);
                                }
                            }
                        })
                        .then(new IntegerArgument("page", 0)
                                .executesPlayer((player, args) -> {
                                    formatFriendPages(Main.getInstance().getFriend().getFriends(player), player);
                                    int page = (int) args[0];
                                    if (page > formattedFriendPages.get(player.getUniqueId()).size() - 1) {
                                        int maxPages = formattedFriendPages.get(player.getUniqueId()).size() - 1;
                                        player.sendMessage(TranslatableChatComponent.read("friendCommand.page_not_found").replace("%%s", String.valueOf(page)).replace("%s", String.valueOf(maxPages)));
                                        return;
                                    }

                                    TextComponent previousPage = new TextComponent("<<");
                                    previousPage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(TranslatableChatComponent.read("friendCommand.previous_page"))));
                                    previousPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend list " + (page - 1)));

                                    TextComponent currentPage = new TextComponent(" " + TranslatableChatComponent.read("friendCommand.friend_pages_title").replace("%s", String.valueOf(page + 1)) + " ");

                                    TextComponent nextPage = new TextComponent(">>");
                                    nextPage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(TranslatableChatComponent.read("friendCommand.next_page"))));
                                    nextPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend list " + (page + 1)));

                                    if (page == 0 && formattedFriendPages.get(player.getUniqueId()).size() - 1 != 0) {
                                        player.spigot().sendMessage(currentPage, nextPage);
                                        for (String friend : formattedFriendPages.get(player.getUniqueId()).get(0)) {
                                            player.sendMessage("§6" + friend);
                                        }
                                        return;
                                    }
                                    if (page == 0 && formattedFriendPages.get(player.getUniqueId()).size() - 1 == 0) {
                                        player.spigot().sendMessage(currentPage);
                                        for (String friend : formattedFriendPages.get(player.getUniqueId()).get(0)) {
                                            player.sendMessage("§6" + friend);
                                        }
                                        return;
                                    }
                                    if (page > 0 && page < formattedFriendPages.get(player.getUniqueId()).size() - 1) {
                                        player.spigot().sendMessage(previousPage, currentPage, nextPage);
                                        for (String friend : formattedFriendPages.get(player.getUniqueId()).get(0)) {
                                            player.sendMessage("§6" + friend);
                                        }
                                        return;
                                    }
                                    if (page == formattedFriendPages.get(player.getUniqueId()).size() - 1) {
                                        player.spigot().sendMessage(previousPage, currentPage);
                                        for (String friend : formattedFriendPages.get(player.getUniqueId()).get(0)) {
                                            player.sendMessage("§6" + friend);
                                        }
                                    }
                                })))
                .then(new LiteralArgument("accept")
                        .then(new Argument<Player>(Argument.ArgumentType.PLAYER_SINGLE).getArgument()
                                .executesPlayer((player, args) -> {
                                    Player target = null;
                                    if (receivingPlayers.containsKey(player.getUniqueId())) {
                                        for (UUID uuid : receivingPlayers.get(player.getUniqueId())) {
                                            target = (((Player) args[0]).getUniqueId().equals(uuid)) ? (Player) args[0] : null;
                                        }
                                        if (target != null) {
                                            Main.getInstance().getFriend().addFriend(player, target);
                                            Main.getInstance().getFriend().addFriend(target, player);

                                            player.sendMessage(TranslatableChatComponent.read("friendCommand.friend_request_accepted").replace("%s", target.getName()));
                                            target.sendMessage(TranslatableChatComponent.read("friendCommand.friend_request_accepted").replace("%s", player.getName()));

                                            List<UUID> requestedFriends = requestingPlayers.get(player.getUniqueId());
                                            requestedFriends.remove(target.getUniqueId());
                                            requestingPlayers.put(player.getUniqueId(), requestedFriends);

                                            List<UUID> requestingFriends = receivingPlayers.get(target.getUniqueId());
                                            requestingFriends.remove(player.getUniqueId());
                                            receivingPlayers.put(target.getUniqueId(), requestingFriends);

                                            // Assuming that "player" can't be the key here because this method can only be called from a context where "player" is the one who receives the request and "target" is the one who sent the request
                                            // Therefore "player" could be a key for another cooldown but not for the one which is required here
                                            for (Cooldown cooldown : currentCooldowns.get(target.getUniqueId())) {
                                                if (cooldown.target().equals(player)) {
                                                    cooldown.getToCancel().cancel();

                                                    List<Cooldown> cooldownList = currentCooldowns.get(target.getUniqueId());
                                                    cooldownList.remove(cooldown);
                                                    currentCooldowns.put(target.getUniqueId(), cooldownList);
                                                }
                                            }
                                        } else {
                                            player.sendMessage(TranslatableChatComponent.read("friendCommand.accepting_failed").replace("%s", ((Player) args[0]).getName()));
                                        }
                                    } else {
                                        player.sendMessage(TranslatableChatComponent.read("friendCommand.accepting_failed").replace("%s", ((Player) args[0]).getName()));
                                    }
                                })))
                .then(new LiteralArgument("deny")
                        .then(new Argument<Player>(Argument.ArgumentType.PLAYER_SINGLE).getArgument()
                                .executesPlayer((player, args) -> {
                                    Player target = null;
                                    if (receivingPlayers.containsKey(player.getUniqueId())) {
                                        for (UUID uuid : receivingPlayers.get(player.getUniqueId())) {
                                            target = (((Player) args[0]).getUniqueId().equals(uuid)) ? (Player) args[0] : null;
                                        }
                                        if (target != null) {
                                            player.sendMessage(TranslatableChatComponent.read("friendCommand.friend_request_denied").replace("%s", target.getName()));
                                            target.sendMessage(TranslatableChatComponent.read("friendCommand.friend_request_denied").replace("%s", target.getName()));

                                            List<UUID> requestedFriends = requestingPlayers.get(player.getUniqueId());
                                            requestedFriends.remove(target.getUniqueId());
                                            requestingPlayers.put(player.getUniqueId(), requestedFriends);

                                            List<UUID> requestingFriends = receivingPlayers.get(target.getUniqueId());
                                            requestingFriends.remove(player.getUniqueId());
                                            receivingPlayers.put(target.getUniqueId(), requestingFriends);

                                            // Assuming that "player" can't be the key here because this method can only be called from a context where "player" is the one who receives the request and "target" is the one who sent the request
                                            // Therefore "player" could be a key for another cooldown but not for the one which is required here
                                            for (Cooldown cooldown : currentCooldowns.get(target.getUniqueId())) {
                                                if (cooldown.target().equals(player)) {
                                                    cooldown.getToCancel().cancel();

                                                    List<Cooldown> cooldownList = currentCooldowns.get(target.getUniqueId());
                                                    cooldownList.remove(cooldown);
                                                    currentCooldowns.put(target.getUniqueId(), cooldownList);
                                                }
                                            }
                                        } else {
                                            player.sendMessage(TranslatableChatComponent.read("friendCommand.denying_failed").replace("%s", ((Player) args[0]).getName()));
                                        }
                                    } else {
                                        player.sendMessage(TranslatableChatComponent.read("friendCommand.denying_failed").replace("%s", ((Player) args[0]).getName()));
                                    }
                                })))
                .register();
    }

    public void formatFriendPages(List<String> friends, Player player) {
        List<List<String>> friendPages = new ArrayList<>();
        if (friends.size() <= 10) {
            friendPages.add(friends);

        }
        while (friends.size() > 1) {
            List<String> friendPage = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                friendPage.add(friends.get(i));
            }
            for (String friend : friendPage) {
                friends.remove(friend);
            }
            friendPages.add(friendPage);
        }
        formattedFriendPages.put(player.getUniqueId(), friendPages);
    }

    @Override
    public boolean checkDate(Player player, Player target, Cooldown cooldown) {
        if (System.currentTimeMillis() >= cooldown.endTime()) {

            List<UUID> requestedFriends = requestingPlayers.get(player.getUniqueId());
            requestedFriends.remove(target.getUniqueId());
            requestingPlayers.put(player.getUniqueId(), requestedFriends);

            List<UUID> requestingFriends = receivingPlayers.get(target.getUniqueId());
            requestingFriends.remove(player.getUniqueId());
            receivingPlayers.put(target.getUniqueId(), requestingFriends);

            player.sendMessage(TranslatableChatComponent.read("friendCommand.request_expired"));
            target.sendMessage(TranslatableChatComponent.read("friendCommand.request_expired"));

            return true;
        }
        return false;
    }
}
