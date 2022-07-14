package me.derechtepilz.economy.itemmanager.save;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.utility.NamespacedKeys;
import me.derechtepilz.economy.utility.datatypes.UUIDDataType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.UUID;

public class SaveItems {
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public SaveItems() {
        File dir = new File("./plugins/Economy");
        if (!dir.exists()) {
            dir.mkdir();
        }

        File file = new File(dir, "SALES_DO_NOT_EDIT");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        JsonObject object = buildSellingItems();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try {
            Writer writer = new FileWriter(file);
            writer.write(new Base64Utils().encodeBase64(gson.toJson(object)));
            writer.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private JsonObject buildSellingItems() {
        JsonObject offers = new JsonObject();
        JsonObject allOffers = new JsonObject();

        JsonObject playerOffers = new JsonObject();
        JsonObject specialOffers = new JsonObject();
        JsonObject earnedCoins = new JsonObject();

        // Build player offers
        for (UUID uuid : Main.getInstance().getPlayerOffers().keySet()) {
            JsonArray sellingPlayer = new JsonArray();
            for (ItemStack item : Main.getInstance().getPlayerOffers().get(uuid)) {
                JsonObject sellingItem = new JsonObject();
                UUIDDataType uuidDataType = new UUIDDataType();

                String id = "minecraft:" + item.getType().name().toLowerCase();
                int price = item.getItemMeta().getPersistentDataContainer().get(NamespacedKeys.PRICE.getKey(), PersistentDataType.INTEGER);
                int amount = item.getAmount();
                UUID itemUuid = uuidDataType.fromPrimitive(item.getItemMeta().getPersistentDataContainer().get(NamespacedKeys.UUID.getKey(), PersistentDataType.BYTE_ARRAY));

                sellingItem.addProperty("id", id);
                sellingItem.addProperty("price", price);
                sellingItem.addProperty("amount", amount);
                sellingItem.addProperty("itemUuid", String.valueOf(itemUuid));

                sellingPlayer.add(sellingItem);
            }
            playerOffers.add(String.valueOf(uuid), sellingPlayer);
        }
        allOffers.add("playerOffers", playerOffers);

        // Build special offers
        if (Main.getInstance().getSpecialOffers().get("console") != null) {
            JsonArray sellingConsole = new JsonArray();
            for (ItemStack item : Main.getInstance().getSpecialOffers().get("console")) {
                JsonObject sellingItem = new JsonObject();
                UUIDDataType uuidDataType = new UUIDDataType();

                String id = "minecraft:" + item.getType().name().toLowerCase();
                int price = item.getItemMeta().getPersistentDataContainer().get(NamespacedKeys.PRICE.getKey(), PersistentDataType.INTEGER);
                int amount = item.getAmount();
                UUID itemUuid = uuidDataType.fromPrimitive(item.getItemMeta().getPersistentDataContainer().get(NamespacedKeys.UUID.getKey(), PersistentDataType.BYTE_ARRAY));

                sellingItem.addProperty("id", id);
                sellingItem.addProperty("price", price);
                sellingItem.addProperty("amount", amount);
                sellingItem.addProperty("itemUuid", String.valueOf(itemUuid));

                sellingConsole.add(sellingItem);
            }
            specialOffers.add("specialOffers", sellingConsole);
        }
        allOffers.add("specialOffers", specialOffers);

        // Build earned coins
        for (UUID uuid : Main.getInstance().getEarnedCoins().keySet()) {
            earnedCoins.addProperty(String.valueOf(uuid), Main.getInstance().getEarnedCoins().get(uuid));
        }
        offers.add("offers", allOffers);
        offers.add("earnedCoins", earnedCoins);
        return offers;
    }
}
