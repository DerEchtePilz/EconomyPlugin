package me.derechtepilz.economy;

import com.google.gson.*;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import dev.jorel.commandapi.RegisteredCommand;
import me.derechtepilz.economy.coinmanagement.CoinDisplay;
import me.derechtepilz.economy.commands.ConsoleCommands;
import me.derechtepilz.economy.commands.EconomyCommand;
import me.derechtepilz.economy.inventorymanagement.InventoryHandler;
import me.derechtepilz.economy.inventorymanagement.ItemUpdater;
import me.derechtepilz.economy.itemmanagement.Item;
import me.derechtepilz.economy.offers.BuyOfferMenuListener;
import me.derechtepilz.economy.offers.CancelOfferMenu;
import me.derechtepilz.economycore.EconomyAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class Main extends JavaPlugin {

    private boolean isVersionSupported;
    private final Main main = this;

    private int inventoryManagementTaskId;
    private int coinDisplayTaskId;

    // Store item-related fields
    private final List<UUID> registeredItemUuids = new ArrayList<>();
    private final HashMap<UUID, Item> registeredItems = new HashMap<>();
    private final HashMap<UUID, ItemStack> expiredItems = new HashMap<>();

    // Initialize command classes
    private final EconomyCommand economyCommand = new EconomyCommand(main);
    private final ConsoleCommands consoleCommands = new ConsoleCommands(main);

    // Initialize inventory management classes
    private final ItemUpdater itemUpdater = new ItemUpdater(main);
    private final InventoryHandler inventoryHandler = new InventoryHandler(main);
    private final CancelOfferMenu cancelOfferMenu = new CancelOfferMenu(main);

    // Initialize coin management classes
    private final CoinDisplay coinDisplay = new CoinDisplay(main);

    @Override
    public void onEnable() {
        EconomyAPI.onEnable(main);

        if (isVersionSupported) {
            CommandAPI.onEnable(main);
            commandRegistration();
        }

        listenerRegistration();
        inventoryManagementTaskId = inventoryHandler.updateOffersAndInventory();
        coinDisplayTaskId = coinDisplay.displayCoins();
    }

    @Override
    public void onLoad() {
        EconomyAPI.onLoad();
        loadItems();

        String version = Bukkit.getBukkitVersion().split("-")[0];
        isVersionSupported = VersionHandler.isVersionSupported(version);

        if (isVersionSupported) {
            CommandAPI.onLoad(new CommandAPIConfig().missingExecutorImplementationMessage("You cannot execute this command!"));
        }
    }

    @Override
    public void onDisable() {
        saveItems();

        Bukkit.getScheduler().cancelTask(inventoryManagementTaskId);
        Bukkit.getScheduler().cancelTask(coinDisplayTaskId);

        EconomyAPI.onDisable();

        for (RegisteredCommand registeredCommand : CommandAPI.getRegisteredCommands()) {
            CommandAPI.unregister(registeredCommand.commandName());
        }
        CommandAPI.onDisable();
    }

    private void commandRegistration() {
        economyCommand.register();
        consoleCommands.register();
    }

    private void listenerRegistration() {
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new BuyOfferMenuListener(main), this);
        manager.registerEvents(cancelOfferMenu, this);
    }

    // Store item-related methods
    public List<UUID> getRegisteredItemUuids() {
        return registeredItemUuids;
    }

    public HashMap<UUID, Item> getRegisteredItems() {
        return registeredItems;
    }

    public HashMap<UUID, ItemStack> getExpiredItems() {
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

                ItemStack item = new ItemStack(material, amount);
                getExpiredItems().put(seller, item);
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

            JsonArray runningAuctionsArray = new JsonArray();
            for (UUID uuid : getRegisteredItems().keySet()) {
                runningAuctionsArray.add(getRegisteredItems().get(uuid).saveItem());
            }
            auctionsObject.add("runningAuctions", runningAuctionsArray);

            JsonArray expiredAuctionsArray = new JsonArray();
            for (UUID uuid : getExpiredItems().keySet()) {
                JsonObject expiredItem = new JsonObject();
                ItemStack itemStack = getExpiredItems().get(uuid);
                expiredItem.addProperty("material", itemStack.getType().name());
                expiredItem.addProperty("amount", itemStack.getAmount());
                expiredItem.addProperty("seller", String.valueOf(uuid));
                expiredAuctionsArray.add(expiredItem);
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
}
