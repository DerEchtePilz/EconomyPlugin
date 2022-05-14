package me.derechtepilz.economy.utility;

import me.derechtepilz.economy.Main;

import java.util.Arrays;

public class Config {

    private static int itemQuantitiesMinAmount = 1;
    private static int itemQuantitiesMaxAmount = 10;
    private static int itemPriceMinAmount = 50;
    private static int itemPriceMaxAmount = 5000;
    private static int startBalance = 50;
    private static int interest = 1;
    private static String language = "EN_US";

    private static boolean isLoaded = false;

    private Config() {

    }

    public static void loadConfig() {
        if (isLoaded) {
            Main.getInstance().getLogger().severe(TranslatableChatComponent.read("config.loadConfig.is_loaded"));
            return;
        }
        isLoaded = true;
        if (Main.getInstance().getConfig().contains("itemQuantities.minAmount")) {
            itemQuantitiesMinAmount = Main.getInstance().getConfig().getInt("itemQuantities.minAmount");
            itemQuantitiesMaxAmount = Main.getInstance().getConfig().getInt("itemQuantities.maxAmount");
            itemPriceMinAmount = Main.getInstance().getConfig().getInt("itemPrice.minAmount");
            itemPriceMaxAmount = Main.getInstance().getConfig().getInt("itemPrice.maxAmount");
            startBalance = Main.getInstance().getConfig().getInt("startBalance");
            interest = Main.getInstance().getConfig().getInt("interest");
            language = Main.getInstance().getConfig().getString("language");
        } else {
            writeConfig(true);
        }
    }

    public static void writeConfig(boolean overwrite) {
        if (overwrite) {
            set("itemQuantities.minAmount", itemQuantitiesMinAmount);
            set("itemQuantities.maxAmount", itemQuantitiesMaxAmount);

            set("itemPrice.minAmount", itemPriceMinAmount);
            set("itemPrice.maxAmount", itemPriceMaxAmount);

            set("startBalance", startBalance);
            set("interest", interest);

            set("language", language);

            Main.getInstance().getConfig().setComments("itemQuantities", Arrays.asList(
                    "---------- CONFIG ----------",
                    "Please do only edit this file if you know what you are doing",
                    "Everything is configurable with an in-game command",
                    null,
                    "Saves the amounts of items that can be sold at once"
            ));
            Main.getInstance().getConfig().setComments("itemPrice", Arrays.asList(
                    null,
                    "Saves the minimum and maximum amount items can be sold for"
            ));
            Main.getInstance().getConfig().setComments("startBalance", Arrays.asList(
                    null,
                    "Saves the amount of coins new players get when they first log onto the server!",
                    "When this is increased players who already got this bonus",
                    "will be given this amount minus their own start bonus amount",
                    "When this is decreased players who already got the bonus won't lose money though"
            ));
            Main.getInstance().getConfig().setComments("interest", Arrays.asList(
                    null,
                    "Value in percentages",
                    "Players are given interest every 24 hours starting from when they first join the server",
                    "The formula is as follows: balance * (1 + (interest / 100))",
                    "This value can also be negative or zero.",
                    "When using a negative value the formula is the same",
                    "When using a zero the formula is used but obviously doesn't change anything",
                    "The plugin won't allow a value of -100 though"
            ));
            Main.getInstance().getConfig().setComments("language", Arrays.asList(
                    null,
                    "Saves the server language"
            ));
            saveConfig();
        } else {
            if (!Main.getInstance().getConfig().contains("itemQuantities.minAmount")) {
                writeConfig(true);
            }
        }
    }

    public static void set(String path, Object value) {
        Main.getInstance().getConfig().set(path, value);
    }

    public static Object get(String path) {
        return Main.getInstance().getConfig().get(path);
    }

    public static void saveConfig() {
        Main.getInstance().saveConfig();
    }

    public static boolean contains(String path) {
        return Main.getInstance().getConfig().contains(path);
    }

    public static void reloadConfig() {
        Main.getInstance().reloadConfig();
    }

}