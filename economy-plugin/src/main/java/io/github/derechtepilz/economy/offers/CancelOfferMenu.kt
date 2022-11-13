package io.github.derechtepilz.economy.offers

import io.github.derechtepilz.economy.Main
import io.github.derechtepilz.economy.inventorymanagement.InventoryUtility
import io.github.derechtepilz.economy.itemmanagement.Item
import io.github.derechtepilz.economy.utility.DataHandler
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.*

class CancelOfferMenu(private val main: Main) {

    private val pageSize: Int = 36
    private val playerOffers: HashMap<UUID, MutableList<Array<ItemStack>>> = HashMap()
    private var cancelMenu: Inventory? = null

    fun openInventory(player: Player, page: Int) {
        preparePlayerItems(player)
        if (playerOffers[player.uniqueId]!!.size == 0) {
            if (player.openInventory.title == "Cancel your offers") {
                player.closeInventory()
            }
            return
        }
        if (cancelMenu == null) {
            cancelMenu = Bukkit.createInventory(null, pageSize + 9, "Cancel your offers")
        }
        cancelMenu!!.contents = playerOffers[player.uniqueId]!![page]

        if (DataHandler.canInventoryOpen(player)) {
            if (player.openInventory.title == "Cancel your offers") {
                player.openInventory.topInventory.contents = playerOffers[player.uniqueId]!![page]
                return
            }
            player.openInventory(cancelMenu!!)
        }
    }

    private fun preparePlayerItems(player: Player) {
        val offers: MutableList<ItemStack> = mutableListOf()
        for (uuid in main.registeredItems.keys) {
            val item: Item = main.registeredItems[uuid] ?: continue
            if (!item.seller.equals(player.uniqueId)) {
                continue
            }
            offers.add(item.itemStack)
        }

        if (offers.size == 0) {
            if (playerOffers[player.uniqueId]!!.size >= 1) {
                playerOffers[player.uniqueId]!!.clear()
            }
            return
        }

        val inventoryPages: MutableList<Array<ItemStack>> = getCancelMenuPages(offers)
        playerOffers[player.uniqueId] = inventoryPages
    }

    private fun getCancelMenuPages(list: MutableList<ItemStack>): MutableList<Array<ItemStack>> {
        if (list.size == 0) {
            return mutableListOf()
        }

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
            cancelMenuPages.add(
                InventoryUtility.addBottomMenuRow(
                    cancelMenuPage.toTypedArray(),
                    cancelMenuPages.size,
                    maxPages,
                    pageSize + 9
                ).toTypedArray()
            )
            cancelMenuPages.add(cancelMenuPage.toTypedArray())
            cancelMenuPage = ArrayList(45)
        }
        if (list.size == 0) {
            return cancelMenuPages
        } else {
            val buyMenuPage: ArrayList<ItemStack> = ArrayList(45)
            for (i in 0..44) {
                buyMenuPage.add(ItemStack(Material.AIR))
            }
            for (i in list.indices) {
                buyMenuPage[i] = list[i]
            }
            cancelMenuPages.add(
                InventoryUtility.addBottomMenuRow(
                    buyMenuPage.toTypedArray(),
                    cancelMenuPages.size,
                    maxPages,
                    pageSize + 9
                ).toTypedArray()
            )
        }
        return cancelMenuPages
    }

}
