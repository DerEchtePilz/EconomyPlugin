package me.derechtepilz.economy.utility.config;

import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.utility.TranslatableChatComponent;

public class Config {
    static String configValues = "";
    private static boolean isLoaded = false;

    private Config() {

    }

    public static void loadConfig() {
        if (isLoaded) {
            Main.getInstance().getLogger().severe(TranslatableChatComponent.read("config.loadConfig.is_loaded"));
            return;
        }
        new LoadConfig().getConfigValues(false);
        isLoaded = true;
    }

    public static String get(ConfigFields field) {
        return field.getValue();
    }

    public static void set(ConfigFields field, String value) {
        switch (field) {
            case ITEM_QUANTITIES_MIN_AMOUNT -> Main.getInstance().getJsonBuilder().setItemQuantitiesMinAmount(value);
            case ITEM_QUANTITIES_MAX_AMOUNT -> Main.getInstance().getJsonBuilder().setItemQuantitiesMaxAmount(value);
            case ITEM_PRICE_MIN_AMOUNT -> Main.getInstance().getJsonBuilder().setItemPriceMinAmount(value);
            case ITEM_PRICE_MAX_AMOUNT -> Main.getInstance().getJsonBuilder().setItemPriceMaxAmount(value);
            case START_BALANCE -> Main.getInstance().getJsonBuilder().setStartBalance(value);
            case INTEREST -> Main.getInstance().getJsonBuilder().setInterest(value);
            case LANGUAGE -> Main.getInstance().getJsonBuilder().setLanguage(value);
        }
        new SaveConfig(Main.getInstance().getJsonBuilder().buildJson());
    }

    public static void saveConfig() {
        new SaveConfig(Main.getInstance().getJsonBuilder().buildJson());
    }

    public static boolean contains(ConfigFields field) {
        return field.containsValue(field.getPrimitive());
    }

    public static void reloadConfig() {
        new LoadConfig().getConfigValues(false);
    }

    public static void resetConfig() {
        new LoadConfig().getConfigValues(true);
    }
}