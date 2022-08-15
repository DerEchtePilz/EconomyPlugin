package me.derechtepilz.economycore;

import me.derechtepilz.economycore.exceptions.BalanceException;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

public class EconomyAPI {

    // Cannot be instantiated
    private EconomyAPI() {
    }

    // Stores API-wide fields
    private static Logger LOGGER;
    static Plugin plugin;

    // Stores coin-related NamespacedKeys
    static NamespacedKey lastInterestKey;
    static NamespacedKey playerBalance;
    static NamespacedKey playerStartBalance;

    // Stores item-related NamespacedKeys
    static NamespacedKey itemSeller;
    static NamespacedKey itemPrice;
    static NamespacedKey itemUuid;

    /**
     * This enables Economy's features.
     * If you do not call this method, the API will not work and throws an error
     *
     * @param plugin Defines the plugin
     */
    public static void onEnable(Plugin plugin) {
        EconomyAPI.plugin = plugin;
        lastInterestKey = new NamespacedKey(EconomyAPI.plugin, "lastInterest");
        playerBalance = new NamespacedKey(EconomyAPI.plugin, "balance");
        playerStartBalance = new NamespacedKey(EconomyAPI.plugin, "startBalance");

        itemSeller = new NamespacedKey(EconomyAPI.plugin, "itemSeller");
        itemPrice = new NamespacedKey(EconomyAPI.plugin, "itemPrice");
        itemUuid = new NamespacedKey(EconomyAPI.plugin, "itemUuid");

        final Listener playerJoinListener = new Listener() {
            @EventHandler
            public void onJoin(PlayerJoinEvent event) {
                Player player = event.getPlayer();
                if (player.getPersistentDataContainer().has(playerBalance, PersistentDataType.DOUBLE)) {
                    loadBankAccount(player);
                } else {
                    setBankAccount(player);
                }
                Bank bank = BANK_ACCOUNTS.get(player.getUniqueId());
                bank.calculateInterest();
                getLogger().info(bank.toString());
            }
        };
        Bukkit.getPluginManager().registerEvents(playerJoinListener, plugin);
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

    /**
     * This saves API-specific values into a config file
     * <p>
     * While this can be called everywhere at any time, you should only call this in your plugin's onDisable() method
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void onDisable() {
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
            getLogger().severe("Couldn't disable the EconomyAPI because of an IOException!");
            exception.printStackTrace();
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
     * Interest is calculated by a formular: {@code playerBalance * (1 + interest / 100)}
     * <p>
     * As you can see, the interest is a percentage value and should therefore never be less than zero
     *
     * @param rate The percentage of interest a player should get every 24 hours
     */
    public static void setInterest(double rate) {
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


    // All the Bank system stuff

    // Stores the bank accounts
    private static final HashMap<UUID, Bank> BANK_ACCOUNTS = new HashMap<>();

    /**
     * This is used to create a new bank account for a player
     * <p>
     * Using this twice on the same player resets their account
     * <p>
     * Under normal circumstances this method does not need to be called! Registering a new bank account is done automatically when a player joins the server
     *
     * @param player The player who owns the account
     */
    public static void setBankAccount(Player player) {
        Bank bank = new Bank(player);
        BANK_ACCOUNTS.put(player.getUniqueId(), bank);
    }

    /**
     * This is the API entrypoint for setting a balance
     *
     * @param player The player who owns the account
     * @param amount The amount of coins to set on the player's account
     * @return a boolean whether it was successful
     */
    public static boolean setBalance(Player player, double amount) throws BalanceException {
        if (!BANK_ACCOUNTS.containsKey(player.getUniqueId())) {
            LOGGER.severe("Couldn't set a balance for player '" + player.getName() + "' because they do not have an account!");
            return false;
        }
        Bank bank = BANK_ACCOUNTS.get(player.getUniqueId());
        bank.setBalance(amount);
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
        if (!BANK_ACCOUNTS.containsKey(player.getUniqueId())) {
            LOGGER.severe("Couldn't add coins to the balance of player '" + player.getName() + "' because they do not have an account!");
            return false;
        }
        Bank bank = BANK_ACCOUNTS.get(player.getUniqueId());
        bank.addBalance(amount);
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
        if (!BANK_ACCOUNTS.containsKey(player.getUniqueId())) {
            LOGGER.severe("Couldn't remove coins from the balance of player '" + player.getName() + "' because they do not have an account!");
            return false;
        }
        Bank bank = BANK_ACCOUNTS.get(player.getUniqueId());
        bank.removeBalance(amount);
        return true;
    }

    /**
     * This is a private method used in {@link EconomyAPI#onEnable(Plugin)} to load a player's bank account
     *
     * @param player The player who owns the account
     */
    @SuppressWarnings("ConstantConditions")
    private static void loadBankAccount(Player player) {
        double balance = player.getPersistentDataContainer().get(playerBalance, PersistentDataType.DOUBLE);
        double startBalance = player.getPersistentDataContainer().get(playerStartBalance, PersistentDataType.DOUBLE);
        long lastInterest = player.getPersistentDataContainer().get(lastInterestKey, PersistentDataType.LONG);
        Bank bank = new Bank(player, balance, startBalance, lastInterest);
        BANK_ACCOUNTS.put(player.getUniqueId(), bank);
    }
}
