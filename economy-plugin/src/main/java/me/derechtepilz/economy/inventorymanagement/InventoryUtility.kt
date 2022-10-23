package me.derechtepilz.economy.inventorymanagement

import org.bukkit.inventory.ItemStack

class InventoryUtility {

    companion object {
        @JvmStatic
        fun addBottomMenuRow(buyMenuPage: Array<ItemStack>, currentPage: Int, maxPages: Int, pageSize: Int): List<ItemStack> {
            // Only one page exists
            if (currentPage == 0 && maxPages == 1) {
                for (i in 36 until pageSize) {
                    buyMenuPage[i] = StandardInventoryItems.MENU_GLASS
                    if (i == 40) {
                        buyMenuPage[i] = StandardInventoryItems.MENU_CLOSE
                    }
                }
                return listOf(*buyMenuPage)
            }
            // More than one page exist, but we are on the first page
            if (currentPage == 0 && maxPages > 1) {
                for (i in 36 until pageSize) {
                    buyMenuPage[i] = StandardInventoryItems.MENU_GLASS
                    if (i == 40) {
                        buyMenuPage[i] = StandardInventoryItems.MENU_CLOSE
                    }
                    if (i == pageSize - 1) {
                        buyMenuPage[i] = StandardInventoryItems.ARROW_NEXT
                    }
                }
                return listOf(*buyMenuPage)
            }
            // More than one page exist, we are not on the first and not on the last page
            if (currentPage > 0 && currentPage < maxPages - 1) {
                for (i in 36 until pageSize) {
                    buyMenuPage[i] = StandardInventoryItems.MENU_GLASS
                    if (i == 36) {
                        buyMenuPage[i] = StandardInventoryItems.ARROW_PREVIOUS
                    }
                    if (i == 40) {
                        buyMenuPage[i] = StandardInventoryItems.MENU_CLOSE
                    }
                    if (i == pageSize - 1) {
                        buyMenuPage[i] = StandardInventoryItems.ARROW_NEXT
                    }
                }
                return listOf(*buyMenuPage)
            }
            // More than one page exist, and we are on the last page
            if (currentPage > 0 && currentPage == maxPages - 1) {
                for (i in 36 until pageSize) {
                    buyMenuPage[i] = StandardInventoryItems.MENU_GLASS
                    if (i == 36) {
                        buyMenuPage[i] = StandardInventoryItems.ARROW_PREVIOUS
                    }
                    if (i == 40) {
                        buyMenuPage[i] = StandardInventoryItems.MENU_CLOSE
                    }
                }
            }
            return listOf(*buyMenuPage)
        }

        @JvmStatic
        fun calculateMaxPages(offers: Int, itemsPerPage: Int): Int {
            var offers = offers
            var pages = 0
            while (offers >= itemsPerPage) {
                pages += 1
                offers -= itemsPerPage
            }
            if (offers > 0) {
                pages += 1
            }
            return pages
        }
    }

}