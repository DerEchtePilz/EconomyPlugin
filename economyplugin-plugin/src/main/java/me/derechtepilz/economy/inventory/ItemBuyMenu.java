package me.derechtepilz.economy.inventory;

import me.derechtepilz.economy.Main;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ItemBuyMenu extends InventoryManager {
    private final Main main;
    public ItemBuyMenu(Main main) {
        this.main = main;
    }

    private final int inventoryPages = getInventoryPages();

    @Override
    public ItemStack[] createInventoryItems(List<ItemStack> items) {
        return new ItemStack[0];
    }

    @Override
    public List<ItemStack[]> createInventoryPages() {
        return null;
    }

    @Override
    public int getInventoryPages() {
        int pages;
        if (main.getOfferedItemsList().size() % 45 == 0) {
            pages = main.getOfferedItemsList().size() / 45;
        } else {
            pages = (main.getOfferedItemsList().size() < 45) ? 1 : main.getOfferedItemsList().size() / 45 + 1;
        }
        return pages;
    }
}
