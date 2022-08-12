package me.derechtepilz.economy.utility;

import me.derechtepilz.economy.Main;
import org.bukkit.NamespacedKey;

public class NamespacedKeys {

    // Player namespaces
    public static final NamespacedKey INVENTORY_TYPE = new NamespacedKey(Main.getPlugin(Main.class), "inventoryType");
    public static final NamespacedKey INVENTORY_PAGE = new NamespacedKey(Main.getPlugin(Main.class), "inventoryPage");

}
