package me.derechtepilz.economy.coinmanagement

import me.derechtepilz.economy.Main
import me.derechtepilz.economy.utility.ChatFormatter
import me.derechtepilz.economycore.EconomyAPI
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

class CoinDisplay(val main: Main) {

    private val chatFormatter: ChatFormatter = ChatFormatter()

    fun displayCoins(): Int {
        val taskId: Int = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, {
            for (player: Player in Bukkit.getOnlinePlayers()) {
                val textComponent = TextComponent(
                    "§aYour balance: §6" + chatFormatter.valueOf(player.persistentDataContainer.get(
                        EconomyAPI.getPlayerBalance(),
                        PersistentDataType.DOUBLE
                    ))
                )
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, textComponent)
            }
        }, 20, 20)
        return taskId;
    }

}