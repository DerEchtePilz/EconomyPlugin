package me.derechtepilz.economy.itemmanager;

import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.utility.datatypes.UUIDDataType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ItemUtils {
    private ItemUtils() {

    }

    public static void createSalableItem(String sellerName, ItemStack item, int price) {
        UUIDDataType uuidDataType = new UUIDDataType();
        ItemStack salableItem = new ItemStack(item.getType(), item.getAmount());
        ItemMeta salableItemMeta = salableItem.getItemMeta();

        UUID itemUuid = UUID.randomUUID();
        salableItemMeta.getPersistentDataContainer().set(Main.getInstance().getUuid(), PersistentDataType.BYTE_ARRAY, uuidDataType.toPrimitive(itemUuid));
        salableItemMeta.getPersistentDataContainer().set(Main.getInstance().getPrice(), PersistentDataType.INTEGER, price);

        Player player = Bukkit.getPlayer(sellerName);
        ItemStack[] offers;
        if (player == null) {
            salableItemMeta.getPersistentDataContainer().set(Main.getInstance().getCreator(), PersistentDataType.STRING, "console");

            salableItem.setItemMeta(salableItemMeta);
            offers = constructSalableItems(sellerName, salableItem, true);
            Main.getInstance().getSpecialOffers().put("console", offers);
        } else {
            salableItemMeta.getPersistentDataContainer().set(Main.getInstance().getCreator(), PersistentDataType.BYTE_ARRAY, uuidDataType.toPrimitive(player.getUniqueId()));

            salableItem.setItemMeta(salableItemMeta);
            offers = constructSalableItems(sellerName, salableItem, true);
            Main.getInstance().getPlayerOffers().put(player.getUniqueId(), offers);
        }
    }

    public static void cancelSalableItem(ItemStack offerToCancel) {
        UUIDDataType uuidDataType = new UUIDDataType();
        ItemMeta meta = offerToCancel.getItemMeta();

        UUID sellerUuid = uuidDataType.fromPrimitive(meta.getPersistentDataContainer().get(Main.getInstance().getCreator(), PersistentDataType.BYTE_ARRAY));

        ItemStack[] offers = constructSalableItems(Bukkit.getPlayer(sellerUuid).getName(), offerToCancel, false);
        Main.getInstance().getPlayerOffers().put(sellerUuid, offers);
        Main.getInstance().getOfferedItemsList().remove(offerToCancel);
    }

    private static ItemStack[] constructSalableItems(String sellerName, ItemStack salableItem, boolean addOffer) {
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
            if (Main.getInstance().getPlayerOffers().containsKey(player.getUniqueId())) {
                offeredItems = Main.getInstance().getPlayerOffers().get(player.getUniqueId()).length;
                offers = Main.getInstance().getPlayerOffers().get(player.getUniqueId());
            } else {
                offeredItems = 0;
                offers = new ItemStack[offeredItems];
            }
        }
        if (addOffer) {
            ItemStack[] items = new ItemStack[offeredItems + 1];
            for (int i = 0; i < offers.length; i++) {
                items[i] = offers[i];
            }
            items[offers.length] = salableItem;
            return items;
        } else {
            List<ItemStack> updatedOffers = new ArrayList<>();
            for (ItemStack item : offers) {
                if (!item.equals(salableItem)) {
                    updatedOffers.add(item);
                }
            }
            return updatedOffers.toArray(new ItemStack[0]);
        }
    }
}
