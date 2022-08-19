package me.derechtepilz.economy.offers

import me.derechtepilz.economy.Main
import me.derechtepilz.economy.inventorymanagement.InventoryUtility
import me.derechtepilz.economy.inventorymanagement.StandardInventoryItems
import me.derechtepilz.economy.itemmanagement.Item
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CancelOfferMenu(private val main: Main) : Listener {

    private val pageSize: Int = 36
    private val playerOffers: HashMap<UUID, MutableList<Array<ItemStack>>> = HashMap()
    private val playerPage: HashMap<UUID, Int> = HashMap()

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        if (event.view.title == "Cancel your offers") {
            event.isCancelled = true
            val player: Player = event.whoClicked as Player

            if (event.currentItem == null) return
            val item: ItemStack = event.currentItem!!
            if (item == StandardInventoryItems.MENU_CLOSE) {
                player.closeInventory()
                return
            }
            if (item == StandardInventoryItems.ARROW_PREVIOUS) {
                var currentPage: Int = playerPage[player.uniqueId]!!
                currentPage -= 1
                playerPage[player.uniqueId] = currentPage
                player.openInventory.topInventory.contents = playerOffers[player.uniqueId]!![currentPage]
                return
            }
            if (item == StandardInventoryItems.ARROW_NEXT) {
                var currentPage: Int = if (!playerPage.containsKey(player.uniqueId)) 0 else playerPage[player.uniqueId]!!
                currentPage += 1
                playerPage[player.uniqueId] = currentPage
                player.openInventory.topInventory.contents = playerOffers[player.uniqueId]!![currentPage]
                return
            }
            // TODO: Handle cancelling items
        }
    }

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
