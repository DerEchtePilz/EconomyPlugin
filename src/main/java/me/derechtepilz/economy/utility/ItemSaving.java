package me.derechtepilz.economy.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.utility.datatypes.UUIDDataType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.UUID;

public class ItemSaving {
    private ItemSaving() {

    }

    private static JsonObject buildSellingItems() {
        JsonObject offers = new JsonObject();
        JsonObject allOffers = new JsonObject();

        JsonObject playerOffers = new JsonObject();
        for (UUID uuid : Main.getInstance().getOfferingPlayers().keySet()) {
            JsonArray sellingPlayer = new JsonArray();
            for (ItemStack item : Main.getInstance().getOfferingPlayers().get(uuid)) {
                JsonObject sellingItem = new JsonObject();
                UUIDDataType uuidDataType = new UUIDDataType();

                String id = "minecraft:" + item.getType().name().toLowerCase();
                int price = item.getItemMeta().getPersistentDataContainer().get(Main.getInstance().getPrice(), PersistentDataType.INTEGER);
                int amount = item.getAmount();
                UUID itemUuid = uuidDataType.fromPrimitive(item.getItemMeta().getPersistentDataContainer().get(Main.getInstance().getUuid(), PersistentDataType.BYTE_ARRAY));

                sellingItem.addProperty("id", id);
                sellingItem.addProperty("price", price);
                sellingItem.addProperty("amount", amount);
                sellingItem.addProperty("itemUuid", String.valueOf(itemUuid));

                sellingPlayer.add(sellingItem);
            }
            playerOffers.add(String.valueOf(uuid), sellingPlayer);
        }
        allOffers.add("playerOffers", playerOffers);

        JsonObject specialOffers = new JsonObject();
        JsonArray sellingConsole = new JsonArray();
        for (ItemStack item : Main.getInstance().getSpecialOffers().get("console")) {
            JsonObject sellingItem = new JsonObject();

            String id = "minecraft:" + item.getType().name().toLowerCase();
            int price = item.getItemMeta().getPersistentDataContainer().get(Main.getInstance().getPrice(), PersistentDataType.INTEGER);
            int amount = item.getAmount();
            String itemUuid = "console";

            sellingItem.addProperty("id", id);
            sellingItem.addProperty("price", price);
            sellingItem.addProperty("amount", amount);
            sellingItem.addProperty("itemUuid", itemUuid);

            sellingConsole.add(sellingItem);
        }
        specialOffers.add("specialOffers", sellingConsole);
        allOffers.add("specialOffers", specialOffers);

        offers.add("offers", allOffers);

        return offers;
    }

    public static void save() {
        File dir = new File("./plugins/AdvancedItems");
        if (!dir.exists()) {
            dir.mkdir();
        }

        File file = new File(dir, "sales.json");
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
            gson.toJson(object, writer);
            writer.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
