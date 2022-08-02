package me.derechtepilz.economy.inventorymanagement;

import me.derechtepilz.economy.Main;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class InventoryHandler {

    private final Main main;
    private boolean timerRunning = true;

    public InventoryHandler(Main main) {
        this.main = main;
    }

    public void updateOffersAndInventory() {
        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, () -> {
            if (timerRunning) {
                List<ItemStack> updatedItems = main.getItemUpdater().getUpdatedItems();
            }
        }, 20, 20);
    }

    public boolean isTimerRunning() {
        return timerRunning;
    }

    public void setTimerRunning(boolean timerRunning) {
        this.timerRunning = timerRunning;
    }
}
