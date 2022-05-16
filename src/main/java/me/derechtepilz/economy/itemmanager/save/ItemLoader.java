package me.derechtepilz.economy.itemmanager.save;

import me.derechtepilz.economy.utility.NamespacedKeys;
import me.derechtepilz.economy.utility.datatypes.UUIDDataType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class ItemLoader {
    public ItemLoader() {
    }

    public ItemStack getPlayerItem(String itemId, int price, int amount, UUID itemUuid, UUID creatorUuid) {
        ItemStack item = new ItemStack(Material.valueOf(itemId), amount);
        ItemMeta meta = item.getItemMeta();

        UUIDDataType uuidDataType = new UUIDDataType();
        meta.getPersistentDataContainer().set(NamespacedKeys.UUID.getKey(), PersistentDataType.BYTE_ARRAY, uuidDataType.toPrimitive(itemUuid));
        meta.getPersistentDataContainer().set(NamespacedKeys.PRICE.getKey(), PersistentDataType.INTEGER, price);
        meta.getPersistentDataContainer().set(NamespacedKeys.CREATOR.getKey(), PersistentDataType.BYTE_ARRAY, uuidDataType.toPrimitive(creatorUuid));

        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getConsoleItem(String itemId, int price, int amount, UUID itemUuid) {
        ItemStack item = new ItemStack(Material.valueOf(itemId), amount);
        ItemMeta meta = item.getItemMeta();

        UUIDDataType uuidDataType = new UUIDDataType();
        meta.getPersistentDataContainer().set(NamespacedKeys.UUID.getKey(), PersistentDataType.BYTE_ARRAY, uuidDataType.toPrimitive(itemUuid));
        meta.getPersistentDataContainer().set(NamespacedKeys.PRICE.getKey(), PersistentDataType.INTEGER, price);
        meta.getPersistentDataContainer().set(NamespacedKeys.CREATOR.getKey(), PersistentDataType.STRING, "console");

        item.setItemMeta(meta);
        return item;
    }
}
