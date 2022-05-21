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

import java.util.*;

public class ItemCancelMenu implements Listener {

    private final HashMap<UUID, List<ItemStack[]>> inventories = new HashMap<>();
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
                inventory = Bukkit.createInventory(null, 54, TranslatableChatComponent.read("itemCancelMenu.inventory_title") + (currentInventory + 1) + ")");
                inventory.setContents(inventories.get(player.getUniqueId()).get(currentInventory));
                player.openInventory(inventory);
                return;
            }
            if (event.getCurrentItem().equals(previousPage)) {
                currentInventory -= 1;
                inventory = Bukkit.createInventory(null, 54, TranslatableChatComponent.read ("itemCancelMenu.inventory_title") + (currentInventory + 1) + ")");
                inventory.setContents(inventories.get(player.getUniqueId()).get(currentInventory));
                player.openInventory(inventory);
                return;
            }
            if (Main.getInstance().getOfferedItemsList().contains(event.getCurrentItem())) {
                ItemStack cancelledItem = new ItemStack(event.getCurrentItem().getType(), event.getCurrentItem().getAmount());
                player.getInventory().addItem(cancelledItem);

                ItemUtils.cancelSalableItem(event.getCurrentItem());
                player.sendMessage(TranslatableChatComponent.read("itemCancelMenu.cancelled_item"));

                ItemStack[] shownInventory = inventories.get(player.getUniqueId()).get(currentInventory);
                shownInventory[event.getSlot()] = itemCancelled;
                inventories.get(player.getUniqueId()).set(currentInventory, shownInventory);
                inventory.setContents(inventories.get(player.getUniqueId()).get(currentInventory));
            }
        }
    }

    /**
     *
     * @param player The player that cancels an offer
     * @param offeredItems The offers the player has made
     */
    public void openOfferCancelMenu(Player player, ItemStack[] offeredItems) {
        List<ItemStack[]> offers = new ArrayList<>();

        // Prepare inventory pages
        ItemStack[] cancelOfferMenuItems = new ItemStack[offeredItems.length];
        for (int i = 0; i < offeredItems.length; i++) {
            cancelOfferMenuItems[i] = offeredItems[i];
        }
        if (cancelOfferMenuItems.length % 45 != 0) {
            ItemStack[] updatedCancelOfferMenuItem = new ItemStack[Main.getInstance().findNextMultiple(cancelOfferMenuItems.length, 45)];
            for (int i = 0; i < offeredItems.length; i++) {
                updatedCancelOfferMenuItem[i] = offeredItems[i];
            }
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
            offers.add(inventory);
        }
        inventories.put(player.getUniqueId(), offers);
        // Create inventory
        inventory = Bukkit.createInventory(null, 54, TranslatableChatComponent.read("itemCancelMenu.inventory_title") + "1)");
        inventory.setContents(inventories.get(player.getUniqueId()).get(0));
        currentInventory = 0;
        player.openInventory(inventory);
    }

    private final ItemStack closeItem = new ItemBuilder(Material.BARRIER).setName(TranslatableChatComponent.read("items.title.close")).build();
    private final ItemStack nextPage = new ItemBuilder(Material.ARROW).setName(TranslatableChatComponent.read("items.title.next_page")).build();
    private final ItemStack previousPage = new ItemBuilder(Material.ARROW).setName(TranslatableChatComponent.read("items.title.previous_page")).build();
    private final ItemStack menuGlass = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName(TranslatableChatComponent.read("items.title.menu_glass")).build();
    private final ItemStack itemCancelled = new ItemBuilder(Material.BARRIER).setName(TranslatableChatComponent.read("itemCancelMenu_item_cancelled")).build();
}
