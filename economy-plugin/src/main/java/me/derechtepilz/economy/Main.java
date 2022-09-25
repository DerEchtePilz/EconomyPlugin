package me.derechtepilz.economy;

import com.google.gson.*;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import me.derechtepilz.economy.coinmanagement.CoinDisplay;
import me.derechtepilz.economy.coinmanagement.JoinCoinManagement;
import me.derechtepilz.economy.commands.ConsoleCommands;
import me.derechtepilz.economy.commands.EconomyCommand;
import me.derechtepilz.economy.inventorymanagement.InventoryHandler;
import me.derechtepilz.economy.inventorymanagement.ItemUpdater;
import me.derechtepilz.economy.itemmanagement.Item;
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
import org.bukkit.Material;
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
import static org.fusesource.jansi.AnsiColors.*;

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

    // Update stuff
    private boolean shouldRegisterUpdateInformation = false;
    private boolean isNewUpdateAvailable = false;
    private final UpdateDownload updateDownload = new UpdateDownload(main);
    private final UpdateChecker updateChecker = new UpdateChecker(main);

    @Override
    public void onEnable() {
        if (!updatedPluginName.equals("")) {
            updateDownload.enablePlugin(updatedPluginName);
        }
        if (updateChecker.isLatestVersion() && updateChecker.isPreviousVersionPresent()) {
            updateDownload.disableAndDeleteOutdatedPlugin();
        }

        if (!isNewUpdateAvailable) {
            EconomyAPI.onEnable(main);

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
            loadItems();

            String version = Bukkit.getBukkitVersion().split("-")[0];
            isVersionSupported = VersionHandler.isVersionSupported(version);

            if (isVersionSupported) {
                CommandAPI.onLoad(new CommandAPIConfig().missingExecutorImplementationMessage("You cannot execute this command!"));
            }
        }
    }

    @Override
    public void onDisable() {
        if (!isNewUpdateAvailable) {
            saveItems();

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

    @SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
    private void loadItems() {
        try {
            File file = new File("./plugins/Economy");
            if (!file.exists()) {
                file.mkdir();
            }
            File items = new File(file, "items.json");
            BufferedReader reader = new BufferedReader(new FileReader(items));

            String line;
            StringBuilder buildSavedItems = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                buildSavedItems.append(line);
            }

            JsonObject savedItems = JsonParser.parseString(buildSavedItems.toString()).getAsJsonObject();

            boolean auctionRunning = savedItems.get("auctionRunning").getAsBoolean();
            inventoryHandler.setTimerRunning(auctionRunning);

            JsonArray runningAuctions = savedItems.get("runningAuctions").getAsJsonArray();
            JsonArray expiredAuctions = savedItems.get("expiredAuctions").getAsJsonArray();

            for (int i = 0; i < runningAuctions.size(); i++) {
                JsonObject itemObject = runningAuctions.get(i).getAsJsonObject();
                Material material = Material.getMaterial(itemObject.get("material").getAsString());
                int amount = itemObject.get("amount").getAsInt();
                double price = itemObject.get("price").getAsDouble();
                UUID seller = UUID.fromString(itemObject.get("seller").getAsString());
                UUID uuid = UUID.fromString(itemObject.get("uuid").getAsString());
                int duration = itemObject.get("duration").getAsInt();

                Item item = new Item(main, material, amount, price, seller, uuid, duration);
                item.register();
                getLogger().info("Registered auction: " + item);
            }

            for (int i = 0; i < expiredAuctions.size(); i++) {
                JsonObject itemObject = expiredAuctions.get(i).getAsJsonObject();
                Material material = Material.matchMaterial(itemObject.get("material").getAsString());
                int amount = itemObject.get("amount").getAsInt();
                UUID seller = UUID.fromString(itemObject.get("seller").getAsString());

                List<ItemStack> expiredItems = (getExpiredItems().containsKey(seller)) ? getExpiredItems().get(seller) : new ArrayList<>();
                expiredItems.add(new ItemStack(material, amount));
                getExpiredItems().put(seller, expiredItems);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    private void saveItems() {
        try {
            File file = new File("./plugins/Economy");
            if (!file.exists()) {
                file.mkdir();
            }
            File items = new File(file, "items.json");
            FileWriter writer = new FileWriter(items);

            JsonObject auctionsObject = new JsonObject();
            auctionsObject.addProperty("auctionRunning", inventoryHandler.isTimerRunning());

            JsonArray runningAuctionsArray = new JsonArray();
            for (UUID uuid : getRegisteredItems().keySet()) {
                runningAuctionsArray.add(getRegisteredItems().get(uuid).saveItem());
            }
            auctionsObject.add("runningAuctions", runningAuctionsArray);

            JsonArray expiredAuctionsArray = new JsonArray();
            for (UUID uuid : getExpiredItems().keySet()) {
                for (ItemStack itemStack : getExpiredItems().get(uuid)) {
                    JsonObject expiredItem = new JsonObject();
                    expiredItem.addProperty("material", itemStack.getType().name());
                    expiredItem.addProperty("amount", itemStack.getAmount());
                    expiredItem.addProperty("seller", String.valueOf(uuid));
                    expiredAuctionsArray.add(expiredItem);
                }
            }
            auctionsObject.add("expiredAuctions", expiredAuctionsArray);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(auctionsObject));
            writer.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
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
}
