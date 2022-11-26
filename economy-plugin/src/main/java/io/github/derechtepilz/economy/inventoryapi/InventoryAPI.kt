package io.github.derechtepilz.economy.inventoryapi

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.java.JavaPlugin

class InventoryAPI {

	private lateinit var inventoryLayout: CharArray
	private lateinit var plugin: JavaPlugin

	constructor(inventoryLayout: CharArray, plugin: JavaPlugin) {
		this.inventoryLayout = inventoryLayout
		this.plugin = plugin
	}

	constructor(inventory: MutableMap<Int, Inventory>) {
		wasInventoryCreated = true
		Companion.inventory = inventory
	}

	constructor(inventory: Array<ItemStack>, title: String) {
		if (inventory.size > 54) throw InventoryModificationException("This constructor only supports one inventory page!")
		if (inventory.size < 8) throw InventoryLayoutException("The given inventory contents are not applicable to an inventory! The given size is '${inventory.size}', should be at least '9'!")
		if (inventory.size % 9 != 0) throw InventoryLayoutException("The given inventory contents are not applicable to an inventory! The given size is '${inventory.size}', should be a multiple of '9'!")
		wasInventoryCreated = true
		val menu: Inventory = Bukkit.createInventory(null, inventory.size, title)
		menu.contents = inventory
		Companion.inventory = mutableMapOf(Pair(0, menu))
	}

	companion object {
		@JvmStatic
		private var wasInventoryCreated = false

		private var inventory: MutableMap<Int, Inventory>? = null
	}

	fun verifyInventoryLayout(): InventoryConfiguration {
		if (inventoryLayout.size < 8) throw InventoryLayoutException("The given inventory layout is not applicable to an inventory! The given size is '${inventoryLayout.size}', should be at least '9'!")
		if (inventoryLayout.size > 54) throw InventoryLayoutException("The given inventory layout is not applicable to an inventory! The given size is '${inventoryLayout.size}', should be at maximum '54'!")
		if (inventoryLayout.size % 9 != 0) throw InventoryLayoutException("The given inventory layout is not applicable to an inventory! The given size is '${inventoryLayout.size}', should be a multiple of '9'!")
		return InventoryConfiguration(inventoryLayout, plugin)
	}

	fun updateItem(position: Char, itemStack: ItemStack): MutableMap<Int, Inventory> {
		if (!wasInventoryCreated) {
			throw InventoryModificationException("You have create an inventory first before you try to modify it!")
		}
		if (inventory == null) {
			throw InventoryModificationException("The inventory created previously has not been set correctly! Modification impossible!")
		}
		if (!inventoryLayout.contains(position)) throw InventoryLayoutException("This inventory layout does not contain a character '$position'! Please choose another one!")
		val updatedInventory: MutableMap<Int, Inventory> = inventory!!
		for (i in updatedInventory.keys) {
			val inventoryToUpdate: Inventory = updatedInventory[i]!!
			for (char in inventoryLayout.indices) {
				if (inventoryLayout[char] != position) continue
				inventoryToUpdate.setItem(char, itemStack)
			}
			updatedInventory[i] = inventoryToUpdate
		}
		inventory = updatedInventory
		return updatedInventory
	}

	fun getInventory(): MutableMap<Int, Inventory> {
		if (inventory == null) throw InventoryModificationException("This inventory has not been created yet!")
		return inventory!!
	}

	class InventoryConfiguration constructor(private val inventoryLayout: CharArray, private val plugin: JavaPlugin) {

		fun setItemsPerPage(itemCount: Int, inventoryContents: Array<ItemStack?>): InventoryPagination {
			if (itemCount > 45) throw InventoryLayoutException("An inventory cannot have more than 45 items per page, you wanted '$itemCount'!")
			if (itemCount >= inventoryLayout.size) throw InventoryLayoutException("You specified '$itemCount' items per inventory page! However, this is not possible for your inventory layout which takes '${inventoryLayout.size}' slots! Please make sure the items per page are '9' items fewer than your inventory layout is large!")
			val pageItemCount = if (itemCount % 9 != 0) {
				itemCount + (9 - itemCount % 9)
			} else {
				itemCount
			}
			var pages: Int = inventoryContents.size / pageItemCount
			if (inventoryContents.size % pageItemCount != 0) {
				pages += 1
			}

			val inventories: MutableMap<Int, MutableList<ItemStack?>> = mutableMapOf()
			for (i in 0 until pages) {
				val inventoryPage: MutableList<ItemStack?> = mutableListOf()
				for (j in inventoryLayout.indices) {
					inventoryPage.add(null)
				}
				inventories[i] = inventoryPage
			}

			return InventoryPagination(inventoryLayout, pages, itemCount, inventoryContents, inventories, plugin)
		}

