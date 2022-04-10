package me.derechtepilz.economy;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import me.derechtepilz.economy.itemmanager.*;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    private String languages;

    @Override
    public void onEnable() {
        plugin = this;

        if (!getConfig().contains("wasEnabled")) {
            saveDefaultConfig();
        }

        prepareTranslationAccess();
        if (languages.equals("§cNo lang.json file was found!")) {
            getLogger().severe(languages + " Messages may not display correctly! Please contact DerEchtePilz#7406 on the discord server found in the README.md on this plugin's Github page!");
        }

        String version = Bukkit.getBukkitVersion().split("-")[0];
        if (!version.equals("1.18.1")) {
            getLogger().severe(TranslatableChatComponent.read("main.onEnable.version_info").replace("%s", Bukkit.getBukkitVersion().split("-")[0]));
        }

        commandRegistration();
        listenerRegistration();

        getLogger().info(TranslatableChatComponent.read("main.onEnable.plugin_enable_message"));
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
        CommandAPI.unregister("buy");

        getLogger().info(TranslatableChatComponent.read("main.onDisable.plugin_disable_message"));
    }

    public static Main getInstance() {
        return plugin;
    }

    private void commandRegistration() {
        new ItemCreateOffer();
        new ItemCancelOffer();
        new ItemBuyOffer();
    }

    private void listenerRegistration() {
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(itemCancelMenu, this);
        manager.registerEvents(itemBuyMenu, this);
    }

    public NamespacedKey getCreator() {
        return creator;
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

    public ItemBuyMenu getItemBuyMenu() {
        return itemBuyMenu;
    }

    public String getLanguages() {
        return languages;
    }

    public int findNextMultiple(int input, int multipleToFind) {
        if (input > multipleToFind) {
            if (input % multipleToFind == 0) {
                return input;
            }
            int multiple = input;
            while (multiple % multipleToFind != 0) {
                multiple++;
            }
            return multiple;
        } else {
            return multipleToFind;
        }
    }

    private void prepareTranslationAccess() {
        String line;
        try {
            InputStream inputStream = Main.getInstance().getResource("lang.json");
            if (inputStream == null) {
                languages = "§cNo lang.json file was found!";
                return;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder builder = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            languages = builder.toString();
        } catch (IOException exception) {
            languages = "§cNo lang.json file was found!";
        }
    }
}
