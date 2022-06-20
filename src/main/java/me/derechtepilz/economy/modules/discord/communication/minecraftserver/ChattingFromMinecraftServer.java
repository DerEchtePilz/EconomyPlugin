package me.derechtepilz.economy.modules.discord.communication.minecraftserver;

import me.derechtepilz.economy.modules.discord.DiscordBot;
import net.dv8tion.jda.api.EmbedBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChattingFromMinecraftServer implements Listener {
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (DiscordBot.getDiscordBot() != null) {
            if (DiscordBot.getDiscordBot().isActive()) {
                Player player = event.getPlayer();
                event.setFormat("\u00A72[MinecraftChat]\u00A7f " + "%1$s" + ": %2$s");
                DiscordBot.getDiscordBot().getMinecraftChat().sendMessageEmbeds(new EmbedBuilder().setColor(0x0960f).setDescription("[MinecraftChat] " + player.getName() + ": " + event.getMessage()).build()).queue();
            }
        }
    }
}