		class InventoryPagination constructor(private val inventoryLayout: CharArray, private val pages: Int, private val itemCount: Int, private val inventoryContents: Array<ItemStack?>, private val inventories: MutableMap<Int, MutableList<ItemStack?>>, private val plugin: JavaPlugin) {

			private var closeItem = false
			private var previousPageItem = false
			private var nextPageItem = false

			private lateinit var closeItemStack: ItemStack
			private lateinit var previousPageItemStack: ItemStack
			private lateinit var nextPageItemStack: ItemStack

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
					inventories[i] = inventoryPage
				}
				closeItem = true
				closeItemStack = itemStack
				return this
			}

			fun setPreviousPageItem(position: Char, itemStack: ItemStack): InventoryPagination {
				if (!inventoryLayout.contains(position)) throw InventoryLayoutException("This inventory layout does not contain a character '$position'! Please choose another one!")
				if (checkAppearances(position, inventoryLayout) != 1) throw InventoryLayoutException("This inventory has multiple appearances of the character '$position' you chose to be the close item position. The previous page item may only appear once!")
				for (i in 1 until pages) {
					val inventoryPage: MutableList<ItemStack?> = if (inventories.containsKey(i)) {
						inventories[i]!!
					} else {
						mutableListOf()
					}
					inventoryPage[inventoryLayout.indexOf(position)] = itemStack
					inventories[i] = inventoryPage
				}
				previousPageItem = true
				previousPageItemStack = itemStack
				return this
			}

			fun setNextPageItem(position: Char, itemStack: ItemStack): InventoryPagination {
				if (!inventoryLayout.contains(position)) throw InventoryLayoutException("This inventory layout does not contain a character '$position'! Please choose another one!")
				if (checkAppearances(position, inventoryLayout) != 1) throw InventoryLayoutException("This inventory has multiple appearances of the character '$position' you chose to be the close item position. The next page item may only appear once!")
				for (i in 0 until pages - 1) {
					val inventoryPage: MutableList<ItemStack?> = if (inventories.containsKey(i)) {
						inventories[i]!!
					} else {
						mutableListOf()
					}
					inventoryPage[inventoryLayout.indexOf(position)] = itemStack
					inventories[i] = inventoryPage
				}
				nextPageItem = true
				nextPageItemStack = itemStack
				return this
			}

