package io.github.derechtepilz.economy.itemmanagement;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.derechtepilz.economy.Main;
import io.github.derechtepilz.economy.utility.ItemBuilder;
import io.github.derechtepilz.economy.utility.NamespacedKeys;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Type;
import java.util.*;

public class Item {

    // General item and auction infos
    private final Main main;
    private final Material material;
    private final int amount;
    private final double price;
    private final UUID seller;
    private final UUID uuid;
    public int duration;

    // Item meta infos
    private final String displayName;
    private final Map<Enchantment, Integer> enchantments;
    private final int damage;
    private final int customModelData;

    public Item(Main main, Material material, int amount, double price, UUID seller, int duration, String displayName, Map<Enchantment, Integer> enchantments, int damage, int customModelData) {
        this.main = main;
        this.material = material;
        this.amount = amount;
        this.price = price;
        this.seller = seller;
        this.duration = duration;
        this.uuid = UUID.randomUUID();
        this.displayName = displayName;
        this.enchantments = enchantments;
        this.damage = damage;
        this.customModelData = customModelData;
    }

    public Item(Main main, Material material, int amount, double price, UUID seller, UUID uuid, int duration, String displayName, Map<Enchantment, Integer> enchantments, int damage, int customModelData) {
        this.main = main;
        this.material = material;
        this.amount = amount;
        this.price = price;
        this.seller = seller;
        this.duration = duration;
        this.uuid = uuid;
        this.displayName = displayName;
        this.enchantments = enchantments;
        this.damage = damage;
        this.customModelData = customModelData;
    }

    public void register() {
        main.getRegisteredItemUuids().add(uuid);
        main.getOfferingPlayerUuids().add(seller);
        main.getRegisteredItems().put(uuid, new Item(main, material, amount, price, seller, uuid, duration, displayName, enchantments, damage, customModelData));
    }

    public ItemStack getItemStack() {
        List<String> lore = new ArrayList<>();
        lore.add("§6Seller: §a" + Bukkit.getOfflinePlayer(seller).getName());
        lore.add("§6Price: §a" + price);
        lore.add("§6Expires in: §a" + convertSecondsToTime(duration));

        return new ItemBuilder(material).setAmount(amount).setDescription(lore).setName(displayName).setDamage(damage).setEnchantments(enchantments).setCustomModelData(customModelData)
            .setData(NamespacedKeys.ITEM_PRICE, PersistentDataType.DOUBLE, price)
            .setData(NamespacedKeys.ITEM_SELLER, PersistentDataType.STRING, String.valueOf(seller))
            .setData(NamespacedKeys.ITEM_UUID, PersistentDataType.STRING, String.valueOf(uuid))
            .build();
    }

    public ItemStack getBoughtItem() {
        return new ItemBuilder(material).setAmount(amount).setName(displayName).setDamage(damage).setEnchantments(enchantments).setCustomModelData(customModelData).build();
    }

    public void decreaseDurationAndUpdate() {
        if (duration == 0) {
            main.getRegisteredItems().remove(uuid);
            main.getRegisteredItemUuids().remove(uuid);

            List<ItemStack> expiredItems = (main.getExpiredItems().containsKey(seller)) ? main.getExpiredItems().get(seller) : new ArrayList<>();
            expiredItems.add(new ItemBuilder(material).setName(displayName).setAmount(amount).setEnchantments(enchantments).setDamage(damage).setCustomModelData(customModelData).build());
            main.getExpiredItems().put(seller, expiredItems);

            return;
        }
        duration -= 1;
        main.getRegisteredItems().put(uuid, new Item(main, material, amount, price, seller, uuid, duration, displayName, enchantments, damage, customModelData));
    }

    private String convertSecondsToTime(int duration) {
        int seconds = 0;
        int minutes = 0;
        int hours = 0;
        while (duration >= 3600) {
            hours += 1;
            duration -= 3600;
        }
        while (duration >= 60) {
            minutes += 1;
            duration -= 60;
        }
        while (duration > 0) {
            seconds += 1;
            duration -= 1;
        }
        return String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
    }

    @SuppressWarnings("UnstableApiUsage")
    public JsonObject saveItem() {
        JsonObject item = new JsonObject();
        item.addProperty("material", material.name());
        item.addProperty("amount", amount);
        item.addProperty("displayName", displayName);
        item.addProperty("price", price);
        item.addProperty("seller", seller.toString());
        item.addProperty("uuid", uuid.toString());
        item.addProperty("duration", duration);
        item.addProperty("damage", damage);
        item.addProperty("customModelData", customModelData);

        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, Integer>>(){}.getType();

        item.add("enchantments", gson.toJsonTree(convertEnchantments(), type));
        return item;
    }

    public UUID getSeller() {
        return seller;
    }

    private TreeMap<String, Integer> convertEnchantments() {
        TreeMap<String, Integer> readyForJsonMap = new TreeMap<>();
        for (Enchantment enchantment : enchantments.keySet()) {
            readyForJsonMap.put(enchantment.getKey().getKey(), enchantments.get(enchantment));
        }
        return readyForJsonMap;
    }

    @Override
    public String toString() {
        return "Auction: [Seller: " + Bukkit.getOfflinePlayer(seller).getName() + ", Material: " + material.name().toLowerCase()
            + ", Amount: " + amount + ", Price: " + price;
    }
}
