package me.derechtepilz.economy;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import me.derechtepilz.database.Database;
import me.derechtepilz.economy.coinmanagement.CoinDisplay;
import me.derechtepilz.economy.coinmanagement.JoinCoinManagement;
import me.derechtepilz.economy.commands.ConsoleCommands;
import me.derechtepilz.economy.commands.EconomyCommand;
import me.derechtepilz.economy.friendmanagement.FriendRequest;
import me.derechtepilz.economy.friendmanagement.LoadFriends;
import me.derechtepilz.economy.friendmanagement.SaveFriends;
import me.derechtepilz.economy.friendmanagement.Friend;
import me.derechtepilz.economy.inventorymanagement.InventoryHandler;
import me.derechtepilz.economy.inventorymanagement.ItemUpdater;
import me.derechtepilz.economy.itemmanagement.Item;
import me.derechtepilz.economy.itemmanagement.LoadItem;
import me.derechtepilz.economy.itemmanagement.SaveItem;
import me.derechtepilz.economy.offers.BuyOfferMenuListener;
import me.derechtepilz.economy.offers.CancelOfferMenu;
import me.derechtepilz.economy.offers.CancelOfferMenuListener;
import me.derechtepilz.economy.offers.ExpiredOfferMenu;
import me.derechtepilz.economy.permissionmanagement.Permission;
import me.derechtepilz.economy.updatemanagement.UpdateChecker;
import me.derechtepilz.economy.updatemanagement.UpdateDownload;
import me.derechtepilz.economy.updatemanagement.UpdateInformation;
import me.derechtepilz.economy.utility.Config;
import me.derechtepilz.economycore.EconomyAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import static org.fusesource.jansi.Ansi.*;

public final class Main extends JavaPlugin {

    private boolean isVersionSupported;
    private final Main main = this;
    private String updatedPluginName = "";

    private int inventoryManagementTaskId;
    private int coinDisplayTaskId;

    private Logger logger;

    @NotNull
    public Logger getLogger() {
        if (logger == null) {
            logger = new EconomyPluginLogger();
        }
        return logger;
    }

    // Store item-related fields
    private final List<UUID> registeredItemUuids = new ArrayList<>();
    private final List<UUID> offeringPlayerUuids = new ArrayList<>();
    private final HashMap<UUID, Item> registeredItems = new HashMap<>();
    private final HashMap<UUID, List<ItemStack>> expiredItems = new HashMap<>();

    // Initialize command classes
    private final EconomyCommand economyCommand = new EconomyCommand(main);
    private final ConsoleCommands consoleCommands = new ConsoleCommands(main);

    // Initialize inventory management classes
    private final ItemUpdater itemUpdater = new ItemUpdater(main);
    private final InventoryHandler inventoryHandler = new InventoryHandler(main);
    private final CancelOfferMenu cancelOfferMenu = new CancelOfferMenu(main);
    private final ExpiredOfferMenu expiredOfferMenu = new ExpiredOfferMenu(main);

    // Initialize coin management fields
    private final HashMap<UUID, Double> earnedCoins = new HashMap<>();

    // Initialize coin management classes
    private final CoinDisplay coinDisplay = new CoinDisplay(main);

    // Utility classes
    private final Config config = new Config(main);
    private final SaveItem saveItem = new SaveItem(main);
    private final LoadItem loadItem = new LoadItem(main);

    // Update stuff
    private boolean shouldRegisterUpdateInformation = false;
    private boolean isNewUpdateAvailable = false;
    private final UpdateDownload updateDownload = new UpdateDownload(main);
    private final UpdateChecker updateChecker = new UpdateChecker(main);

    // Database stuff (if I need it)
    private Database database;

    // Friend system stuff
    private final Friend friend = new Friend();
    private final FriendRequest friendRequest = new FriendRequest();
    private final SaveFriends saveFriends = new SaveFriends(main);
    private final LoadFriends loadFriends = new LoadFriends(main);

