package me.derechtepilz.economy.inventorymanagement;

import me.derechtepilz.economy.utility.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class StandardInventoryItems {

    private StandardInventoryItems() {

    }

    public static final ItemStack MENU_GLASS = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName("§7").build();
    public static final ItemStack MENU_CLOSE = new ItemBuilder(Material.BARRIER).setName("§cClose").build();
    public static final ItemStack ARROW_PREVIOUS = new ItemBuilder(Material.ARROW).setName("§aSwitch to: Previous page").build();
    public static final ItemStack ARROW_NEXT = new ItemBuilder(Material.ARROW).setName("§aSwitch to: Next page").build();

}
