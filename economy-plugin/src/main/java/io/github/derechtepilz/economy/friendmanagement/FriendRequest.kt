package io.github.derechtepilz.economy.friendmanagement

import java.util.UUID

class FriendRequest {

    private val friendRequests: MutableMap<UUID, MutableList<UUID>> = mutableMapOf()

    /**
     * @param initiator The one who made the friend request
     * @param uuid The one receiving the friend request
     */
    fun addFriendRequest(initiator: UUID, uuid: UUID) {
        val friendRequestsList: MutableList<UUID> = if (friendRequests.containsKey(initiator)) friendRequests[initiator]!! else mutableListOf()
        friendRequestsList.add(uuid)
        friendRequests[initiator] = friendRequestsList
    }

    /**
     * @param initiator The one who made the friend request
     * @param uuid The one receiving the friend request
     */
    fun removeFriendRequest(initiator: UUID, uuid: UUID) {
        val friendRequestsList: MutableList<UUID> = if (friendRequests.containsKey(initiator)) friendRequests[initiator]!! else mutableListOf()
        friendRequestsList.remove(uuid)
        friendRequests[initiator] = friendRequestsList
    }

    /**
     * @param initiator The one who made the friend request
     * @param uuid The one receiving the friend request
     */
    fun isFriendRequest(initiator: UUID, uuid: UUID): Boolean {
        val friendRequestsList: MutableList<UUID> = if (friendRequests.containsKey(initiator)) friendRequests[initiator]!! else mutableListOf()
        return friendRequestsList.contains(uuid)
    }

}