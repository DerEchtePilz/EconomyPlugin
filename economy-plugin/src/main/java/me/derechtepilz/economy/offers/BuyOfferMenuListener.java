package me.derechtepilz.economy.offers;

import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.utility.DataHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class BuyOfferMenuListener implements Listener {

    private final Main main;
    public BuyOfferMenuListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (event.getView().getTitle().contains("Buy Menu (")) {
            DataHandler.removeBuyMenuData(player);
        }
    }
}
