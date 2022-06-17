package me.derechtepilz.economy.modules.discord.communication.discordserver;

import me.derechtepilz.economy.modules.discord.DiscordBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class ChattingFromDiscordServer extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (DiscordBot.getDiscordBot().isActive() && event.getChannel().equals(DiscordBot.getDiscordBot().getMinecraftChat())) {
            if (event.getAuthor().isBot() || event.getAuthor().isSystem()) {
                return;
            }

            String message = event.getMessage().getContentStripped();
            String member = event.getMember().getEffectiveName();
            String messageId = event.getMessageId();

            event.getChannel().deleteMessageById(messageId).queue();

            event.getChannel().sendMessageEmbeds(new EmbedBuilder().setColor(0x0266c9).setDescription("[DiscordChat] " + member + ": " + message).build()).queue();
            Bukkit.broadcastMessage("\u00A79[DiscordChat]\u00A7f " + event.getMember().getEffectiveName() + ": " + event.getMessage().getContentStripped());
        }
    }
}