			fun finishPagination(): InventoryLayoutFinished {
				if (!closeItem) throw InventoryLayoutException("Your inventory does not contain an item to close it!")
				if (pages > 1) {
					if (!previousPageItem) throw InventoryLayoutException("Your inventory does not contain an item to switch to the previous page!")
					if (!nextPageItem) throw InventoryLayoutException("Your inventory does not contain an item to switch to the next page!")
				}

				val pluginManager: PluginManager = Bukkit.getPluginManager()
				pluginManager.registerEvents(object : Listener {
					@EventHandler
					fun onClick(event: InventoryClickEvent) {
						val clickedItem: ItemStack = event.currentItem ?: return
						val player: Player = event.whoClicked as Player
						if (clickedItem == closeItemStack) {
							player.closeInventory()
						}
					}
				}, plugin)
				return InventoryLayoutFinished(inventoryLayout, pages, itemCount, inventoryContents, inventories)
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

			class InventoryLayoutFinished constructor(private val inventoryLayout: CharArray, private val pages: Int, private val itemsPerPage: Int, private val inventoryContents: Array<ItemStack?>, private val inventories: MutableMap<Int, MutableList<ItemStack?>>) {

				private lateinit var fillerItemStack: ItemStack
				private var shouldFillEmptySlots = true

				private var fillerItem = false

				private val inventoryMap: MutableMap<Int, Inventory> = mutableMapOf()

				fun setFillerItem(position: Char, itemStack: ItemStack): InventoryLayoutFinished {
					if (!inventoryLayout.contains(position)) throw InventoryLayoutException("This inventory layout does not contain a character '$position'! Please choose another one!")
					fillerItemStack = itemStack
					for (i in 0 until pages) {
						val inventoryPage: MutableList<ItemStack?> = if (inventories.containsKey(i)) {
							inventories[i]!!
						} else {
							mutableListOf()
						}
						for (j in 0 until inventoryPage.size) {
							if (inventoryLayout[j] != position) {
								continue
							}
							inventoryPage[j] = itemStack
						}
						inventories[i] = inventoryPage
					}
					fillerItem = true
					return this
				}

				fun setAdditionalItem(position: Char, itemStack: ItemStack): InventoryLayoutFinished {
					if (!inventoryLayout.contains(position)) throw InventoryLayoutException("This inventory layout does not contain a character '$position'! Please choose another one!")
					for (i in 0 until pages) {
						val inventoryPage: MutableList<ItemStack?> = if (inventories.containsKey(i)) {
							inventories[i]!!
						} else {
							mutableListOf()
						}
						for (j in 0 until inventoryPage.size) {
							if (inventoryLayout[j] != position) {
								continue
							}
							inventoryPage[j] = itemStack
						}
						inventories[i] = inventoryPage
					}
					return this
				}

				fun setMainContentSlots(position: Char): InventoryLayoutFinished {
					if (!inventoryLayout.contains(position)) throw InventoryLayoutException("This inventory layout does not contain a character '$position'! Please choose another one!")
					if (checkContentSlots(position, inventoryLayout) != itemsPerPage) throw InventoryLayoutException("This inventory layout does not contain the required amount of characters '$position' to fit '$itemsPerPage' items per page!")
					var currentPositionInContents = 0
					for (i in 0 until pages) {
						val inventoryPage: MutableList<ItemStack?> = if (inventories.containsKey(i)) {
							inventories[i]!!
						} else {
							mutableListOf()
						}
						for (j in 0 until itemsPerPage) {
							if (inventoryLayout[j] != position) {
								continue
							}
							inventoryPage[j] = if (currentPositionInContents < inventoryContents.size) {
								inventoryContents[currentPositionInContents]
							} else {
								null
							}
							currentPositionInContents += 1
						}
						inventories[i] = inventoryPage
					}
					return this
				}

				fun finishInventory(title: String, displayCurrentPage: Boolean): MutableMap<Int, Inventory> {
					if (!fillerItem) throw InventoryLayoutException("You did not specify a filler item for empty slots! Please do that before calling this method!")
					if (shouldFillEmptySlots) {
						for (i in 0 until pages) {
							val inventoryPage: MutableList<ItemStack?> = if (inventories.containsKey(i)) {
								inventories[i]!!
							} else {
								mutableListOf()
							}
							for (j in 0 until inventoryPage.size) {
								if (inventoryPage[j] != null) {
									continue
								}
								inventoryPage[j] = fillerItemStack
							}
							inventories[i] = inventoryPage
						}
					}
					for (i in inventories.keys) {
						val inventoryName = if (displayCurrentPage) {
							"$title (${i + 1})"
						} else {
							title
						}
						val inventory = Bukkit.createInventory(null, inventoryLayout.size, inventoryName)
						inventory.contents = inventories[i]!!.toTypedArray()
						inventoryMap[i] = inventory
					}

					wasInventoryCreated = true
					Companion.inventory = inventoryMap

					return inventoryMap
				}

				/**
				 * This defines if empty inventory slots should be filled with the filler item
				 *
				 * @param shouldFillEmptySlots whether empty slots should be filled, default: `true`
				 */
				fun shouldFillEmptyInventorySlots(shouldFillEmptySlots: Boolean): InventoryLayoutFinished {
					this.shouldFillEmptySlots = shouldFillEmptySlots
					return this
				}

				private fun checkContentSlots(character: Char, charArray: CharArray): Int {
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

}