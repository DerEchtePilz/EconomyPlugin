package me.derechtepilz.economy.utility;

import me.derechtepilz.economy.Main;
import org.bukkit.NamespacedKey;

import javax.naming.Name;

public enum NamespacedKeys {
    CREATOR(new NamespacedKey(Main.getInstance(), "itemSeller")),
    UUID(new NamespacedKey(Main.getInstance(), "id")),
    PRICE(new NamespacedKey(Main.getInstance(), "price")),
    BALANCE(new NamespacedKey(Main.getInstance(), "balance")),
    LAST_INTEREST(new NamespacedKey(Main.getInstance(), "lastInterest")),
    START_BALANCE(new NamespacedKey(Main.getInstance(), "startBalance")),
    COINS_EARNED(new NamespacedKey(Main.getInstance(), "coinsEarned")),
    PERMISSION(new NamespacedKey(Main.getInstance(), "permissions"));

    private final NamespacedKey key;
    NamespacedKeys(NamespacedKey key) {
        this.key = key;
    }

    public NamespacedKey getKey() {
        return key;
    }
}
