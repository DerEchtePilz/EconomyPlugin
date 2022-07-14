package me.derechtepilz.economy.inventory;

import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class InventoryManager implements Listener {
    public abstract ItemStack[] createInventoryItems(List<ItemStack> items);

    public abstract List<ItemStack[]> createInventoryPages();

    public abstract int getInventoryPages();
}
