package me.derechtepilz.economy;

import com.google.gson.*;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import dev.jorel.commandapi.RegisteredCommand;
import me.derechtepilz.economy.inventorymanagement.InventoryHandler;
import me.derechtepilz.economy.inventorymanagement.ItemUpdater;
import me.derechtepilz.economy.itemmanagement.Item;
import me.derechtepilz.economycore.EconomyAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class Main extends JavaPlugin {

    private boolean isVersionSupported;
    private final Main main = this;

    // Store item-related fields
    private final List<UUID> registeredItemUuids = new ArrayList<>();
    private final HashMap<UUID, Item> registeredItems = new HashMap<>();
    private final HashMap<UUID, ItemStack> expiredItems = new HashMap<>();

    // Initialize command classes
    private final EconomyCommand economyCommand = new EconomyCommand(main);

    // Initialize inventory management classes
    private final ItemUpdater itemUpdater = new ItemUpdater(main);
    private final InventoryHandler inventoryHandler = new InventoryHandler(main);

    @Override
    public void onEnable() {
        EconomyAPI.onEnable(main);

        if (isVersionSupported) {
            CommandAPI.onEnable(main);
            commandRegistration();
        }

        listenerRegistration();
        inventoryHandler.updateOffersAndInventory();
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

        EconomyAPI.onDisable();
        CommandAPI.onDisable();
        for (RegisteredCommand registeredCommand : CommandAPI.getRegisteredCommands()) {
            CommandAPI.unregister(registeredCommand.commandName());
        }
    }

    private void commandRegistration() {
        economyCommand.register();
    }

    private void listenerRegistration() {

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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void loadItems() {
        try {
            File file = new File("./plugins/Economy");
            if (!file.exists()) {
                file.mkdir();
            }
            File items = new File(file, "items");
            BufferedReader reader = new BufferedReader(new FileReader(items));

            String line;
            StringBuilder buildSavedItems = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                buildSavedItems.append(line);
            }

            JsonArray itemsArray = JsonParser.parseString(buildSavedItems.toString()).getAsJsonArray();
            for (int i = 0; i < itemsArray.size(); i++) {
                JsonObject itemObject = itemsArray.get(i).getAsJsonObject();
                Material material = Material.getMaterial(itemObject.get("material").getAsString());
                int amount = itemObject.get("amount").getAsInt();
                double price = itemObject.get("price").getAsDouble();
                UUID seller = UUID.fromString(itemObject.get("seller").getAsString());
                UUID uuid = UUID.fromString(itemObject.get("uuid").getAsString());
                int duration = itemObject.get("duration").getAsInt();

                Item item = new Item(main, material, amount, price, seller, uuid, duration);
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
            File items = new File(file, "items");
            FileWriter writer = new FileWriter(items);

            JsonArray itemsArray = new JsonArray();
            for (UUID uuid : getRegisteredItems().keySet()) {
                itemsArray.add(getRegisteredItems().get(uuid).saveItem());
            }

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(itemsArray));
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
}
