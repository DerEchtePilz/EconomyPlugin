package me.derechtepilz.economy.offers

import me.derechtepilz.economy.Main
import me.derechtepilz.economy.inventorymanagement.StandardInventoryItems
import me.derechtepilz.economy.utility.DataHandler
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack

class CancelOfferMenuListener(private val main: Main) : Listener {

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
                DataHandler.updateMenuPage(player, DataHandler.getCurrentPage(player) - 1)
                return
            }
            if (item == StandardInventoryItems.ARROW_NEXT) {
                DataHandler.updateMenuPage(player, DataHandler.getCurrentPage(player) + 1)
                return
            }
            // TODO: Handle cancelling items
        }
    }

    @EventHandler
    fun onClose(event: InventoryCloseEvent) {
        val player = event.player as Player
        if (event.view.title == "Cancel your offers") {
            DataHandler.removeMenuData(player)
        }
    }

}