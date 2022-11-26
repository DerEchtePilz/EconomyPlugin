package io.github.derechtepilz.economy;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import io.github.derechtepilz.database.Database;
import io.github.derechtepilz.economy.coinmanagement.CoinDisplay;
import io.github.derechtepilz.economy.coinmanagement.JoinCoinManagement;
import io.github.derechtepilz.economy.commands.ConsoleCommands;
import io.github.derechtepilz.economy.commands.EconomyCommand;
import io.github.derechtepilz.economy.friendmanagement.Friend;
import io.github.derechtepilz.economy.friendmanagement.FriendRequest;
import io.github.derechtepilz.economy.friendmanagement.LoadFriends;
import io.github.derechtepilz.economy.friendmanagement.SaveFriends;
import io.github.derechtepilz.economy.inventorymanagement.InventoryHandler;
import io.github.derechtepilz.economy.inventorymanagement.ItemUpdater;
import io.github.derechtepilz.economy.itemmanagement.Item;
import io.github.derechtepilz.economy.itemmanagement.LoadItem;
import io.github.derechtepilz.economy.itemmanagement.SaveItem;
import io.github.derechtepilz.economy.offers.BuyOfferMenuListener;
import io.github.derechtepilz.economy.offers.CancelOfferMenuListener;
import io.github.derechtepilz.economy.offers.ExpiredOfferMenu;
import io.github.derechtepilz.economy.permissionmanagement.Permission;
import io.github.derechtepilz.economy.permissionmanagement.PermissionGroup;
import io.github.derechtepilz.economy.tests.TestsCommand;
import io.github.derechtepilz.economy.tests.inventory.InventoryTest;
import io.github.derechtepilz.economy.tradesystem.Trade;
import io.github.derechtepilz.economy.updatemanagement.UpdateChecker;
import io.github.derechtepilz.economy.updatemanagement.UpdateDownload;
import io.github.derechtepilz.economy.updatemanagement.UpdateInformation;
import io.github.derechtepilz.economy.utility.Config;
import io.github.derechtepilz.economycore.EconomyAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import static org.fusesource.jansi.Ansi.ansi;

public final class Main extends JavaPlugin {

	private final boolean isDevelopment = true;

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

	// Tests
	private InventoryTest inventoryTest;

	// Store item-related fields
	private final List<UUID> registeredItemUuids = new ArrayList<>();
	private final List<UUID> offeringPlayerUuids = new ArrayList<>();
	private final HashMap<UUID, Item> registeredItems = new HashMap<>();
	private final HashMap<UUID, List<ItemStack>> expiredItems = new HashMap<>();

	// Initialize command classes
	private EconomyCommand economyCommand;
	private final ConsoleCommands consoleCommands = new ConsoleCommands(main);

	// Initialize inventory management classes
	private final ItemUpdater itemUpdater = new ItemUpdater(main);
	private final InventoryHandler inventoryHandler = new InventoryHandler(main);
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

	// Trade system stuff
	private final HashMap<UUID, Trade> trades = new HashMap<>();

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

			if (isDevelopment) {
				inventoryTest = new InventoryTest(main);
			}

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
			PermissionGroup.INSTANCE.loadPermissionGroups();

			String version = Bukkit.getBukkitVersion().split("-")[0];
			isVersionSupported = VersionHandler.isVersionSupported(version);

			if (isVersionSupported) {
				CommandAPI.onLoad(new CommandAPIConfig().missingExecutorImplementationMessage("You cannot execute this command!"));
			}
			economyCommand = new EconomyCommand(main);
		}
	}

	@Override
	public void onDisable() {
		if (!isNewUpdateAvailable) {
			saveItem.saveItems();
			saveFriends.saveFriends();
			PermissionGroup.INSTANCE.savePermissionGroups();

			Bukkit.getScheduler().cancelTask(inventoryManagementTaskId);
			Bukkit.getScheduler().cancelTask(coinDisplayTaskId);

			EconomyAPI.onDisable();
			CommandAPI.onDisable();
		}
	}

	private void commandRegistration() {
		economyCommand.register();
		consoleCommands.register();

		if (isDevelopment) {
			new TestsCommand(main).register();
		}
	}

	private void listenerRegistration() {
		PluginManager manager = Bukkit.getPluginManager();
		manager.registerEvents(new BuyOfferMenuListener(main), this);
		manager.registerEvents(new CancelOfferMenuListener(main), this);
		manager.registerEvents(new JoinCoinManagement(main), this);
		manager.registerEvents(expiredOfferMenu, this);
		manager.registerEvents(loadFriends, this);

		if (shouldRegisterUpdateInformation) {
			manager.registerEvents(new UpdateInformation(main), this);
			shouldRegisterUpdateInformation = false;
		}

		if (isDevelopment) {
			manager.registerEvents(inventoryTest, this);
		}

		manager.registerEvents(new Listener() {
			@EventHandler
			public void onJoin(PlayerJoinEvent event) {
				Permission.updatePermissions(event.getPlayer());
			}
		}, this);
	}

	// Test
	public InventoryTest getInventoryTest() {
		return inventoryTest;
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

	// Store trade system stuff
	public HashMap<UUID, Trade> getTrades() {
		return trades;
	}
}
