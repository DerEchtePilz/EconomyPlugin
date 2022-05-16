package me.derechtepilz.economy.utility;

import com.google.gson.*;
import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.utility.datatypes.UUIDDataType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class ItemSaving {
    private ItemSaving() {

    }

    private static List<ItemStack> playerOffers = new ArrayList<>();
    private static List<ItemStack> specialOffers = new ArrayList<>();

    private static JsonObject buildSellingItems() {
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

    private static void saveSellingItems(String json) {
        JsonElement element = JsonParser.parseString(json);
        JsonObject offers = element.getAsJsonObject();

        // Save player offers
        JsonObject playerOffers = offers.getAsJsonObject("offers").getAsJsonObject("playerOffers");
        for (String uuid : playerOffers.keySet()) {
            JsonArray items = playerOffers.getAsJsonArray(uuid);
            for (int i = 0; i < items.size(); i++) {
                JsonObject item = items.get(i).getAsJsonObject();

                String id = item.getAsJsonPrimitive("id").getAsString();
                int price = item.getAsJsonPrimitive("price").getAsInt();
                int amount = item.getAsJsonPrimitive("amount").getAsInt();
                String itemUuidString = item.getAsJsonPrimitive("itemUuid").getAsString();

                UUID itemUuid = UUID.fromString(itemUuidString);
                String itemId = id.substring(10).toUpperCase();

                savePlayerItem(itemId, price, amount, itemUuid, UUID.fromString(uuid));
            }
            Main.getInstance().getPlayerOffers().put(UUID.fromString(uuid), buildOffersArray(ItemSaving.playerOffers));
            ItemSaving.playerOffers = new ArrayList<>();
        }

        // Save special offers
        JsonObject specialOffers = offers.getAsJsonObject("offers").getAsJsonObject("specialOffers");
        if (specialOffers.has("specialOffers")) {
            JsonArray array = specialOffers.getAsJsonArray("specialOffers");
            for (int i = 0; i < array.size(); i++) {
                JsonObject item = array.get(i).getAsJsonObject();

                String id = item.getAsJsonPrimitive("id").getAsString();
                int price = item.getAsJsonPrimitive("price").getAsInt();
                int amount = item.getAsJsonPrimitive("amount").getAsInt();
                String itemUuidString = item.getAsJsonPrimitive("itemUuid").getAsString();

                UUID itemUuid = UUID.fromString(itemUuidString);
                String itemId = id.substring(10).toUpperCase();

                saveConsoleItem(itemId, price, amount, itemUuid);
            }
            Main.getInstance().getSpecialOffers().put("console", buildOffersArray(ItemSaving.specialOffers));
            ItemSaving.specialOffers = new ArrayList<>();
        }

        // Save earned coins
        if (offers.has("earnedCoins")) {
            JsonObject earnedCoins = offers.getAsJsonObject("earnedCoins");
            for (String uuid : earnedCoins.keySet()) {
                int playerEarnedCoins = earnedCoins.getAsJsonPrimitive(uuid).getAsInt();
                Main.getInstance().getEarnedCoins().put(UUID.fromString(uuid), playerEarnedCoins);
            }
        }
    }

    private static void savePlayerItem(String itemId, int price, int amount, UUID itemUuid, UUID creatorUuid) {
        ItemStack item = new ItemStack(Material.valueOf(itemId), amount);
        ItemMeta meta = item.getItemMeta();

        UUIDDataType uuidDataType = new UUIDDataType();
        meta.getPersistentDataContainer().set(NamespacedKeys.UUID.getKey(), PersistentDataType.BYTE_ARRAY, uuidDataType.toPrimitive(itemUuid));
        meta.getPersistentDataContainer().set(NamespacedKeys.PRICE.getKey(), PersistentDataType.INTEGER, price);
        meta.getPersistentDataContainer().set(NamespacedKeys.CREATOR.getKey(), PersistentDataType.BYTE_ARRAY, uuidDataType.toPrimitive(creatorUuid));

        item.setItemMeta(meta);
        Main.getInstance().getOfferedItemsList().add(item);
        playerOffers.add(item);
    }

    private static void saveConsoleItem(String itemId, int price, int amount, UUID itemUuid) {
        ItemStack item = new ItemStack(Material.valueOf(itemId), amount);
        ItemMeta meta = item.getItemMeta();

        UUIDDataType uuidDataType = new UUIDDataType();
        meta.getPersistentDataContainer().set(NamespacedKeys.UUID.getKey(), PersistentDataType.BYTE_ARRAY, uuidDataType.toPrimitive(itemUuid));
        meta.getPersistentDataContainer().set(NamespacedKeys.PRICE.getKey(), PersistentDataType.INTEGER, price);
        meta.getPersistentDataContainer().set(NamespacedKeys.CREATOR.getKey(), PersistentDataType.STRING, "console");

        item.setItemMeta(meta);
        Main.getInstance().getOfferedItemsList().add(item);
        specialOffers.add(item);
    }

    private static ItemStack[] buildOffersArray(List<ItemStack> offers) {
        ItemStack[] items = new ItemStack[offers.size()];
        for (int i = 0; i < offers.size(); i++) {
            items[i] = offers.get(i);
        }
        return items;
    }

    public static void load() {
        try {
            File file = new File(new File("./plugins/Economy"), "SALES_DO_NOT_EDIT");
            if (file.exists()) {
                FileReader fileReader = new FileReader(file);
                BufferedReader reader = new BufferedReader(fileReader);
                String fileContent;
                StringBuilder builder = new StringBuilder();
                while ((fileContent = reader.readLine()) != null) {
                    builder.append(fileContent);
                }
                saveSellingItems(decodeBase64(builder.toString()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
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
            writer.write(encodeBase64(gson.toJson(object)));
            writer.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private static String encodeBase64(String toEncode) {
        return Base64.getEncoder().encodeToString(toEncode.getBytes());
    }

    private static String decodeBase64(String toDecode) {
        return new String(Base64.getDecoder().decode(toDecode));
    }
}
