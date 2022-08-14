package me.derechtepilz.economy.offers;

import me.derechtepilz.economy.inventorymanagement.StandardInventoryItems;
import me.derechtepilz.economy.utility.DataHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BuyOfferMenu {

    private final int itemsPerPage = 4 * 9;
    private final int pageSize = 5 * 9;
    private final List<ItemStack[]> inventoryPages = new ArrayList<>();
    private Inventory buyMenu;

    public void updateBuyMenu(List<ItemStack> offers) {
        List<ItemStack[]> buyMenuPages = getBuyMenuPages(offers);
        if (buyMenuPages.size() == 0) {
            if (inventoryPages.size() >= 1) {
                inventoryPages.clear();
            }
            return;
        }
        if (inventoryPages.size() > 0) {
            inventoryPages.clear();
        }
        inventoryPages.addAll(buyMenuPages);
    }

    public void openInventory(Player player, int page) {
        if (inventoryPages.size() == 0) {
            if (player.getOpenInventory().getTitle().contains("Buy Menu (")) {
                player.closeInventory();
            }
            return;
        }
        if (buyMenu == null) {
            buyMenu = Bukkit.createInventory(null, 5 * 9, "Buy Menu (" + (page + 1) + ")");
        }
        buyMenu.setContents(inventoryPages.get(page));

        if (DataHandler.canInventoryOpen(player)) {
            if (player.getOpenInventory().getTitle().contains("Buy Menu (")) {
                player.getOpenInventory().getTopInventory().setContents(inventoryPages.get(page));
                return;
            }
            player.openInventory(buyMenu);
        }
    }

    private List<ItemStack[]> getBuyMenuPages(List<ItemStack> offers) {
        if (offers.size() == 0) {
            return new ArrayList<>();
        }

        int maxPages = calculateMaxPages(offers.size(), itemsPerPage);

        List<ItemStack[]> buyMenuPages = new ArrayList<>();
        while (offers.size() >= itemsPerPage) {
            ItemStack[] buyMenuPage = new ItemStack[45];
            int removedItemsFromOffers = 0;

            for (int i = 0; removedItemsFromOffers < itemsPerPage; removedItemsFromOffers++) {
                buyMenuPage[i] = offers.get(i);
                offers.remove(i);
            }

            buyMenuPages.add(addBottomMenuRow(buyMenuPage, buyMenuPages.size(), maxPages).toArray(new ItemStack[0]));
            buyMenuPages.add(buyMenuPage);
            buyMenuPage = new ItemStack[45];
        }
        if (offers.size() == 0) {
            return buyMenuPages;
        } else {
            ItemStack[] buyMenuPage = new ItemStack[45];
            for (int i = 0; i < offers.size(); i++) {
                buyMenuPage[i] = offers.get(i);
            }
            buyMenuPages.add(addBottomMenuRow(buyMenuPage, buyMenuPages.size(), maxPages).toArray(new ItemStack[0]));
        }
        return buyMenuPages;
    }

    private List<ItemStack> addBottomMenuRow(ItemStack[] buyMenuPage, int currentPage, int maxPages) {
        // Only one page exists
        if (currentPage == 0 && maxPages == 1) {
            for (int i = 36; i < pageSize; i++) {
                buyMenuPage[i] = StandardInventoryItems.MENU_GLASS;
                if (i == 40) {
                    buyMenuPage[i] = StandardInventoryItems.MENU_CLOSE;
                }
            }
            return Arrays.asList(buyMenuPage);
        }
        // More than one page exist, but we are on the first page
        if (currentPage == 0 && maxPages > 1) {
            for (int i = 36; i < pageSize; i++) {
                buyMenuPage[i] = StandardInventoryItems.MENU_GLASS;
                if (i == 40) {
                    buyMenuPage[i] = StandardInventoryItems.MENU_CLOSE;
                }
                if (i == pageSize - 1) {
                    buyMenuPage[i] = StandardInventoryItems.ARROW_NEXT;
                }
            }
            return Arrays.asList(buyMenuPage);
        }
        // More than one page exist, we are not on the first and not on the last page
        if (currentPage > 0 && currentPage < maxPages - 1) {
            for (int i = 36; i < pageSize; i++) {
                buyMenuPage[i] = StandardInventoryItems.MENU_GLASS;
                if (i == 36) {
                    buyMenuPage[i] = StandardInventoryItems.ARROW_PREVIOUS;
                }
                if (i == 40) {
                    buyMenuPage[i] = StandardInventoryItems.MENU_CLOSE;
                }
                if (i == pageSize - 1) {
                    buyMenuPage[i] = StandardInventoryItems.ARROW_NEXT;
                }
            }
            return Arrays.asList(buyMenuPage);
        }
        // More than one page exist, and we are on the last page
        if (currentPage > 0 && currentPage == maxPages - 1) {
            for (int i = 36; i < pageSize; i++) {
                buyMenuPage[i] = StandardInventoryItems.MENU_GLASS;
                if (i == 36) {
                    buyMenuPage[i] = StandardInventoryItems.ARROW_PREVIOUS;
                }
                if (i == 40) {
                    buyMenuPage[i] = StandardInventoryItems.MENU_CLOSE;
                }
            }
        }
        return Arrays.asList(buyMenuPage);
    }

    private int calculateMaxPages(int offers, int itemsPerPage) {
        int pages = 0;
        while (offers >= itemsPerPage) {
            pages += 1;
            offers -= itemsPerPage;
        }
        if (offers > 0) {
            pages += 1;
        }
        return pages;
    }

}