    @Override
    public void onEnable() {
        if (!updatedPluginName.equals("")) {
            updateDownload.enablePlugin(updatedPluginName);
        }
        if (updateChecker.isLatestVersion() && updateChecker.isPreviousVersionPresent()) {
            updateDownload.disableAndDeleteOutdatedPlugin();
        }

        if (!isNewUpdateAvailable) {
            database = EconomyAPI.onEnable(main);
            loadFriends.loadFriends();

            if (isVersionSupported) {
                CommandAPI.onEnable(main);
                commandRegistration();
            }

            listenerRegistration();
            inventoryManagementTaskId = inventoryHandler.updateOffersAndInventory();
            coinDisplayTaskId = coinDisplay.displayCoins();

            getLogger().info(ansi().fgGreen().a("You are on version ").fgYellow().a("v" + getDescription().getVersion()).toString());
        }
    }

    @Override
    public void onLoad() {
        try {
            config.loadConfig();
        } catch (FileNotFoundException e) {
            getLogger().info(ansi().fgRed().a("The config is not present! Please report this!").toString());
        }

        isNewUpdateAvailable = new UpdateChecker(main).isUpdateAvailable();
        boolean isDirectDownload = Boolean.parseBoolean(config.get("allowDirectDownloads"));
        if (isNewUpdateAvailable && !isDirectDownload) {
            getLogger().info(ansi().fgYellow().a("There is a new update available for the EconomyPlugin! Please download the latest version at https:/github.com/DerEchtePilz/EconomyPlugin/releases/latest").toString());
            shouldRegisterUpdateInformation = true;
        }
        if (isNewUpdateAvailable && isDirectDownload) {
            getLogger().info(ansi().fgYellow().a("A new update is available for the EconomyPlugin! Downloading now...").toString());
            updatedPluginName = updateDownload.downloadUpdate();
        }
        if (!isNewUpdateAvailable) {
            EconomyAPI.onLoad();
            loadItem.loadItems();

            String version = Bukkit.getBukkitVersion().split("-")[0];
            isVersionSupported = VersionHandler.isVersionSupported(version);

            if (isVersionSupported) {
                getLogger().info("Calling CommandAPI.onLoad()");
                CommandAPI.onLoad(new CommandAPIConfig().missingExecutorImplementationMessage("You cannot execute this command!"));
            }
        }
    }

    @Override
    public void onDisable() {
        if (!isNewUpdateAvailable) {
            saveItem.saveItems();
            saveFriends.saveFriends();

            Bukkit.getScheduler().cancelTask(inventoryManagementTaskId);
            Bukkit.getScheduler().cancelTask(coinDisplayTaskId);

            EconomyAPI.onDisable();
            CommandAPI.onDisable();
        }
    }

    private void commandRegistration() {
        economyCommand.register();
        consoleCommands.register();
    }

    private void listenerRegistration() {
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new BuyOfferMenuListener(main), this);
        manager.registerEvents(new CancelOfferMenuListener(main), this);
        manager.registerEvents(new JoinCoinManagement(main), this);
        manager.registerEvents(expiredOfferMenu, this);

        if (shouldRegisterUpdateInformation) {
            manager.registerEvents(new UpdateInformation(main), this);
            shouldRegisterUpdateInformation = false;
        }

        manager.registerEvents(new Listener() {
            @EventHandler
            public void onJoin(PlayerJoinEvent event) {
                Permission.updatePermissions(event.getPlayer());
            }
        }, this);
    }

    // Store item-related methods
    public List<UUID> getRegisteredItemUuids() {
        return registeredItemUuids;
    }

    public List<UUID> getOfferingPlayerUuids() {
        return offeringPlayerUuids;
    }

    public HashMap<UUID, Item> getRegisteredItems() {
        return registeredItems;
    }

    public HashMap<UUID, List<ItemStack>> getExpiredItems() {
        return expiredItems;
    }

    // Store inventory-management-related methods
    public ItemUpdater getItemUpdater() {
        return itemUpdater;
    }

    public InventoryHandler getInventoryHandler() {
        return inventoryHandler;
    }

    public CancelOfferMenu getCancelOfferMenu() {
        return cancelOfferMenu;
    }

    public ExpiredOfferMenu getExpiredOfferMenu() {
        return expiredOfferMenu;
    }

    // Store coin management-related methods
    public HashMap<UUID, Double> getEarnedCoins() {
        return earnedCoins;
    }

    // Store utility methods
    public Config getPluginConfig() {
        return config;
    }

    public Database getDatabase() {
        return database;
    }

    // Store friend system methods
    public Friend getFriend() {
        return friend;
    }

    public FriendRequest getFriendRequest() {
        return friendRequest;
    }
}
