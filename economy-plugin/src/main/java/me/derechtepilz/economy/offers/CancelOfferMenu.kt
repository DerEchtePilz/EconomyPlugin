package me.derechtepilz.economy.offers

import me.derechtepilz.economy.Main
import me.derechtepilz.economy.inventorymanagement.InventoryUtility
import me.derechtepilz.economy.itemmanagement.Item
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.ArrayList

class CancelOfferMenu(private val main: Main): Listener {

    private val pageSize: Int = 36
    private val playerOffers: HashMap<UUID, MutableList<Array<ItemStack>>> = HashMap()

    fun openCancelOfferMenu(player: Player): Boolean {
        val inventory: Inventory = Bukkit.createInventory(null, pageSize + 9, "Cancel your offers")
        if (!playerOffers.containsKey(player.uniqueId)) {
            preparePlayerItems(player)
        }
        return if (!playerOffers.containsKey(player.uniqueId)) {
            false
        } else {
            inventory.contents = playerOffers[player.uniqueId]!![0]
            player.openInventory(inventory)
            true
        }
    }

    private fun preparePlayerItems(player: Player) {
        val offers: MutableList<ItemStack> = mutableListOf()
        for (uuid in main.registeredItems.keys) {
            val item: Item = main.registeredItems[uuid] ?: continue
            if (!(item.seller.equals(player.uniqueId))) {
                continue
            }
            offers.add(item.itemStack)
        }
        if (offers.size == 0) {
            return
        }
        playerOffers[player.uniqueId] = formatPages(offers)
    }

    private fun formatPages(list: MutableList<ItemStack>): MutableList<Array<ItemStack>> {
        val maxPages: Int = InventoryUtility.calculateMaxPages(list.size, pageSize)
        val cancelMenuPages: MutableList<Array<ItemStack>> = mutableListOf()
        while (list.size >= pageSize) {
            var cancelMenuPage: MutableList<ItemStack> = ArrayList(45)
            var removedItemsFromOffers = 0
            val i = 0
            while (removedItemsFromOffers < pageSize) {
                cancelMenuPage.add(list[i])
                list.removeAt(i)
                removedItemsFromOffers++
            }
            cancelMenuPages.add(InventoryUtility.addBottomMenuRow(cancelMenuPage.toTypedArray(), cancelMenuPages.size, maxPages, pageSize + 9).toTypedArray())
            cancelMenuPages.add(cancelMenuPage.toTypedArray())
            cancelMenuPage = ArrayList(45)
        }
        if (list.size == 0) {
            return cancelMenuPages
        } else {
            val buyMenuPage: MutableList<ItemStack> = mutableListOf()
            for (i in list.indices) {
                buyMenuPage.add(list[i])
            }
            cancelMenuPages.add(InventoryUtility.addBottomMenuRow(buyMenuPage.toTypedArray(), cancelMenuPages.size, maxPages, pageSize + 9).toTypedArray())
        }
        return cancelMenuPages
    }

}
