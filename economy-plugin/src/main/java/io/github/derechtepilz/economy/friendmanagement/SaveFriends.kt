package io.github.derechtepilz.economy.friendmanagement

import io.github.derechtepilz.economy.Main
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream
import java.util.*

class SaveFriends(private val main: Main) {

    fun saveFriends() {
        val dir = File("./plugins/Economy/Friends")
        if (!dir.exists()) {
            dir.mkdir()
        }
        for (uuid: UUID in main.friend.getFriends().keys) {
            val fileOutputStream = FileOutputStream("$uuid.bin")
            val objectOutputStream = ObjectOutputStream(fileOutputStream)
            objectOutputStream.writeObject(main.friend.getFriends()[uuid])
            objectOutputStream.close()
        }
    }


}