package me.derechtepilz.economy;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import me.derechtepilz.economy.economymanager.*;
import me.derechtepilz.economy.itemmanager.*;
import me.derechtepilz.economy.playermanager.PermissionCommand;
import me.derechtepilz.economy.utility.Config;
import me.derechtepilz.economy.bukkitcommands.commands.FallbackCommand;
import me.derechtepilz.economy.utility.ItemSaving;
import me.derechtepilz.economy.utility.Language;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class Main extends JavaPlugin {
    private static Main plugin;
    private Language language;

    private final NamespacedKey creator = new NamespacedKey(this, "itemSeller");
    private final NamespacedKey uuid = new NamespacedKey(this, "id");
    private final NamespacedKey price = new NamespacedKey(this, "price");

    private final NamespacedKey balance = new NamespacedKey(this, "balance");
    private final NamespacedKey lastInterest = new NamespacedKey(this, "lastInterest");
    private final NamespacedKey startBalance = new NamespacedKey(this, "startBalance");

    private final NamespacedKey permission = new NamespacedKey(this, "permissions");

    private final HashMap<UUID, ItemStack> offeredItems = new HashMap<>();
    private final HashMap<UUID, ItemStack[]> offeringPlayers = new HashMap<>();
    private final HashMap<String, ItemStack[]> specialOffers = new HashMap<>();

    private final HashMap<Player, BankManager> bankAccounts = new HashMap<>();

    private final List<ItemStack> offeredItemsList = new ArrayList<>();

    private ItemCancelMenu itemCancelMenu;
    private ItemBuyMenu itemBuyMenu;

    private final FallbackCommand fallbackCommand = new FallbackCommand();

    private boolean wasCommandAPILoaded;

    @Override
    public void onEnable() {
        if (getConfig().contains("language")) {
            language = Language.valueOf(getConfig().getString("language"));
        } else {
            language = Language.EN_US;
        }

        itemCancelMenu = new ItemCancelMenu();
        itemBuyMenu = new ItemBuyMenu();

        commandRegistration();
        listenerRegistration();

        getLogger().info(TranslatableChatComponent.read("main.onEnable.plugin_enable_message"));
    }

    @Override
    public void onLoad() {
        plugin = this;
        String version = Bukkit.getBukkitVersion().split("-")[0];
        if (VersionHandler.isVersionSupported(version)) {
            CommandAPI.onLoad(new CommandAPIConfig());
            wasCommandAPILoaded = true;
        } else {
            getLogger().severe(TranslatableChatComponent.read("main.onLoad.version_info").replace("%s", Bukkit.getBukkitVersion().split("-")[0]));
            wasCommandAPILoaded = false;
        }

        ItemSaving.load();
        Config.loadConfig();
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {

        if (wasCommandAPILoaded) {
            CommandAPI.unregister("createoffer");
            CommandAPI.unregister("canceloffer");
            CommandAPI.unregister("buy");
            CommandAPI.unregister("givecoins");
            CommandAPI.unregister("takecoins");
            CommandAPI.unregister("setcoins");
            CommandAPI.unregister("permission");
        }

        ItemSaving.save();

        getLogger().info(ChatColor.translateAlternateColorCodes('&', TranslatableChatComponent.read("main.onDisable.plugin_disable_message")));
    }

    public static Main getInstance() {
        return plugin;
    }

    private void commandRegistration() {
        if (wasCommandAPILoaded) {
            new ItemCreateOffer();
            new ItemCancelOffer();
            new ItemBuyOffer();
            new GiveCoinsCommand();
            new TakeCoinsCommand();
            new SetCoinsCommand();
            new PermissionCommand();
        }
        getCommand("fallback").setExecutor(fallbackCommand);
        getCommand("fallback").setTabCompleter(fallbackCommand);
    }

    private void listenerRegistration() {
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(itemCancelMenu, this);
        manager.registerEvents(itemBuyMenu, this);
        manager.registerEvents(new ManageCoinsWhenJoining(), this);
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

    public NamespacedKey getBalance() {
        return balance;
    }

    public NamespacedKey getLastInterest() {
        return lastInterest;
    }

    public NamespacedKey getStartBalance() {
        return startBalance;
    }

    public NamespacedKey getPermission() {
        return permission;
    }

    public HashMap<UUID, ItemStack> getOfferedItems() {
        return offeredItems;
    }

    public HashMap<UUID, ItemStack[]> getOfferingPlayers() {
        return offeringPlayers;
    }

    public HashMap<String, ItemStack[]> getSpecialOffers() {
        return specialOffers;
    }

    public HashMap<Player, BankManager> getBankAccounts() {
        return bankAccounts;
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

    public Language getLanguage() {
        return language;
    }

    public boolean isWasCommandAPILoaded() {
        return wasCommandAPILoaded;
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
}