package me.derechtepilz.economy.modules.discord.communication.discord;

import me.derechtepilz.economy.modules.discord.DiscordBot;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class ChattingFromDiscordServer extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (DiscordBot.getDiscordBot() != null) {
            if (!event.isFromGuild() && !event.getAuthor().isBot()) {
                if (!DiscordBot.getDiscordBot().isActive()) {
                    return;
                }
                String[] message = event.getMessage().getContentStripped().split(":");
                if (isPlayer(message[0])) {
                    Bukkit.getPlayer(message[0]).sendMessage(TranslatableChatComponent.read("chattingFromDiscordServer.minecraft_server.message_received").replace("%s", event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator()) + message[1]);
                } else {
                    event.getAuthor().openPrivateChannel().complete().sendMessage(TranslatableChatComponent.read("chattingFromDiscordServer.wrong_private_message_format")).queue();
                }
                return;
            }
            if (DiscordBot.getDiscordBot().isActive() && event.getChannel().equals(DiscordBot.getDiscordBot().getMinecraftChat())) {
                if (event.getAuthor().isBot() || event.getAuthor().isSystem()) {
                    return;
                }

                String message = event.getMessage().getContentStripped();
                String member = event.getMember().getUser().getName();
                String messageId = event.getMessageId();

                event.getChannel().deleteMessageById(messageId).queue();

                event.getChannel().sendMessageEmbeds(new EmbedBuilder().setColor(0x0266c9).setDescription("[DiscordChat] " + member + ": " + message).build()).queue();
                Bukkit.broadcastMessage("\u00A79[DiscordChat]\u00A7f " + event.getMember().getEffectiveName() + ": " + event.getMessage().getContentStripped());
            }
        }
    }

    private boolean isPlayer(String name) {
        return Bukkit.getPlayer(name) != null;
    }
}