package me.derechtepilz.economy.itemmanagement;

import me.derechtepilz.economy.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Item {

    private final Main main;
    private final Material material;
    private final int amount;
    private final double price;
    private final UUID seller;
    private final UUID uuid;
    private int duration;
    private int taskId;

    private final NamespacedKey itemPrice;
    private final NamespacedKey itemSeller;
    private final NamespacedKey itemUuid;

    public Item(Main main, Material material, int amount, double price, UUID seller, int duration) {
        this.main = main;
        this.material = material;
        this.amount = amount;
        this.price = price;
        this.seller = seller;
        this.duration = duration;
        this.uuid = UUID.randomUUID();

        this.itemPrice = new NamespacedKey(main, "itemPrice");
        this.itemSeller = new NamespacedKey(main, "itemSeller");
        this.itemUuid = new NamespacedKey(main, "itemUuid");

        main.getRegisteredItemUuids().add(uuid);
        runTimer();
    }

    public ItemStack getItemStack() {
        ItemStack itemStack = new ItemStack(material, amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(itemPrice, PersistentDataType.DOUBLE, price);
        itemMeta.getPersistentDataContainer().set(itemSeller, PersistentDataType.STRING, String.valueOf(seller));
        itemMeta.getPersistentDataContainer().set(itemUuid, PersistentDataType.STRING, String.valueOf(uuid));

        List<String> lore = new ArrayList<>();
        lore.add("Seller: " + Bukkit.getOfflinePlayer(seller).getName());
        lore.add("Price: " + price);
        lore.add("Expires in: " + convertSecondsToTime(duration));

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private void runTimer() {
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, () -> {
            duration -= 1;
            if (duration == 0) {
                main.getRegisteredItems().remove(uuid);
                main.getRegisteredItemUuids().remove(uuid);
                Bukkit.getScheduler().cancelTask(taskId);
            }
            main.getRegisteredItems().put(uuid, getItemStack());
        }, 20, 20);
    }

    public void decreaseDuration() {
        if (duration == 0) {
            main.getRegisteredItems().remove(uuid);
            main.getRegisteredItemUuids().remove(uuid);
            return;
        }
        duration -= 1;
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

}
