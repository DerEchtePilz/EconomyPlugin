package me.derechtepilz.economy.utility;

import me.derechtepilz.economy.inventorymanagement.InventoryHandler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class DataHandler {

	private DataHandler() {}

	public static void setBuyMenuData(Player player) {
		player.getPersistentDataContainer().set(NamespacedKeys.INVENTORY_PAGE, PersistentDataType.INTEGER, 0);
		player.getPersistentDataContainer().set(NamespacedKeys.INVENTORY_TYPE, PersistentDataType.STRING, InventoryHandler.InventoryType.BUY_MENU.name());
		player.getPersistentDataContainer().set(NamespacedKeys.CAN_INVENTORY_OPEN, PersistentDataType.BYTE, (byte) 1);
	}

	public static void setBuyMenuData(Player player, ItemStack filter) {
		player.getPersistentDataContainer().set(NamespacedKeys.INVENTORY_PAGE, PersistentDataType.INTEGER, 0);
		player.getPersistentDataContainer().set(NamespacedKeys.INVENTORY_TYPE, PersistentDataType.STRING, InventoryHandler.InventoryType.BUY_MENU.name());
		player.getPersistentDataContainer().set(NamespacedKeys.CAN_INVENTORY_OPEN, PersistentDataType.BYTE, (byte) 1);
		player.getPersistentDataContainer().set(NamespacedKeys.ITEM_FILTER, PersistentDataType.STRING, filter.getType().toString());
	}

	public static void setCancelMenuData(Player player) {
		player.getPersistentDataContainer().set(NamespacedKeys.INVENTORY_PAGE, PersistentDataType.INTEGER, 0);
		player.getPersistentDataContainer().set(NamespacedKeys.INVENTORY_TYPE, PersistentDataType.STRING, InventoryHandler.InventoryType.CANCEL_MENU.name());
		player.getPersistentDataContainer().set(NamespacedKeys.CAN_INVENTORY_OPEN, PersistentDataType.BYTE, (byte) 1);
	}

	public static void removeMenuData(Player player) {
		player.getPersistentDataContainer().remove(NamespacedKeys.INVENTORY_PAGE);
		player.getPersistentDataContainer().remove(NamespacedKeys.INVENTORY_TYPE);
		player.getPersistentDataContainer().remove(NamespacedKeys.CAN_INVENTORY_OPEN);
		player.getPersistentDataContainer().remove(NamespacedKeys.ITEM_FILTER);
	}

	public static void updateMenuPage(Player player, int newPage) {
		player.getPersistentDataContainer().set(NamespacedKeys.INVENTORY_PAGE, PersistentDataType.INTEGER, newPage);
	}

	@SuppressWarnings("ConstantConditions")
	public static int getCurrentPage(Player player) {
		return player.getPersistentDataContainer().has(NamespacedKeys.INVENTORY_PAGE, PersistentDataType.INTEGER) ? player.getPersistentDataContainer().get(NamespacedKeys.INVENTORY_PAGE, PersistentDataType.INTEGER) : 0;
	}

	@SuppressWarnings("ConstantConditions")
	public static boolean canInventoryOpen(Player player) {
		return player.getPersistentDataContainer().get(NamespacedKeys.CAN_INVENTORY_OPEN, PersistentDataType.BYTE) == 1;
	}

}
