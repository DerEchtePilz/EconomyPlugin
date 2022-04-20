/**
 * MIT License
 *
 * Copyright (c) 2022 DerEchtePilz
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.derechtepilz.economy;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import me.derechtepilz.economy.economymanager.BankManager;
import me.derechtepilz.economy.economymanager.GiveCoinsCommand;
import me.derechtepilz.economy.economymanager.ManageCoinsWhenJoining;
import me.derechtepilz.economy.itemmanager.*;
import me.derechtepilz.economy.utility.Config;
import me.derechtepilz.economy.bukkitcommands.FallbackCommand;
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

    private final HashMap<UUID, ItemStack> offeredItems = new HashMap<>();
    private final HashMap<UUID, ItemStack[]> offeringPlayers = new HashMap<>();
    private final HashMap<String, ItemStack[]> specialOffers = new HashMap<>();

    private final HashMap<Player, BankManager> bankAccounts = new HashMap<>();

    private final List<ItemStack> offeredItemsList = new ArrayList<>();

    private ItemCancelMenu itemCancelMenu;
    private ItemBuyMenu itemBuyMenu;

    private boolean wasCommandAPILoaded;

    @Override
    public void onEnable() {
        plugin = this;

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
        String version = Bukkit.getBukkitVersion().split("-")[0];
        if (VersionHandler.isVersionSupported(version)) {
            CommandAPI.onLoad(new CommandAPIConfig());
            wasCommandAPILoaded = true;
        } else {
            getLogger().severe(TranslatableChatComponent.read("main.onLoad.version_info").replace("%s", Bukkit.getBukkitVersion().split("-")[0]));
            wasCommandAPILoaded = false;
        }

        Config.loadConfig();
    }

    @Override
    public void onDisable() {

        if (wasCommandAPILoaded) {
            CommandAPI.unregister("createoffer");
            CommandAPI.unregister("canceloffer");
            CommandAPI.unregister("buy");
            CommandAPI.unregister("givecoins");
        }

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
        }
        getCommand("fallback").setExecutor(new FallbackCommand());
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