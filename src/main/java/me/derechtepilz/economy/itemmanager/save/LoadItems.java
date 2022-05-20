package me.derechtepilz.economy.itemmanager.save;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.derechtepilz.economy.Main;
import org.bukkit.inventory.ItemStack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LoadItems {
    private List<ItemStack> playerOffers = new ArrayList<>();
    private List<ItemStack> specialOffers = new ArrayList<>();

    public LoadItems() {
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
                saveSellingItems(new Base64Utils().decodeBase64(builder.toString()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveSellingItems(String json) {
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

                ItemStack loadedItem = new ItemLoader().getPlayerItem(itemId, price, amount, itemUuid, UUID.fromString(uuid));
                Main.getInstance().getOfferedItemsList().add(loadedItem);
                this.playerOffers.add(loadedItem);
            }
            Main.getInstance().getPlayerOffers().put(UUID.fromString(uuid), buildOffersArray(this.playerOffers));
            this.playerOffers = new ArrayList<>();
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

                ItemStack loadedItem = new ItemLoader().getConsoleItem(itemId, price, amount, itemUuid);
                Main.getInstance().getOfferedItemsList().add(loadedItem);
                this.specialOffers.add(loadedItem);
            }
            Main.getInstance().getSpecialOffers().put("console", buildOffersArray(this.specialOffers));
            this.specialOffers = new ArrayList<>();
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

    private ItemStack[] buildOffersArray(List<ItemStack> offers) {
        ItemStack[] items = new ItemStack[offers.size()];
        for (int i = 0; i < offers.size(); i++) {
            items[i] = offers.get(i);
        }
        return items;
    }
}
