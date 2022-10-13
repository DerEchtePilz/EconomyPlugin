package me.derechtepilz.economy.friendmanagement

import me.derechtepilz.economy.Main
import java.io.File
import java.io.FileInputStream
import java.io.ObjectInputStream
import java.util.*

class LoadFriends(private val main: Main) {

    @Suppress("UNCHECKED_CAST")
    fun loadFriends() {
        val fileFolder = File("./plugins/Economy/Friends")
        val dataFiles: Array<out File> = fileFolder.listFiles() ?: return
        for (file: File in dataFiles) {
            val uuid: String = file.name.replace(".bin", "")
            val fileInputStream = FileInputStream("$uuid.bin")
            val objectInputStream = ObjectInputStream(fileInputStream)
            val any: Any = objectInputStream.readObject()
            main.friend.addFriendList(UUID.fromString(uuid), any as MutableList<UUID>)
        }
    }

}