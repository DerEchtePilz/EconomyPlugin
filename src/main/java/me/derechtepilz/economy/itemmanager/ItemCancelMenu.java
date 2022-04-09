package me.derechtepilz.economy.itemmanager;

import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.utility.ItemBuilder;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ItemCancelMenu implements Listener {

    private final List<ItemStack[]> inventories = new ArrayList<>();
    private int currentInventory;
    private Inventory inventory;

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getView().getTitle().contains(TranslatableChatComponent.read("itemCancelMenu.inventory_title")) && Objects.equals(event.getClickedInventory(), event.getView().getTopInventory())) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            if (event.getCurrentItem().equals(closeItem)) {
                player.closeInventory();
                return;
            }
            if (event.getCurrentItem().equals(nextPage)) {
                currentInventory += 1;
                inventory = Bukkit.createInventory(null, 54, TranslatableChatComponent.read("itemCancelMenu.inventory_title")+ (currentInventory + 1) + ")");
                inventory.setContents(inventories.get(currentInventory));
                player.openInventory(inventory);
                return;
            }
            if (event.getCurrentItem().equals(previousPage)) {
                currentInventory -= 1;
                inventory = Bukkit.createInventory(null, 54, TranslatableChatComponent.read ("itemCancelMenu.inventory_title") + (currentInventory + 1) + ")");
                inventory.setContents(inventories.get(currentInventory));
                player.openInventory(inventory);
            }
        }
    }

    /**
     *
     * @param player The player that cancels an offer
     * @param offeredItems The offers the player has made
     */
    public void openOfferCancelMenu(Player player, ItemStack[] offeredItems) {
        // Prepare inventory pages
        ItemStack[] cancelOfferMenuItems = new ItemStack[offeredItems.length];
        for (int i = 0; i < offeredItems.length; i++) {
            cancelOfferMenuItems[i] = offeredItems[i];
        }
        if (cancelOfferMenuItems.length % 45 != 0) {
            ItemStack[] updatedCancelOfferMenuItem = new ItemStack[Main.getInstance().findNextMultiple(cancelOfferMenuItems.length, 45)];
            for (int i = offeredItems.length; i < updatedCancelOfferMenuItem.length; i++) {
                updatedCancelOfferMenuItem[i] = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName("§7").build();
            }
            cancelOfferMenuItems = updatedCancelOfferMenuItem;
        }
        int inventoryCount = cancelOfferMenuItems.length / 45;
        for (int i = 0; i < inventoryCount; i++) {
            ItemStack[] inventory = new ItemStack[54];
            for (int j = 0; j < 45; j++) {
                inventory[j] = cancelOfferMenuItems[j];
            }
            if (i == 0) {
                for (int j = 45; j < inventory.length; j++) {
                    inventory[j] = menuGlass;
                    inventory[49] = closeItem;
                    if (inventoryCount > 1) {
                        inventory[53] = nextPage;
                    }
                }
            } else if (i < inventoryCount - 1) {
                for (int j = 45; j < inventory.length; j++) {
                    inventory[j] = menuGlass;
                    inventory[45] = previousPage;
                    inventory[49] = closeItem;
                    inventory[53] = nextPage;
                }
            } else if (i == inventoryCount - 1) {
                for (int j = 45; j < inventory.length; j++) {
                    inventory[j] = menuGlass;
                    inventory[45] = previousPage;
                    inventory[49] = closeItem;
                }
            }
            inventories.add(inventory);
        }
        // Create inventory
        inventory = Bukkit.createInventory(null, 54, TranslatableChatComponent.read("itemCancelMenu.inventory_title") + " (1)");
        inventory.setContents(inventories.get(0));
        currentInventory = 0;
        player.openInventory(inventory);
    }

    private final ItemStack closeItem = new ItemBuilder(Material.BARRIER).setName(TranslatableChatComponent.read("itemCancelMenu.close_item_name")).build();
    private final ItemStack nextPage = new ItemBuilder(Material.ARROW).setName(TranslatableChatComponent.read("itemCancelMenu.next_page_name")).build();
    private final ItemStack previousPage = new ItemBuilder(Material.ARROW).setName(TranslatableChatComponent.read("itemCancelMenu.previous_page_name")).build();
    private final ItemStack menuGlass = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName(TranslatableChatComponent.read("itemCancelMenu.menu_glass_name")).build();
}
