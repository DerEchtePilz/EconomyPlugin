package io.github.derechtepilz.economy.tradesystem

import io.github.derechtepilz.economy.Main
import io.github.derechtepilz.economy.inventoryapi.InventoryAPI
import io.github.derechtepilz.economy.inventorymanagement.StandardInventoryItems
import io.github.derechtepilz.economy.utility.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID

class Trade(private val main: Main, private val tradeInitiator: Player, private val tradeTarget: Player) {

    private val itemsForPlayer: MutableList<ItemStack> = mutableListOf()
    private val itemsForTarget: MutableList<ItemStack> = mutableListOf()

    private val initiatorInventory: Inventory
    private val targetInventory: Inventory

    private val initiatorInventoryContents: Array<ItemStack?>
    private val targetInventoryContents: Array<ItemStack?>

    private val itemsForTargetDisplaySlots: IntArray = intArrayOf(
        0, 1, 2, 3,
        9, 10, 11, 12,
        18, 19, 20, 21,
        27, 28, 29, 30
    )

    private val itemsForInitiatorDisplaySlots: IntArray = intArrayOf(
        5, 6, 7, 8,
        14, 15, 16, 17,
        23, 24, 25, 26,
        32, 33, 34, 35
    )

    private val tradeInventoryAPIInitiator: InventoryAPI = InventoryAPI(charArrayOf(
        '#', '#', '#', '#', 'F', '#', '#', '#', '#',
        '#', '#', '#', '#', 'F', '#', '#', '#', '#',
        '#', '#', '#', '#', 'F', '#', '#', '#', '#',
        '#', '#', '#', '#', 'F', '#', '#', '#', '#',
        'A', 'P', 'P', 'P', 'C', 'T', 'T', 'T', 'T'
    ), main)

    private val tradeInventoryInitiator: MutableMap<Int, Inventory> = tradeInventoryAPIInitiator.verifyInventoryLayout()
        .setItemsPerPage(32, arrayOfNulls(32))
        .setCloseItem('C', StandardInventoryItems.MENU_CLOSE).finishPagination()
        .setFillerItem('F', StandardInventoryItems.MENU_GLASS).setMainContentSlots('#')
        .setAdditionalItem('P', ItemBuilder(Material.RED_STAINED_GLASS_PANE).setName("§cYou currently do not accept this trade!").build())
        .setAdditionalItem('A', ItemBuilder(Material.LIGHT_GRAY_DYE).setName("§aClick here to accept this trade!").build())
        .setAdditionalItem('T', ItemBuilder(Material.RED_STAINED_GLASS_PANE).setName("§cYour trade partner currently does not accept this trade!").build())
        .finishInventory("Trade Menu", false)

    private val tradeInventoryAPITarget: InventoryAPI = InventoryAPI(tradeInventoryInitiator)
    private val tradeInventoryTarget: MutableMap<Int, Inventory> = tradeInventoryAPITarget.getInventory()

    init {
        this.initiatorInventory = tradeInventoryInitiator[0]!!
        this.targetInventory = tradeInventoryTarget[0]!!

        this.initiatorInventoryContents = initiatorInventory.contents
        this.targetInventoryContents = targetInventory.contents

        tradeInitiator.openInventory(initiatorInventory)
        tradeTarget.openInventory(targetInventory)
    }

    fun addItemForTradeInitiator(itemStack: ItemStack) {
        // This method should do this:
        // 1. Inventory of trade initiator: put the items on the right-hand side
        // 2. Inventory of trade target: put the items on the left-hand side
        val nextAvailableSlotForInitiator = getNextAvailable(initiatorInventoryContents, itemsForInitiatorDisplaySlots)
        initiatorInventoryContents[nextAvailableSlotForInitiator] = itemStack
        tradeInitiator.openInventory.topInventory.contents = initiatorInventoryContents

        val nextAvailableSlotForTarget = getNextAvailable(targetInventoryContents, itemsForTargetDisplaySlots)
        targetInventoryContents[nextAvailableSlotForTarget] = itemStack
        tradeTarget.openInventory.topInventory.contents = targetInventoryContents

        itemsForPlayer.add(itemStack)
    }

    fun addItemForTradeTarget(itemStack: ItemStack) {
        // This method should do this:
        // 1. Inventory of trade initiator: put the items on the left-hand side
        // 2. Inventory of trade target: put the items on the right-hand side
        val nextAvailableSlotForInitiator = getNextAvailable(initiatorInventoryContents, itemsForTargetDisplaySlots)
        initiatorInventoryContents[nextAvailableSlotForInitiator] = itemStack
        tradeInitiator.openInventory.topInventory.contents = initiatorInventoryContents

        val nextAvailableSlotForTarget = getNextAvailable(targetInventoryContents, itemsForInitiatorDisplaySlots)
        targetInventoryContents[nextAvailableSlotForTarget] = itemStack
        tradeTarget.openInventory.topInventory.contents = targetInventoryContents

        itemsForTarget.add(itemStack)
    }

    fun removeItemFromTradeInitiator(itemStack: ItemStack) {
        // This method should do this:
        // 1. Inventory of trade initiator: remove the items from the right-hand side
        // 2. Inventory of trade target: remove the items from the left-hand side
        itemsForPlayer.remove(itemStack)
    }

    fun removeItemFromTradeTarget(itemStack: ItemStack) {
        // This method should do this:
        // 1. Inventory of trade initiator: remove the items from the left-hand side
        // 2. Inventory of trade target: remove the items from the hand-hand side
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

        deleteTrade()
    }

    fun getTradeInitiator(): UUID {
        return tradeInitiator.uniqueId
    }

    fun getTradeTarget(): UUID {
        return tradeTarget.uniqueId
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

    private fun getNextAvailable(inventoryContents: Array<ItemStack?>, tradeSlots: IntArray): Int {
        for (i in tradeSlots) {
            if (inventoryContents[i] != null) continue
            return i
        }
        return -1
    }

    private fun cancelTrade() {
        tradeInitiator.inventory.addItem(*itemsForTarget.toTypedArray())
        tradeTarget.inventory.addItem(*itemsForPlayer.toTypedArray())

        tradeInitiator.closeInventory()
        tradeTarget.closeInventory()

        deleteTrade()
    }

    private fun deleteTrade() {
        main.trades.remove(tradeInitiator.uniqueId)
        main.trades.remove(tradeTarget.uniqueId)
    }

}