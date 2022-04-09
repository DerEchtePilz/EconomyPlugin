package me.derechtepilz.economy;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import me.derechtepilz.economy.itemmanager.ItemBuyMenu;
import me.derechtepilz.economy.itemmanager.ItemCancelMenu;
import me.derechtepilz.economy.itemmanager.ItemCancelOffer;
import me.derechtepilz.economy.itemmanager.ItemCreateOffer;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class Main extends JavaPlugin {
    private static Main plugin;

    private final NamespacedKey creator = new NamespacedKey(Main.getInstance(), "itemSeller");
    private final NamespacedKey uuid = new NamespacedKey(Main.getInstance(), "id");
    private final NamespacedKey price = new NamespacedKey(Main.getInstance(), "price");

    private final HashMap<UUID, ItemStack[]> offeringPlayers = new HashMap<>();
    private final HashMap<String, ItemStack[]> specialOffers = new HashMap<>();

    private final List<ItemStack> offeredItemsList = new ArrayList<>();

    private final ItemCancelMenu itemCancelMenu = new ItemCancelMenu();
    private final ItemBuyMenu itemBuyMenu = new ItemBuyMenu();

    @Override
    public void onEnable() {
        plugin = this;

        if (!getConfig().contains("wasEnabled")) {
            saveDefaultConfig();
        }

        commandRegistration();
        listenerRegistration();
    }

    @Override
    public void onLoad() {
        reloadConfig();
        CommandAPI.onLoad(new CommandAPIConfig());
    }

    @Override
    public void onDisable() {
        getConfig().set("wasEnabled", true);
        saveConfig();

        CommandAPI.unregister("createoffer");
        CommandAPI.unregister("canceloffer");
    }

    public static Main getInstance() {
        return plugin;
    }

    private void commandRegistration() {
        new ItemCreateOffer();
        new ItemCancelOffer();
    }

    private void listenerRegistration() {
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(itemCancelMenu, this);
        manager.registerEvents(itemBuyMenu, this);
    }

    public NamespacedKey getCreator() {
        return creator;
    }

    public NamespacedKey getUuid() {
        return uuid;
    }

    public NamespacedKey getPrice() {
        return price;
    }

    public HashMap<UUID, ItemStack[]> getOfferingPlayers() {
        return offeringPlayers;
    }

    public HashMap<String, ItemStack[]> getSpecialOffers() {
        return specialOffers;
    }

    public List<ItemStack> getOfferedItemsList() {
        return offeredItemsList;
    }

    public ItemCancelMenu getItemCancelMenu() {
        return itemCancelMenu;
    }
}
