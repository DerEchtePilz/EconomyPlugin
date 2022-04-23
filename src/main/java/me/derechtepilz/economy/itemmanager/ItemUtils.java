package me.derechtepilz.economy.itemmanager;

import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.utility.datatypes.UUIDDataType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class ItemUtils {
    private ItemUtils() {

    }

    public static void createSalableItem(String sellerName, ItemStack item, int price) {
        ItemMeta salableItemMeta = item.getItemMeta();

        UUID itemUuid = UUID.randomUUID();
        salableItemMeta.getPersistentDataContainer().set(Main.getInstance().getUuid(), new UUIDDataType(), itemUuid);
        salableItemMeta.getPersistentDataContainer().set(Main.getInstance().getPrice(), PersistentDataType.INTEGER, price);

        Player player = Bukkit.getPlayer(sellerName);
        ItemStack[] offers;
        if (player == null) {
            salableItemMeta.getPersistentDataContainer().set(Main.getInstance().getCreator(), PersistentDataType.STRING, "console");

            item.setItemMeta(salableItemMeta);
            offers = constructSalableItems(sellerName, item);
            Main.getInstance().getSpecialOffers().put("console", offers);
        } else {
            salableItemMeta.getPersistentDataContainer().set(Main.getInstance().getCreator(), new UUIDDataType(), player.getUniqueId());

            item.setItemMeta(salableItemMeta);
            offers = constructSalableItems(sellerName, item);
            Main.getInstance().getOfferingPlayers().put(player.getUniqueId(), offers);
        }

        Main.getInstance().getOfferedItems().put(itemUuid, item);
    }

    public static void createBoughtItem(ItemStack item) {

    }

    private static ItemStack[] constructSalableItems(String sellerName, ItemStack salableItem) {
        Player player = Bukkit.getPlayer(sellerName);
        int offeredItems;
        ItemStack[] offers;
        if (player == null) {
            if (Main.getInstance().getSpecialOffers().containsKey("console")) {
                offeredItems = Main.getInstance().getSpecialOffers().get("console").length;
                offers = Main.getInstance().getSpecialOffers().get("console");
            } else {
                offeredItems = 0;
                offers = new ItemStack[offeredItems];
            }
        } else {
            if (Main.getInstance().getOfferingPlayers().containsKey(player.getUniqueId())) {
                offeredItems = Main.getInstance().getOfferingPlayers().get(player.getUniqueId()).length;
                offers = Main.getInstance().getOfferingPlayers().get(player.getUniqueId());
            } else {
                offeredItems = 0;
                offers = new ItemStack[offeredItems];
            }
        }
        ItemStack[] items = new ItemStack[offeredItems + 1];
        for (int i = 0; i < offers.length; i++) {
            items[i] = offers[i];
        }
        items[offers.length] = salableItem;
        return items;
    }
}