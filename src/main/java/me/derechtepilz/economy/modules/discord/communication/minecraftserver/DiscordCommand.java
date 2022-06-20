package me.derechtepilz.economy.modules.discord.communication.minecraftserver;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.LongArgument;
import me.derechtepilz.economy.modules.discord.DiscordBot;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;

public class DiscordCommand {
    public DiscordCommand() {
        new CommandTree("discord")
                .then(new LiteralArgument("msg")
                        .then(new LongArgument("discordId")
                                .then(new GreedyStringArgument("msg")
                                        .executesPlayer((player, args) -> {
                                            if (DiscordBot.getDiscordBot() != null) {
                                                if (DiscordBot.getDiscordBot().isActive()) {
                                                    long discordId = (long) args[0];
                                                    String message = (String) args[1];
                                                    String userName = DiscordBot.getDiscordBot().getGuild().getMemberById(discordId).getUser().getName()
                                                                    + "#" + DiscordBot.getDiscordBot().getGuild().getMemberById(discordId).getUser().getDiscriminator();
                                                    try {
                                                        DiscordBot.getDiscordBot().getJda().openPrivateChannelById(discordId).complete().sendMessage("**" + player.getName() + "**: " + message).queue();
                                                        player.sendMessage(TranslatableChatComponent.read("discordCommand.sent_private_message")
                                                                .replace("%s", userName) + " " + message);
                                                    } catch (Exception e) {
                                                        player.sendMessage(TranslatableChatComponent.read("discordCommand.cannot_send_private_message")
                                                                .replace("%s", userName));
                                                    }
                                                }
                                            }
                                        }))))
                .then(new LiteralArgument("searchId")
                        .then(new GreedyStringArgument("query")
                                .executesPlayer((player, args) -> {
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
                                        component.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, member.getId()));
                                        player.spigot().sendMessage(component);
                                    }
                                })))
                .register();
    }

}
