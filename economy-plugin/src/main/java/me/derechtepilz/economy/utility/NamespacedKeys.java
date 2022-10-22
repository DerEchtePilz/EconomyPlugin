package me.derechtepilz.economy.utility;

import me.derechtepilz.economy.Main;
import org.bukkit.NamespacedKey;

public class NamespacedKeys {

	// Player namespaces
	public static final NamespacedKey INVENTORY_TYPE = new NamespacedKey(Main.getPlugin(Main.class), "inventoryType");
	public static final NamespacedKey INVENTORY_PAGE = new NamespacedKey(Main.getPlugin(Main.class), "inventoryPage");
	public static final NamespacedKey CAN_INVENTORY_OPEN = new NamespacedKey(Main.getPlugin(Main.class), "canInventoryOpen");
	public static final NamespacedKey ITEM_FILTER = new NamespacedKey(Main.getPlugin(Main.class), "itemFilter");

	// Permission namespaces
	public static final NamespacedKey PERMISSION = new NamespacedKey(Main.getPlugin(Main.class), "permissions");
	public static final NamespacedKey PERMISSION_CONVERSION_300 = new NamespacedKey(Main.getPlugin(Main.class), "permissionConverted300");

	// Item namespaces
	public static final NamespacedKey ITEM_PRICE = new NamespacedKey(Main.getPlugin(Main.class), "itemPrice");
	public static final NamespacedKey ITEM_SELLER = new NamespacedKey(Main.getPlugin(Main.class), "itemSeller");
	public static final NamespacedKey ITEM_UUID = new NamespacedKey(Main.getPlugin(Main.class), "itemUuid");

}
