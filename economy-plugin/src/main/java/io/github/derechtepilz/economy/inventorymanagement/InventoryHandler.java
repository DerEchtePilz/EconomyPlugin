package io.github.derechtepilz.economy.inventorymanagement;

import io.github.derechtepilz.economy.Main;
import io.github.derechtepilz.economy.offers.BuyOfferMenu;
import io.github.derechtepilz.economy.offers.CancelOfferMenu;
import io.github.derechtepilz.economy.utility.DataHandler;
import io.github.derechtepilz.economy.utility.NamespacedKeys;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class InventoryHandler {

    private final Main main;
    private boolean timerRunning = true;

    private final BuyOfferMenu buyOfferMenu;
    private final CancelOfferMenu cancelOfferMenu;

    public InventoryHandler(Main main) {
        this.main = main;
        this.buyOfferMenu = new BuyOfferMenu(main);
        this.cancelOfferMenu = new CancelOfferMenu(main);
    }

    public void openEconomyMenu(Player player, int page, InventoryType type) {
        if (player.getPersistentDataContainer().has(NamespacedKeys.INVENTORY_TYPE, PersistentDataType.STRING)) {
            player.getPersistentDataContainer().remove(NamespacedKeys.INVENTORY_TYPE);
        }
        if (player.getPersistentDataContainer().has(NamespacedKeys.INVENTORY_PAGE, PersistentDataType.INTEGER)) {
            player.getPersistentDataContainer().remove(NamespacedKeys.INVENTORY_PAGE);
        }
        switch (type) {
            case BUY_MENU -> {
                DataHandler.setBuyMenuData(player);
                buyOfferMenu.openInventory(player, page);
            }
            case CANCEL_MENU -> {
                DataHandler.setCancelMenuData(player);
                cancelOfferMenu.openInventory(player, page);
            }
            case NONE -> {
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    public int updateOffersAndInventory() {
        return Bukkit.getScheduler().scheduleSyncRepeatingTask(main, () -> {
            if (timerRunning) {
                main.getItemUpdater().getUpdatedItems();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    int page = (player.getPersistentDataContainer().has(NamespacedKeys.INVENTORY_PAGE, PersistentDataType.INTEGER)) ? player.getPersistentDataContainer().get(NamespacedKeys.INVENTORY_PAGE, PersistentDataType.INTEGER) : 0;
                    InventoryType type = (player.getPersistentDataContainer().has(NamespacedKeys.INVENTORY_TYPE, PersistentDataType.STRING)) ? InventoryType.valueOf(player.getPersistentDataContainer().get(NamespacedKeys.INVENTORY_TYPE, PersistentDataType.STRING)) : InventoryType.NONE;
                    openEconomyMenu(player, page, type);
                }
            }
        }, 20, 20);
    }

    public boolean isTimerRunning() {
        return timerRunning;
    }

    public void setTimerRunning(boolean timerRunning) {
        this.timerRunning = timerRunning;
    }

    public enum InventoryType {
        BUY_MENU,
        CANCEL_MENU,
        NONE
    }
}
