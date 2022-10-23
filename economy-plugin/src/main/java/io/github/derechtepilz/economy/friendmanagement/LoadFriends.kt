package io.github.derechtepilz.economy.friendmanagement

import io.github.derechtepilz.economy.Main
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.io.File
import java.io.FileInputStream
import java.io.ObjectInputStream
import java.util.*

class LoadFriends(private val main: Main) : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player: Player = event.player
        loadFriends(player.uniqueId)
    }

    @Suppress("UNCHECKED_CAST")
    fun loadFriends(uuid: UUID) {
        val friends = File("./plugins/Economy/Friends/$uuid.bin")
        if (!friends.exists()) return
        val fileInputStream = FileInputStream(friends)
        val objectInputStream = ObjectInputStream(fileInputStream)
        val any: Any = objectInputStream.readObject()
        main.friend.addFriendList(uuid, any as MutableList<UUID>)
    }

}