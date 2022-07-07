package me.derechtepilz.economy.modules.discord;

import me.derechtepilz.economy.utility.ChatFormatter;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import net.dv8tion.jda.api.EmbedBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ServerStatus implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (DiscordBot.getDiscordBot().isActive()) {
            Player player = event.getPlayer();
            if (DiscordBot.getDiscordBot() != null) {
                DiscordBot.getDiscordBot().getMinecraftChat().sendMessageEmbeds(
                        new EmbedBuilder()
                                .setColor(0xffea00)
                                .setTitle(TranslatableChatComponent.read("discord.minecraft_server.player_joined").replace("%s", player.getName()))
                                .setDescription(TranslatableChatComponent.read("discord.minecraft_player_count").replace("%s", ChatFormatter.valueOf(Bukkit.getOnlinePlayers().size())))
                                .build()
                ).queue();
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (DiscordBot.getDiscordBot().isActive()) {
            Player player = event.getPlayer();
            if (DiscordBot.getDiscordBot() != null) {
                DiscordBot.getDiscordBot().getMinecraftChat().sendMessageEmbeds(
                        new EmbedBuilder()
                                .setColor(0xffea00)
                                .setTitle(TranslatableChatComponent.read("discord.minecraft_server.player_quit").replace("%s", player.getName()))
                                .setDescription(TranslatableChatComponent.read("discord.minecraft_player_count").replace("%s", ChatFormatter.valueOf(Bukkit.getOnlinePlayers().size() - 1)))
                                .build()
                ).queue();
            }
        }
    }
}
