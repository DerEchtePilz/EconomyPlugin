package me.derechtepilz.economy;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import dev.jorel.commandapi.RegisteredCommand;
import me.derechtepilz.economy.economymanager.*;
import me.derechtepilz.economy.itemmanager.*;
import me.derechtepilz.economy.itemmanager.save.LoadItems;
import me.derechtepilz.economy.itemmanager.save.SaveItems;
import me.derechtepilz.economy.listeners.CheckPluginUpdate;
import me.derechtepilz.economy.minecraft.HelpCommand;
import me.derechtepilz.economy.modules.discord.DiscordBot;
import me.derechtepilz.economy.modules.discord.ServerStatus;
import me.derechtepilz.economy.modules.discord.StartUpBot;
import me.derechtepilz.economy.modules.discord.communication.minecraftserver.ChattingFromMinecraftServer;
import me.derechtepilz.economy.modules.discord.communication.minecraftserver.DiscordCommand;
import me.derechtepilz.economy.playermanager.TradeCommand;
import me.derechtepilz.economy.playermanager.TradeMenu;
import me.derechtepilz.economy.playermanager.friend.Friend;
import me.derechtepilz.economy.playermanager.friend.FriendCommand;
import me.derechtepilz.economy.playermanager.permission.CustomPermissionGroup;
import me.derechtepilz.economy.playermanager.permission.PermissionCommand;
import me.derechtepilz.economy.utility.CheckUpdate;
import me.derechtepilz.economy.utility.ICooldown;
import me.derechtepilz.economy.utility.Language;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import me.derechtepilz.economy.utility.config.Config;
import me.derechtepilz.economy.utility.config.ConfigCommand;
import net.dv8tion.jda.api.JDA;
import okhttp3.OkHttpClient;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.*;

public final class Main extends JavaPlugin {
    private static Main plugin;
    private Language language;

    private final HashMap<UUID, ItemStack[]> offeringPlayers = new HashMap<>();
    private final HashMap<String, ItemStack[]> specialOffers = new HashMap<>();
    private final HashMap<UUID, Integer> earnedCoins = new HashMap<>();

    private final HashMap<UUID, BankManager> bankAccounts = new HashMap<>();

    private final List<ItemStack> offeredItemsList = new ArrayList<>();

    private ItemCancelMenu itemCancelMenu;
    private ItemBuyMenu itemBuyMenu;
    private TradeMenu tradeMenu;

    private final CreateOfferCommand createOfferCommand = new CreateOfferCommand();
    private final CancelOfferCommand cancelOfferCommand = new CancelOfferCommand();
    private final BuyCommand buyCommand = new BuyCommand();
    private final GiveCoinsCommand giveCoinsCommand = new GiveCoinsCommand();
    private final TakeCoinsCommand takeCoinsCommand = new TakeCoinsCommand();
    private final SetCoinsCommand setCoinsCommand = new SetCoinsCommand();
    private final PermissionCommand permissionCommand = new PermissionCommand();
    private final ConfigCommand configCommand = new ConfigCommand();
    private final TradeCommand tradeCommand = new TradeCommand();
    private final HelpCommand helpCommand = new HelpCommand();
    private final DiscordCommand discordCommand = new DiscordCommand();
    private final FriendCommand friendCommand = new FriendCommand();

    private final CustomPermissionGroup customPermissionGroup = new CustomPermissionGroup();
    private final Friend friend = new Friend();

    private final Map<UUID, ICooldown> cooldownMap = new HashMap<>();

    private boolean wasCommandAPILoaded;
    private boolean isNewUpdateAvailable = false;

    private int taskId;

    @Override
    public void onEnable() {
        itemCancelMenu = new ItemCancelMenu();
        itemBuyMenu = new ItemBuyMenu();
        tradeMenu = new TradeMenu();

        listenerRegistration();

        initializeEnableProcedure();

        if (wasCommandAPILoaded) {
            commandRegistration();
            CommandAPI.onEnable(this);
        }

        getLogger().info(TranslatableChatComponent.read("main.onEnable.plugin_enable_message"));

        startDiscordBot(Config.get("discordToken"));
    }

