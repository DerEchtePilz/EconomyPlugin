package io.github.derechtepilz.economy.tradesystem

import java.util.*

class TradeRequest {

    private val tradeRequests: MutableMap<UUID, MutableList<UUID>> = mutableMapOf()

    /**
     * @param initiator The one who made the trade request
     * @param uuid The one receiving the trade request
     */
    fun addTradeRequest(initiator: UUID, uuid: UUID) {
        val tradeRequestsList: MutableList<UUID> = if (tradeRequests.containsKey(initiator)) tradeRequests[initiator]!! else mutableListOf()
        tradeRequestsList.add(uuid)
        tradeRequests[initiator] = tradeRequestsList
    }

    /**
     * @param initiator The one who made the trade request
     * @param uuid The one receiving the trade request
     */
    fun removeTradeRequest(initiator: UUID, uuid: UUID) {
        val tradeRequestsList: MutableList<UUID> = if (tradeRequests.containsKey(initiator)) tradeRequests[initiator]!! else mutableListOf()
        tradeRequestsList.remove(uuid)
        tradeRequests[initiator] = tradeRequestsList
    }

    /**
     * @param initiator The one who made the trade request
     * @param uuid The one receiving the trade request
     */
    fun isTradeRequest(initiator: UUID, uuid: UUID): Boolean {
        val tradeRequestsList: MutableList<UUID> = if (tradeRequests.containsKey(initiator)) tradeRequests[initiator]!! else mutableListOf()
        return tradeRequestsList.contains(uuid)
    }

}