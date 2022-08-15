package me.derechtepilz.economy.offers;

import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.inventorymanagement.StandardInventoryItems;
import me.derechtepilz.economy.utility.DataHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class BuyOfferMenuListener implements Listener {

    private final Main main;
    public BuyOfferMenuListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getView().getTitle().contains("Buy Menu (")) {
            if (event.getClickedInventory() == null) return;
            event.setCancelled(event.getClickedInventory().equals(event.getView().getTopInventory()));

            if (event.getCurrentItem() == null) return;
            if (event.getCurrentItem().equals(StandardInventoryItems.MENU_CLOSE)) {
                player.closeInventory();
                return;
            }
            if (event.getCurrentItem().equals(StandardInventoryItems.ARROW_NEXT)) {
                DataHandler.updateMenuPage(player, DataHandler.getCurrentPage(player) + 1);
                return;
            }
            if (event.getCurrentItem().equals(StandardInventoryItems.ARROW_PREVIOUS)) {
                DataHandler.updateMenuPage(player, DataHandler.getCurrentPage(player) - 1);
            }
            // TODO: Adding purchases
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (event.getView().getTitle().contains("Buy Menu (")) {
            DataHandler.removeBuyMenuData(player);
        }
    }
}
