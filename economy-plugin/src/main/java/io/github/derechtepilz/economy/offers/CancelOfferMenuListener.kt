package io.github.derechtepilz.economy.offers

import io.github.derechtepilz.economy.Main
import io.github.derechtepilz.economy.inventorymanagement.StandardInventoryItems
import io.github.derechtepilz.economy.utility.DataHandler
import io.github.derechtepilz.economy.utility.NamespacedKeys
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.UUID

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
			if (item.itemMeta?.persistentDataContainer?.has(NamespacedKeys.ITEM_UUID, PersistentDataType.STRING) == false) return
			val itemUuid: UUID = UUID.fromString(item.itemMeta?.persistentDataContainer?.get(NamespacedKeys.ITEM_UUID, PersistentDataType.STRING))

			player.inventory.addItem(main.registeredItems[itemUuid]!!.boughtItem)

			main.registeredItems.remove(itemUuid)
			main.registeredItemUuids.remove(itemUuid)
			main.offeringPlayerUuids.remove(player.uniqueId)
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