package me.derechtepilz.economy;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import dev.jorel.commandapi.CommandAPIHandler;
import me.derechtepilz.economy.bukkitcommands.commands.FallbackCommand;
import me.derechtepilz.economy.economymanager.*;
import me.derechtepilz.economy.itemmanager.*;
import me.derechtepilz.economy.itemmanager.save.LoadItems;
import me.derechtepilz.economy.itemmanager.save.SaveItems;
import me.derechtepilz.economy.playermanager.PermissionCommand;
import me.derechtepilz.economy.utility.Language;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import me.derechtepilz.economy.utility.config.Config;
import me.derechtepilz.economy.utility.config.ConfigCommand;
import me.derechtepilz.economy.utility.config.ConfigFields;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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

    private final FallbackCommand fallbackCommand = new FallbackCommand();

    private boolean wasCommandAPILoaded;

    @Override
    public void onEnable() {
        itemCancelMenu = new ItemCancelMenu();
        itemBuyMenu = new ItemBuyMenu();

        commandRegistration();
        listenerRegistration();

        initializeEnableProcedure();

        getLogger().info(TranslatableChatComponent.read("main.onEnable.plugin_enable_message"));
    }

    @Override
    public void onLoad() {
        plugin = this;
        Config.loadConfig();


        if (Config.contains(ConfigFields.LANGUAGE)) {
            language = Language.valueOf((String) Config.get(ConfigFields.LANGUAGE));
        } else {
            language = Language.EN_US;
        }

        String version = Bukkit.getBukkitVersion().split("-")[0];
        if (VersionHandler.isVersionSupported(version)) {
            CommandAPI.onLoad(new CommandAPIConfig().missingExecutorImplementationMessage(TranslatableChatComponent.read("command.wrong_executor")));
            wasCommandAPILoaded = true;
        } else {
            getLogger().severe(TranslatableChatComponent.read("main.onLoad.version_info").replace("%s", Bukkit.getBukkitVersion().split("-")[0]));
            wasCommandAPILoaded = false;
        }

        new LoadItems();
    }

    @Override
    public void onDisable() {

        if (wasCommandAPILoaded) {
            List<String> commandNames = new ArrayList<>();
            try {
                Field field = Class.forName(CommandAPIHandler.class.getName()).getDeclaredField("registeredCommands");
                field.setAccessible(true);

                List list = (List) field.get(CommandAPIHandler.getInstance());
                for (Object object : list) {
                    String[] firstSplit = object.toString().split("=");
                    String commandName = firstSplit[1].split(",")[0];
                    if (!commandNames.contains(commandName)) {
                        commandNames.add(commandName);
                    }
                }
            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

            for (String commandName : commandNames) {
                CommandAPI.unregister(commandName);
            }
        }

        new SaveItems();

        getLogger().info(TranslatableChatComponent.read("main.onDisable.plugin_disable_message"));
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
            new ConfigCommand();
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

    private void initializeEnableProcedure() {
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.closeInventory();
            p.sendMessage(TranslatableChatComponent.read("main.initialize_enable_procedure.coin_display_missing"));
        });
    }
}