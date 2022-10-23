package io.github.derechtepilz.economy.coinmanagement

import io.github.derechtepilz.economy.Main
import io.github.derechtepilz.economy.utility.ChatFormatter
import io.github.derechtepilz.economycore.EconomyAPI
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class CoinDisplay(val main: Main) {

    private val chatFormatter: ChatFormatter = ChatFormatter()

    fun displayCoins(): Int {
        val taskId: Int = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, {
            for (player: Player in Bukkit.getOnlinePlayers()) {
                val textComponent = TextComponent(
                    "§aYour balance: §6" + chatFormatter.valueOf(EconomyAPI.getBalance(player))
                )
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, textComponent)
            }
        }, 20, 20)
        return taskId;
    }

}