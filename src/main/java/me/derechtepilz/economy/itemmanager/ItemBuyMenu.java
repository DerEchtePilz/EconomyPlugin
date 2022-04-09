package me.derechtepilz.economy.itemmanager;

import me.derechtepilz.economy.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ItemBuyMenu implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {

    }

    public void openBuyMenu(Player player) {
        ItemStack[] playerOffers = new ItemStack[Main.getInstance().getOfferedItemsList().size()];
        ItemStack[] specialOffers = new ItemStack[Main.getInstance().getSpecialOffers().get("console").length];


    }
}
