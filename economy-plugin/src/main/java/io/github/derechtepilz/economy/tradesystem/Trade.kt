package io.github.derechtepilz.economy.tradesystem

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class Trade(private val tradeInitiator: Player, private val tradeTarget: Player) {

    private val itemsForPlayer: MutableList<ItemStack> = mutableListOf()
    private val itemsForTarget: MutableList<ItemStack> = mutableListOf()

    fun addItemForTradeInitiator(itemStack: ItemStack) {
        itemsForPlayer.add(itemStack)
    }

    fun addItemForTradeTarget(itemStack: ItemStack) {
        itemsForTarget.add(itemStack)
    }

    fun removeItemFromTradeInitiator(itemStack: ItemStack) {
        itemsForPlayer.remove(itemStack)
    }

    fun removeItemFromTradeTarget(itemStack: ItemStack) {
        itemsForTarget.remove(itemStack)
    }

    fun completeTrade() {
        val hasTradeInitiatorEnoughSpace: Boolean = hasInventoryEnoughSpace(tradeInitiator, itemsForPlayer)
        val hasTradeTargetEnoughSpace: Boolean = hasInventoryEnoughSpace(tradeTarget, itemsForTarget)
        if (!hasTradeInitiatorEnoughSpace) {
            tradeInitiator.sendMessage("§cYou do not have enough free space in your inventory to hold every offered item! Because of that, this trade is now cancelled!")
            tradeTarget.sendMessage("§b${tradeInitiator.name} §cdoes not have enough space in their inventory to hold every offered item! Because of that, this trade is now cancelled!")

            cancelTrade()
            return
        }
        if (!hasTradeTargetEnoughSpace) {
            tradeInitiator.sendMessage("§b${tradeTarget.name} §cdoes not have enough space in their inventory to hold every offered item! Because of that, this trade is now cancelled!")
            tradeTarget.sendMessage("§cYou do not have enough space in your inventory to hold every offered item! Because of that, this trade is now cancelled!")

            cancelTrade()
            return
        }
        tradeInitiator.inventory.addItem(*itemsForPlayer.toTypedArray())
        tradeTarget.inventory.addItem(*itemsForTarget.toTypedArray())

        tradeInitiator.sendMessage("§aTrade completed!")
        tradeTarget.sendMessage("§aTrade completed!")
    }

    private fun hasInventoryEnoughSpace(player: Player, tradeItems: MutableList<ItemStack>): Boolean {
        var emptyInventorySlots = 0
        for (i in 0 until player.inventory.size) {
            if (player.inventory.getItem(i) == null) {
                emptyInventorySlots += 1
                continue
            }
        }
        return emptyInventorySlots >= tradeItems.size
    }

    private fun cancelTrade() {
        tradeInitiator.inventory.addItem(*itemsForTarget.toTypedArray())
        tradeTarget.inventory.addItem(*itemsForPlayer.toTypedArray())

        tradeInitiator.closeInventory()
        tradeTarget.closeInventory()
    }

}