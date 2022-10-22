package me.derechtepilz.economy.tests.inventory

import me.derechtepilz.economy.Main
import me.derechtepilz.economy.inventoryapi.InventoryAPI
import me.derechtepilz.economy.inventorymanagement.StandardInventoryItems
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class InventoryTest(private val main: Main) : Listener {

	private var currentPage = 0

	private val inventoryLayout: CharArray = charArrayOf(
		'I', 'I', 'I', 'I', 'I', 'I', 'I', 'I', 'I',
		'I', 'I', 'I', 'I', 'I', 'I', 'I', 'I', 'I',
		'I', 'I', 'I', 'I', 'I', 'I', 'I', 'I', 'I',
		'I', 'I', 'I', 'I', 'I', 'I', 'I', 'I', 'I',
		'B', 'F', 'F', 'F', 'C', 'F', 'F', 'F', 'N'
	)

	private var inventory: MutableMap<Int, Inventory> = InventoryAPI(inventoryLayout, main)
		.verifyInventoryLayout()
		.setItemsPerPage(36, ItemListGenerator.generateItemList(36))
		.setCloseItem('C', StandardInventoryItems.MENU_CLOSE).setPreviousPageItem('B', StandardInventoryItems.ARROW_PREVIOUS).setNextPageItem('N', StandardInventoryItems.ARROW_NEXT).finishPagination()
		.setFillerItem('F', StandardInventoryItems.MENU_GLASS).setMainContentSlots('I').finishInventory("Test Inventory", true)

	fun openInventory(player: Player) {
		player.openInventory(inventory[currentPage]!!)
	}

	@EventHandler
	fun onClick(event: InventoryClickEvent) {
		val player: Player = event.whoClicked as Player
		event.isCancelled = true
		val clickedItem: ItemStack = event.currentItem ?: return
		if (clickedItem == StandardInventoryItems.ARROW_PREVIOUS) {
			currentPage -= 1
			player.openInventory(inventory[currentPage]!!)
			return
		}
		if (clickedItem == StandardInventoryItems.ARROW_NEXT) {
			currentPage += 1
			player.openInventory(inventory[currentPage]!!)
			return
		}
	}

	fun regenerateInventory(items: Int) {
		currentPage = 0
		inventory = InventoryAPI(inventoryLayout, main)
			.verifyInventoryLayout()
			.setItemsPerPage(36, ItemListGenerator.generateItemList(items))
			.setCloseItem('C', StandardInventoryItems.MENU_CLOSE).setPreviousPageItem('B', StandardInventoryItems.ARROW_PREVIOUS).setNextPageItem('N', StandardInventoryItems.ARROW_NEXT).finishPagination()
			.setFillerItem('F', StandardInventoryItems.MENU_GLASS).setMainContentSlots('I').finishInventory("Test Inventory", true)
	}

}