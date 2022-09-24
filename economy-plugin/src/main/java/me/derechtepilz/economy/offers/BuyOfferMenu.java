package me.derechtepilz.economy.offers;

import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.inventorymanagement.InventoryUtility;
import me.derechtepilz.economy.itemmanagement.Item;
import me.derechtepilz.economy.utility.DataHandler;
import me.derechtepilz.economy.utility.NamespacedKeys;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BuyOfferMenu {

    private final int itemsPerPage = 4 * 9;
    private static final int PAGE_SIZE = 5 * 9;
    private final HashMap<UUID, List<ItemStack[]>> inventoryPages = new HashMap<>();
    private Inventory buyMenu;

    private final Main main;

    public BuyOfferMenu(Main main) {
        this.main = main;
    }

    public void openInventory(Player player, int page) {
        preparePlayerItems(player);
        if (inventoryPages.get(player.getUniqueId()).size() == 0) {
            if (player.getOpenInventory().getTitle().contains("Buy Menu (")) {
                player.closeInventory();
            }
            return;
        }
        if (buyMenu == null) {
            buyMenu = Bukkit.createInventory(null, 5 * 9, "Buy Menu (" + (page + 1) + ")");
        }
        buyMenu.setContents(inventoryPages.get(player.getUniqueId()).get(page));

        if (DataHandler.canInventoryOpen(player)) {
            if (player.getOpenInventory().getTitle().contains("Buy Menu (")) {
                player.getOpenInventory().getTopInventory().setContents(inventoryPages.get(player.getUniqueId()).get(page));
                return;
            }
            player.openInventory(buyMenu);
        }
    }

    private void preparePlayerItems(Player player) {
        List<ItemStack> offers = new ArrayList<>();
        String filterMaterialName = player.getPersistentDataContainer().get(NamespacedKeys.ITEM_FILTER, PersistentDataType.STRING);
        if (filterMaterialName == null) {
            for (UUID uuid : main.getRegisteredItems().keySet()) {
                Item item = main.getRegisteredItems().get(uuid);
                if (item == null) {
                    continue;
                }
                offers.add(item.getItemStack());
            }
        } else {
            Material filterMaterial = Material.matchMaterial(filterMaterialName);
            for (UUID uuid : main.getRegisteredItems().keySet()) {
                Item item = main.getRegisteredItems().get(uuid);
                if (item == null) {
                    continue;
                }
                if (!item.getItemStack().getType().equals(filterMaterial)) {
                    continue;
                }
                offers.add(item.getItemStack());
            }
        }

        if (offers.size() == 0) {
            if (inventoryPages.get(player.getUniqueId()).size() >= 1) {
                inventoryPages.get(player.getUniqueId()).clear();
            }
            return;
        }

        List<ItemStack[]> inventoryPages = getBuyMenuPages(offers);
        this.inventoryPages.put(player.getUniqueId(), inventoryPages);
    }

    private List<ItemStack[]> getBuyMenuPages(List<ItemStack> offers) {
        if (offers.size() == 0) {
            return new ArrayList<>();
        }

        int maxPages = InventoryUtility.calculateMaxPages(offers.size(), itemsPerPage);

        List<ItemStack[]> buyMenuPages = new ArrayList<>();
        while (offers.size() >= itemsPerPage) {
            ItemStack[] buyMenuPage = new ItemStack[45];
            int removedItemsFromOffers = 0;

            for (int i = 0; removedItemsFromOffers < itemsPerPage; removedItemsFromOffers++) {
                buyMenuPage[i] = offers.get(i);
                offers.remove(i);
            }

            buyMenuPages.add(InventoryUtility.addBottomMenuRow(buyMenuPage, buyMenuPages.size(), maxPages, PAGE_SIZE).toArray(new ItemStack[0]));
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
            buyMenuPages.add(InventoryUtility.addBottomMenuRow(buyMenuPage, buyMenuPages.size(), maxPages, PAGE_SIZE).toArray(new ItemStack[0]));
        }
        return buyMenuPages;
    }

}
