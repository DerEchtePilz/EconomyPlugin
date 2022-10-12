package me.derechtepilz.economy.friendmanagement

import java.util.*

class Friend {

    private val friends: MutableMap<UUID, MutableList<UUID>> = mutableMapOf()

    fun addFriend(uuid: UUID, friend: UUID, isOneAdded: Boolean) {
        val currentFriends: MutableList<UUID> = if (friends.containsKey(uuid)) {
            friends[uuid]!!
        } else {
            mutableListOf()
        }
        currentFriends.add(friend)
        friends[uuid] = currentFriends
        if (!isOneAdded) {
            addFriend(friend, uuid, true)
        }
    }

    fun removeFriend(uuid: UUID, friend: UUID, isOneRemoved: Boolean) {
        val currentFriends: MutableList<UUID> = if (friends.containsKey(uuid)) {
            friends[uuid]!!
        } else {
            mutableListOf()
        }
        currentFriends.remove(friend)
        friends[uuid] = currentFriends
        if (!isOneRemoved) {
            removeFriend(friend, uuid, true)
        }
    }

    fun isFriend(uuid: UUID, friend: UUID): Boolean {
        val currentFriends: MutableList<UUID> = if (friends.containsKey(uuid)) {
            friends[uuid]!!
        } else {
            mutableListOf()
        }
        return currentFriends.contains(friend)
    }

    fun addFriendList(uuid: UUID, friendList: MutableList<UUID>) {
        val currentFriends: MutableList<UUID> = if (friends.containsKey(uuid)) {
            friends[uuid]!!
        } else {
            mutableListOf()
        }
        for (uuid: UUID in friendList) {
            if (currentFriends.contains(uuid)) continue
            currentFriends.add(uuid)
        }
        friends[uuid] = currentFriends
    }

    fun getFriends(): MutableMap<UUID, MutableList<UUID>> {
        return friends
    }

}