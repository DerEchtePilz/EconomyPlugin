package me.derechtepilz.economy.inventoryapi

import org.bukkit.inventory.ItemStack

class InventoryAPI(private val inventoryLayout: CharArray) {

	fun verifyInventoryLayout(): InventoryConfiguration {
		if (inventoryLayout.size < 8) throw InventoryLayoutException("The given inventory layout is not applicable to an inventory! The given size is '${inventoryLayout.size}', should be at least '9'!")
		if (inventoryLayout.size > 54) throw InventoryLayoutException("The given inventory layout is not applicable to an inventory! The given size is '${inventoryLayout.size}', should be at maximum '54'!")
		if (inventoryLayout.size % 9 != 0) throw InventoryLayoutException("The given inventory layout is not applicable to an inventory! The given size is '${inventoryLayout.size}', should be a multiple of '9'!")
		return InventoryConfiguration(inventoryLayout)
	}

	class InventoryConfiguration(private val inventoryLayout: CharArray) {

		fun setItemsPerPage(itemCount: Int, inventoryContents: Array<ItemStack>): InventoryPagination {
			if (itemCount > 45) throw InventoryLayoutException("An inventory cannot have more than 45 items per page, you wanted '$itemCount'!")
			if (itemCount >= inventoryLayout.size) throw InventoryLayoutException("You specified '$itemCount' items per inventory page! However, this is not possible for your inventory layout which takes '${inventoryLayout.size}' slots! Please make sure the items per page are '9' items fewer than your inventory layout is large!")
			val pageItemCount = if (itemCount % 9 != 0) {
				itemCount + (9 - itemCount % 9)
			} else {
				itemCount
			}
			var pages = inventoryContents.size % pageItemCount
			if (inventoryContents.size > pageItemCount * pages) {
				pages += 1
			}

			val inventories: MutableMap<Int, MutableList<ItemStack?>> = mutableMapOf()
			for (i in 0 until pages) {
				val inventoryPage: MutableList<ItemStack?> = mutableListOf()
				for (j in 0 until pageItemCount) {
					inventoryPage.add(null)
				}
				inventories[i] = inventoryPage
			}

			return InventoryPagination(inventoryLayout, pages, inventoryContents, inventories)
		}

		class InventoryPagination(private val inventoryLayout: CharArray, private val pages: Int, private val inventoryContents: Array<ItemStack>, private val inventories: MutableMap<Int, MutableList<ItemStack?>>) {

			var closeItem = false
			var previousPageItem = false
			var nextPageItem = false

			fun setCloseItem(position: Char, itemStack: ItemStack): InventoryPagination {
				if (!inventoryLayout.contains(position)) throw InventoryLayoutException("This inventory layout does not contain a character '$position'! Please choose another one!")
				if (checkAppearances(position, inventoryLayout) != 1) throw InventoryLayoutException("This inventory has multiple appearances of the character '$position' you chose to be the close item position. The close item may only appear once!")
				for (i in 0 until pages) {
					val inventoryPage: MutableList<ItemStack?> = if (inventories.containsKey(i)) {
						inventories[i]!!
					} else {
						mutableListOf()
					}
					inventoryPage[inventoryLayout.indexOf(position)] = itemStack
				}
				closeItem = true
				return this
			}

			fun setPreviousPageItem(position: Char, itemStack: ItemStack): InventoryPagination {
				if (!inventoryLayout.contains(position)) throw InventoryLayoutException("This inventory layout does not contain a character '$position'! Please choose another one!")
				if (checkAppearances(position, inventoryLayout) != 1) throw InventoryLayoutException("This inventory has multiple appearances of the character '$position' you chose to be the close item position. The previous page item may only appear once!")
				for (i in 0 until pages) {
					val inventoryPage: MutableList<ItemStack?> = if (inventories.containsKey(i)) {
						inventories[i]!!
					} else {
						mutableListOf()
					}
					inventoryPage[inventoryLayout.indexOf(position)] = itemStack
				}
				previousPageItem = true
				return this
			}

			private fun checkAppearances(character: Char, charArray: CharArray): Int {
				var appearances = 0
				for (char: Char in charArray) {
					if (char == character) {
						appearances += 1
					}
				}
				return appearances
			}

		}

	}

}