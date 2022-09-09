package me.derechtepilz.economy.offers;

import me.derechtepilz.economy.inventorymanagement.InventoryUtility;
import me.derechtepilz.economy.utility.DataHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BuyOfferMenu {

    private final int itemsPerPage = 4 * 9;
    private static final int PAGE_SIZE = 5 * 9;
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

        int maxPages = InventoryUtility.Companion.calculateMaxPages(offers.size(), itemsPerPage);

        List<ItemStack[]> buyMenuPages = new ArrayList<>();
        while (offers.size() >= itemsPerPage) {
            ItemStack[] buyMenuPage = new ItemStack[45];
            int removedItemsFromOffers = 0;

            for (int i = 0; removedItemsFromOffers < itemsPerPage; removedItemsFromOffers++) {
                buyMenuPage[i] = offers.get(i);
                offers.remove(i);
            }

            buyMenuPages.add(InventoryUtility.Companion.addBottomMenuRow(buyMenuPage, buyMenuPages.size(), maxPages, PAGE_SIZE).toArray(new ItemStack[0]));
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
            buyMenuPages.add(InventoryUtility.Companion.addBottomMenuRow(buyMenuPage, buyMenuPages.size(), maxPages, PAGE_SIZE).toArray(new ItemStack[0]));
        }
        return buyMenuPages;
    }

}
