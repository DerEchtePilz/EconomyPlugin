package me.derechtepilz.economy.utility.inventorymanagement;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public interface InventoryBase {
    void openInventory(Player player, Inventory inventory);
    void closeInventory(Player player);
    Inventory createInventory(InventoryHolder holder, int size, String title);
}
