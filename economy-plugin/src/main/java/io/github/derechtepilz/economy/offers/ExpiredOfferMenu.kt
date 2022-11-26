package io.github.derechtepilz.economy.offers

import io.github.derechtepilz.economy.Main
import io.github.derechtepilz.economy.inventorymanagement.StandardInventoryItems
import io.github.derechtepilz.economy.utility.ItemBuilder
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class ExpiredOfferMenu(private val main: Main) : Listener {

	private var claimItem: ItemStack? = null

	@EventHandler
	fun onClick(event: InventoryClickEvent) {
		val player: Player = event.whoClicked as Player
		if (event.view.title == "Claim your items") {
			event.isCancelled = true
			val clickedItem: ItemStack = event.currentItem ?: return
			if (clickedItem == claimItem) {
				for (item in main.expiredItems[player.uniqueId]!!) {
					player.inventory.addItem(item)
				}
				main.expiredItems.remove(player.uniqueId)
				player.closeInventory()
				player.sendMessage("§aSuccessfully claimed expired auctions!")
			}
		}
	}

	fun openInventory(player: Player) {
		val inventory: Inventory = Bukkit.createInventory(null, 9, "Claim your items")
		claimItem = ItemBuilder(Material.NETHER_STAR).setName("§aClaim expired auctions: §6" + main.expiredItems[player.uniqueId]!!.size).build()
		for (i in 0 until inventory.size) {
			inventory.setItem(i, StandardInventoryItems.MENU_GLASS)
			inventory.setItem(4, claimItem)
		}
		player.openInventory(inventory)
	}

	fun getFreeSlots(player: Player): Int {
		var freeSlots = 0
		for (item: ItemStack? in player.inventory) {
			if (item == null) {
				freeSlots += 1
			}
		}
		return freeSlots
	}

}