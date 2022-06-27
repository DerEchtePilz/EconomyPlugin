package me.derechtepilz.economy.modules.discord.communication.minecraftserver;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.LongArgument;
import me.derechtepilz.economy.modules.discord.DiscordBot;
import me.derechtepilz.economy.playermanager.permission.Permission;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

import java.util.ArrayList;
import java.util.List;

public class DiscordCommand {
    public void register() {
        new CommandTree("discord")
                .then(new LiteralArgument("msg")
                        .then(new LongArgument("discordId")
                                .then(new GreedyStringArgument("msg")
                                        .executesPlayer((player, args) -> {
                                            if (!Permission.hasPermission(player, Permission.DISCORD_MESSAGE_USER)) {
                                                player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                                                return;
                                            }
                                            if (DiscordBot.getDiscordBot() != null) {
                                                if (DiscordBot.getDiscordBot().isActive()) {
                                                    long discordId = (long) args[0];
                                                    String message = (String) args[1];
                                                    String userName = DiscordBot.getDiscordBot().getGuild().getMemberById(discordId).getUser().getName()
                                                            + "#" + DiscordBot.getDiscordBot().getGuild().getMemberById(discordId).getUser().getDiscriminator();

                                                    DiscordBot.getDiscordBot().getJda().openPrivateChannelById(discordId).complete()
                                                            .sendMessage("**" + player.getName() + "**: " + message).queue(null, new ErrorHandler()
                                                                    .handle(ErrorResponse.CANNOT_SEND_TO_USER,
                                                                            (e) -> {
                                                                                player.sendMessage(TranslatableChatComponent.read("discordCommand.cannot_send_private_message")
                                                                                        .replace("%s", userName));
                                                                            }));
                                                    player.sendMessage(TranslatableChatComponent.read("discordCommand.sent_private_message")
                                                            .replace("%s", userName) + " " + message);
                                                }
                                            }
                                        }))))
                .then(new LiteralArgument("searchId")
                        .then(new GreedyStringArgument("query")
                                .executesPlayer((player, args) -> {
                                    if (!Permission.hasPermission(player, Permission.DISCORD_SEARCH_ID)) {
                                        player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                                        return;
                                    }
                                    if (DiscordBot.getDiscordBot() != null) {
                                        if (DiscordBot.getDiscordBot().isActive()) {
                                            String query = ((String) args[0]).toLowerCase();
                                            Guild guild = DiscordBot.getDiscordBot().getGuild();

                                            List<Member> searchResults = new ArrayList<>();
                                            for (Member member : guild.getMemberCache()) {
                                                // check if member is bot or system
                                                if (member.getUser().isSystem() || member.getUser().isBot()) {
                                                    continue;
                                                }
                                                // Search member name or nickname for query
                                                if (member.getEffectiveName().toLowerCase().contains(query)) {
                                                    searchResults.add(member);
                                                    continue;
                                                }

                                                // search member name in case they don't have a nickname
                                                if (member.getUser().getName().toLowerCase().contains(query)) {
                                                    searchResults.add(member);
                                                }
                                            }

                                            player.sendMessage(TranslatableChatComponent.read("discordCommand.search_members").replace("%s", String.valueOf(searchResults.size())));
                                            for (Member member : searchResults) {
                                                TextComponent component = new TextComponent();
                                                String s = "§6" + (searchResults.indexOf(member) + 1) + ". §a" + member.getUser().getName() + "#" + member.getUser().getDiscriminator() + " (ID: " + member.getId() + ")";
                                                component.setText(s);
                                                component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, member.getId()));
                                                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(TranslatableChatComponent.read("discordCommand.copy_id"))));
                                                player.spigot().sendMessage(component);
                                            }
                                        }
                                    }
                                })))
                .register();
    }
}
