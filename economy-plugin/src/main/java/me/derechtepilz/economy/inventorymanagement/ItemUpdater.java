package me.derechtepilz.economy.inventorymanagement;

import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.itemmanagement.Item;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemUpdater {

    private final Main main;

    public ItemUpdater(Main main) {
        this.main = main;
    }

    public List<ItemStack> getUpdatedItems() {
        List<ItemStack> updatedItems = new ArrayList<>();
        for (UUID uuid : main.getRegisteredItems().keySet()) {
            Item item = main.getRegisteredItems().get(uuid);
            ItemStack updatedItem = item.decreaseDurationAndUpdate();
            if (updatedItem == null) {
                continue;
            }
            updatedItems.add(updatedItem);
        }
        return updatedItems;
    }

}
