package me.derechtepilz.economy.modules.discord;

import me.derechtepilz.economy.modules.discord.communication.discordserver.ChattingFromDiscordServer;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import me.derechtepilz.economy.utility.config.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.Bukkit;

import javax.security.auth.login.LoginException;
import java.util.Arrays;

public class DiscordBot {

    private static DiscordBot DISCORD_BOT = null;

    private JDA jda;
    private TextChannel minecraftChat;
    private boolean active = false;

    public DiscordBot(String token) throws LoginException, InterruptedException {
        if (!token.equals("") && DISCORD_BOT == null) {
            DISCORD_BOT = this;
            jda = JDABuilder.createDefault(token, Arrays.asList(GatewayIntent.values()))
                    .setActivity(Activity.listening("Minecraft server"))
                    .addEventListeners(new ChattingFromDiscordServer())
                    .build();

            String guildId = Config.get("guildId");
            if (guildId.equals("")) {
                throw new LoginException();
            }
            Guild guild = jda.awaitStatus(JDA.Status.CONNECTED).getGuildById(Config.get("guildId"));
            Bukkit.broadcastMessage(TranslatableChatComponent.read("startUpBot.discord_bot_running"));
            active = true;
            minecraftChat = (guild.getTextChannelsByName("minecraft-chat", true).size() >= 1) ? guild.getTextChannelsByName("minecraft-chat", true).get(0) : (TextChannel) guild.getDefaultChannel();
            minecraftChat.sendMessageEmbeds(new EmbedBuilder()
                            .setColor(0x2bc71f)
                            .setTitle(TranslatableChatComponent.read("discord.message_discord.connected"))
                            .setDescription(TranslatableChatComponent.read("discord.message_discord.connected_description")).build())
                    .queue();

            if (minecraftChat.equals(guild.getDefaultChannel())) {
                minecraftChat.sendMessage(TranslatableChatComponent.read("discord.channel_information")).queue();
            }
        }
    }

    public static DiscordBot getDiscordBot() {
        return DISCORD_BOT;
    }

    public void setDiscordBotNull() {
        DISCORD_BOT = null;
    }

    public JDA getJda() {
        return jda;
    }

    public void sendShutdownMessage() {
        if (jda.getStatus() == JDA.Status.CONNECTED) {
            minecraftChat.sendMessageEmbeds(new EmbedBuilder()
                            .setColor(0xee4444)
                            .setTitle(TranslatableChatComponent.read("discord.message_discord.disconnected"))
                            .setDescription(TranslatableChatComponent.read("discord.message_discord.disconnected_description")).build())
                    .queue();
        }
    }

    public TextChannel getMinecraftChat() {
        return minecraftChat;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}