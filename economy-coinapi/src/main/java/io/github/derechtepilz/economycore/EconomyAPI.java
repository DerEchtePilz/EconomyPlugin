package io.github.derechtepilz.economycore;

import io.github.derechtepilz.database.DatabaseQueryBuilder;
import io.github.derechtepilz.database.Database;
import io.github.derechtepilz.economycore.exceptions.BalanceException;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.sql.SQLException;
import java.util.logging.Logger;

public class EconomyAPI {

    // Cannot be instantiated
    private EconomyAPI() {
    }

    // Stores API-wide fields
    private static Logger LOGGER;
    private static @NotNull BukkitTask DATABASE_AUTOSAVE_TASK;
    static Bank BANK = new Bank();
    static Plugin PLUGIN;
    static Database DATABASE;

    // Stores coin-related NamespacedKeys
    static NamespacedKey LAST_INTEREST;
    static NamespacedKey PLAYER_BALANCE;
    static NamespacedKey PLAYER_START_BALANCE;

    /**
     * This enables Economy's features.
     * If you do not call this method, the API will not work and throws an error
     *
     * @param plugin Defines the plugin
     */
    public static Database onEnable(Plugin plugin) {
        EconomyAPI.PLUGIN = plugin;
        LAST_INTEREST = new NamespacedKey(EconomyAPI.PLUGIN, "lastInterest");
        PLAYER_BALANCE = new NamespacedKey(EconomyAPI.PLUGIN, "balance");
        PLAYER_START_BALANCE = new NamespacedKey(EconomyAPI.PLUGIN, "startBalance");
        DATABASE = new Database(plugin);

        final Listener playerJoinListener = new Listener() {
            @EventHandler(priority = EventPriority.LOWEST)
            public void onJoin(PlayerJoinEvent event) {
                Player player = event.getPlayer();
                convertOrFetchData(player);
                BANK.fixStartBalance(player);
                BANK.calculateInterest(player);
            }
        };
        runAutoSaveEconomyData();
        Bukkit.getPluginManager().registerEvents(playerJoinListener, plugin);
        return DATABASE;
    }

    /**
     * This loads the EconomyAPI
     */
    public static void onLoad() {
        String serverVersion = Bukkit.getBukkitVersion().split("-")[0];
        if (!EconomyAPIVersionHandler.isVersionSupported(serverVersion)) {
            getLogger().warning("Your server runs on version " + serverVersion + "! The API should work on most to all versions of the game but support will only be provided for Minecraft 1.16 or newer!");
        }

        EconomyAPIVersionHandler.checkDependencies();
        loadConfigValues();
    }