    @Override
    public void onLoad() {
        plugin = this;
        try {
            Config.loadConfig();
            customPermissionGroup.loadPermissionGroup();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Config.contains("language")) {
            language = Language.valueOf(Config.get("language"));
        } else {
            language = Language.EN_US;
        }

        String version = Bukkit.getBukkitVersion().split("-")[0];
        if (VersionHandler.isVersionSupported(version)) {
            CommandAPI.onLoad(new CommandAPIConfig().missingExecutorImplementationMessage(TranslatableChatComponent.read("command.wrong_executor")));
            wasCommandAPILoaded = true;
        } else {
            getLogger().severe(TranslatableChatComponent.read("main.onLoad.version_info").replace("%s", version));
            wasCommandAPILoaded = false;
        }

        new LoadItems();
        isNewUpdateAvailable = new CheckUpdate().checkForUpdate();

        if (isNewUpdateAvailable) {
            getLogger().warning(TranslatableChatComponent.read("checkPluginUpdate.new_update_available_console"));
        }
    }

    @Override
    public void onDisable() {
        stopDiscordBot();
        if (wasCommandAPILoaded) {
            List<String> commandNames = new ArrayList<>();

            for (RegisteredCommand command : CommandAPI.getRegisteredCommands()) {
                if (!commandNames.contains(command.commandName())) {
                    commandNames.add(command.commandName());
                }
            }

            for (String commandName : commandNames) {
                CommandAPI.unregister(commandName);
            }
        }

        try {
            new SaveItems();
            Config.saveConfig();
            customPermissionGroup.buildPermissionGroup();
        } catch (IOException e) {
            e.printStackTrace();
        }

        getLogger().info(TranslatableChatComponent.read("main.onDisable.plugin_disable_message"));
    }

    public static Main getInstance() {
        return plugin;
    }

    private void commandRegistration() {
        createOfferCommand.register();
        cancelOfferCommand.register();
        buyCommand.register();
        giveCoinsCommand.register();
        takeCoinsCommand.register();
        setCoinsCommand.register();
        permissionCommand.register();
        configCommand.register();
        tradeCommand.register();
        helpCommand.register();
        discordCommand.register();
        friendCommand.register();
    }

    private void listenerRegistration() {
        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(itemCancelMenu, this);
        manager.registerEvents(itemBuyMenu, this);
        manager.registerEvents(tradeMenu, this);
        manager.registerEvents(new ManageCoinsWhenJoining(), this);
        manager.registerEvents(new ChattingFromMinecraftServer(), this);
        manager.registerEvents(new StartUpBot(), this);
        manager.registerEvents(new ServerStatus(), this);
        manager.registerEvents(new CheckPluginUpdate(), this);
    }

    public HashMap<UUID, ItemStack[]> getPlayerOffers() {
        return offeringPlayers;
    }

    public HashMap<String, ItemStack[]> getSpecialOffers() {
        return specialOffers;
    }

    public HashMap<UUID, Integer> getEarnedCoins() {
        return earnedCoins;
    }

    public HashMap<UUID, BankManager> getBankAccounts() {
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

    public TradeMenu getTradeMenu() {
        return tradeMenu;
    }

    public CustomPermissionGroup getCustomPermissionGroup() {
        return customPermissionGroup;
    }

    public Friend getFriend() {
        return friend;
    }

    public Language getLanguage() {
        return language;
    }

    public boolean isNewUpdateAvailable() {
        return isNewUpdateAvailable;
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

    public void startDiscordBot(String token) {
        if (DiscordBot.getDiscordBot() != null) {
            DiscordBot.getDiscordBot().setActive(false);
            if (DiscordBot.getDiscordBot().getJda() != null) {
                DiscordBot.getDiscordBot().getJda().shutdownNow();
            }
            DiscordBot.getDiscordBot().setDiscordBotNull();
        }
        try {
            new DiscordBot(token);
        } catch (LoginException e) {
            getLogger().severe(TranslatableChatComponent.read("discord.startup.failed"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"", "BusyWait"})
    public void stopDiscordBot() {
        DiscordBot.getDiscordBot().setActive(false);
        try {
            if (DiscordBot.getDiscordBot() != null) {
                DiscordBot.getDiscordBot().sendShutdownMessage();
                Thread.sleep(2000);
                DiscordBot.getDiscordBot().getJda().shutdownNow();

                OkHttpClient client = DiscordBot.getDiscordBot().getJda().getHttpClient();
                client.connectionPool().evictAll();
                client.dispatcher().executorService().shutdown();
                while (DiscordBot.getDiscordBot().getJda().getStatus() != JDA.Status.SHUTDOWN) {
                    Thread.sleep(20);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initializeEnableProcedure() {
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.closeInventory();
            p.sendMessage(TranslatableChatComponent.read("main.initialize_enable_procedure.coin_display_missing"));
        });
    }
}