    /**
     * This shuts down the API and saves config values and player data
     * <p>
     * While this can be called everywhere at any time, you should only call this in your plugin's onDisable() method
     */
    public static void onDisable() {
        DATABASE_AUTOSAVE_TASK.cancel();
        try {
            saveConfigValues();
            DATABASE.saveEconomyData();
            DATABASE.getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static Logger getLogger() {
        if (LOGGER == null) {
            LOGGER = new EconomyAPILogger();
        }
        return LOGGER;
    }

    // All the ConfigHandler stuff

    /**
     * This is the API entrypoint for configuring the start balance
     *
     * @param amount The amount of coins a player should get when he first joins the server
     */
    public static void setStartBalance(double amount) {
        ConfigHandler.setStartBalance(amount);
    }

    /**
     * This is the API entrypoint for configuring the interest rate
     * <p>
     * Interest is calculated by a formula: {@code playerBalance * (1 + interest / 100)}
     * <p>
     * As you can see, the interest is a percentage value and should therefore never be less than zero
     *
     * @param rate The percentage of interest a player should get every 24 hours
     */
    public static void setInterestRate(double rate) {
        ConfigHandler.setInterest(rate);
    }

    /**
     * This is the API entrypoint for configuring the minimum days between interest
     *
     * @param minimumDaysForInterest the amount of days to pass before a player gets their next interest
     */
    public static void setMinimumDaysForInterest(int minimumDaysForInterest) {
        ConfigHandler.setMinimumDaysForInterest(minimumDaysForInterest);
    }

    public static void resetConfigValues() {
        ConfigHandler.resetConfigValues();
        saveConfigValues();
        loadConfigValues();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void saveConfigValues() {
        try {
            File dir = new File("./plugins/EconomyAPI");
            if (!dir.exists()) {
                dir.mkdir();
            }
            File file = new File(dir, "config");
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file));
            fileWriter.write(ConfigHandler.getStringValue());
            fileWriter.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private static void loadConfigValues() {
        File file = new File("./plugins/EconomyAPI/config");
        try {
            if (file.exists()) {
                BufferedReader fileReader = new BufferedReader(new FileReader(file));
                String line;
                StringBuilder builder = new StringBuilder();
                while ((line = fileReader.readLine()) != null) {
                    builder.append(line);
                }
                ConfigHandler.loadValues(builder.toString());
            }
        } catch (IOException exception) {
            getLogger().severe("Could not load API-specific values! Using default values until new values are provided.");
            exception.printStackTrace();
        }
    }


    // All the Bank system stuff

    /**
     * This is the API entrypoint for setting a balance
     *
     * @param player The player who owns the account
     * @param amount The amount of coins to set on the player's account
     * @return a boolean whether it was successful
     */
    public static boolean setBalance(Player player, double amount) throws BalanceException {
        BANK.setBalance(player, amount);
        return true;
    }

    /**
     * This is the API entrypoint for adding coins to a player's account
     *
     * @param player The player who owns the account
     * @param amount The amount of coins to add to the player's account
     * @return a boolean whether it was successful
     */
    public static boolean addCoinsToBalance(Player player, double amount) throws BalanceException {
        BANK.addBalance(player, amount);
        return true;
    }

    /**
     * This is the API entrypoint for removing coins from a player's bank account
     *
     * @param player The player who owns the account
     * @param amount The amount of coins to remove from the player's bank account
     * @return a boolean whether it was successful
     */
    public static boolean removeCoinsFromBalance(Player player, double amount) throws BalanceException {
        BANK.removeBalance(player, amount);
        return true;
    }

    /**
     * This is the API entrypoint for getting a player's balance
     *
     * @param player The player who owns the account
     * @return the player's balance
     */
    public static double getBalance(Player player) {
        return DATABASE.getBalance(player.getUniqueId());
    }

    // Convenience methods to get namespaced keys
    public static NamespacedKey getPlayerBalance() {
        return PLAYER_BALANCE;
    }

    @SuppressWarnings("ConstantConditions")
    private static void convertOrFetchData(Player player) {
        if (player.getPersistentDataContainer().has(LAST_INTEREST, PersistentDataType.LONG)) {
            long lastInterest = player.getPersistentDataContainer().get(LAST_INTEREST, PersistentDataType.LONG);
            double startBalance = player.getPersistentDataContainer().get(PLAYER_START_BALANCE, PersistentDataType.DOUBLE);
            double balance = player.getPersistentDataContainer().get(PLAYER_BALANCE, PersistentDataType.DOUBLE);

            new DatabaseQueryBuilder(DATABASE).registerPlayer(player.getUniqueId(), balance, lastInterest, startBalance);

            player.getPersistentDataContainer().remove(PLAYER_BALANCE);
            player.getPersistentDataContainer().remove(LAST_INTEREST);
            player.getPersistentDataContainer().remove(PLAYER_START_BALANCE);
        } else {
            if (!DATABASE.isPlayerAccountRegistered(player.getUniqueId())) {
                DATABASE.registerPlayer(player.getUniqueId(), 0.0, System.currentTimeMillis(), ConfigHandler.getStartBalance());
            }
        }
    }

    private static void runAutoSaveEconomyData() {
        DATABASE_AUTOSAVE_TASK = Bukkit.getScheduler().runTaskTimerAsynchronously(PLUGIN, () -> DATABASE.saveEconomyData(), 0, 12000);
    }
}
