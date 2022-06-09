package me.derechtepilz.economy.utility.config;

import com.google.gson.*;
import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.utility.TranslatableChatComponent;

public class Config {

    static final int itemQuantitiesMinAmount = 1;
    static final int itemQuantitiesMaxAmount = 10;
    static final int itemPriceMinAmount = 50;
    static final int itemPriceMaxAmount = 5000;
    static final double startBalance = 50;
    static final double interest = 1;
    static String language = "EN_US";

    private static String configValues = "";
    private static boolean isLoaded = false;

    private Config() {

    }

    public static void loadConfig() {
        if (isLoaded) {
            Main.getInstance().getLogger().severe(TranslatableChatComponent.read("config.loadConfig.is_loaded"));
            return;
        }
        configValues = new LoadConfig().getConfigValues(false);
        isLoaded = true;
    }

    public static Object get(ConfigFields field) {
        JsonElement element = JsonParser.parseString(configValues);
        JsonObject object = element.getAsJsonObject();
        switch (field) {
            case ITEM_QUANTITIES_MIN_AMOUNT -> {
                return object.getAsJsonObject("itemQuantities").getAsJsonPrimitive("minAmount").getAsInt();
            }
            case ITEM_QUANTITIES_MAX_AMOUNT -> {
                return object.getAsJsonObject("itemQuantities").getAsJsonPrimitive("maxAmount").getAsInt();
            }
            case ITEM_PRICE_MIN_AMOUNT -> {
                return object.getAsJsonObject("itemPrice").getAsJsonPrimitive("minAmount").getAsInt();
            }
            case ITEM_PRICE_MAX_AMOUNT -> {
                return object.getAsJsonObject("itemPrice").getAsJsonPrimitive("maxAmount").getAsInt();
            }
            case START_BALANCE -> {
                return object.getAsJsonPrimitive("startBalance").getAsDouble();
            }
            case INTEREST -> {
                return object.getAsJsonPrimitive("interest").getAsDouble();
            }
            case LANGUAGE -> {
                return object.getAsJsonPrimitive("language").getAsString();
            }
            default -> {
                return null;
            }
        }
    }

    public static void set(ConfigFields field, Object value) {
        switch (field) {
            case ITEM_QUANTITIES_MIN_AMOUNT -> new SaveConfig(buildJson((Integer) value, (Integer) get(ConfigFields.ITEM_QUANTITIES_MAX_AMOUNT), (Integer) get(ConfigFields.ITEM_PRICE_MIN_AMOUNT), (Integer) get(ConfigFields.ITEM_PRICE_MAX_AMOUNT), (Double) get(ConfigFields.START_BALANCE), (Double) get(ConfigFields.INTEREST), (String) get(ConfigFields.LANGUAGE)));
            case ITEM_QUANTITIES_MAX_AMOUNT -> new SaveConfig(buildJson((Integer) get(ConfigFields.ITEM_QUANTITIES_MIN_AMOUNT), (Integer) value, (Integer) get(ConfigFields.ITEM_PRICE_MIN_AMOUNT), (Integer) get(ConfigFields.ITEM_PRICE_MAX_AMOUNT), (Double) get(ConfigFields.START_BALANCE), (Double) get(ConfigFields.INTEREST), (String) get(ConfigFields.LANGUAGE)));
            case ITEM_PRICE_MIN_AMOUNT -> new SaveConfig(buildJson((Integer) get(ConfigFields.ITEM_QUANTITIES_MIN_AMOUNT), (Integer) get(ConfigFields.ITEM_QUANTITIES_MAX_AMOUNT), (Integer) value, (Integer) get(ConfigFields.ITEM_PRICE_MAX_AMOUNT), (Double) get(ConfigFields.START_BALANCE), (Double) get(ConfigFields.INTEREST), (String) get(ConfigFields.LANGUAGE)));
            case ITEM_PRICE_MAX_AMOUNT -> new SaveConfig(buildJson((Integer) get(ConfigFields.ITEM_QUANTITIES_MIN_AMOUNT), (Integer) get(ConfigFields.ITEM_QUANTITIES_MAX_AMOUNT), (Integer) get(ConfigFields.ITEM_PRICE_MIN_AMOUNT), (Integer) value, (Double) get(ConfigFields.START_BALANCE), (Double) get(ConfigFields.INTEREST), (String) get(ConfigFields.LANGUAGE)));
            case START_BALANCE -> new SaveConfig(buildJson((Integer) get(ConfigFields.ITEM_QUANTITIES_MIN_AMOUNT), (Integer) get(ConfigFields.ITEM_QUANTITIES_MAX_AMOUNT), (Integer) get(ConfigFields.ITEM_PRICE_MIN_AMOUNT), (Integer) get(ConfigFields.ITEM_PRICE_MAX_AMOUNT), (Double) value, (Double) get(ConfigFields.INTEREST), (String) get(ConfigFields.LANGUAGE)));
            case INTEREST -> new SaveConfig(buildJson((Integer) get(ConfigFields.ITEM_QUANTITIES_MIN_AMOUNT), (Integer) get(ConfigFields.ITEM_QUANTITIES_MAX_AMOUNT), (Integer) get(ConfigFields.ITEM_PRICE_MIN_AMOUNT), (Integer) get(ConfigFields.ITEM_PRICE_MAX_AMOUNT), (Double) get(ConfigFields.START_BALANCE), (Double) value, (String) get(ConfigFields.LANGUAGE)));
            case LANGUAGE -> new SaveConfig(buildJson((Integer) get(ConfigFields.ITEM_QUANTITIES_MIN_AMOUNT), (Integer) get(ConfigFields.ITEM_QUANTITIES_MAX_AMOUNT), (Integer) get(ConfigFields.ITEM_PRICE_MIN_AMOUNT), (Integer) get(ConfigFields.ITEM_PRICE_MAX_AMOUNT), (Double) get(ConfigFields.START_BALANCE), (Double) get(ConfigFields.INTEREST), (String) value));
        }
    }

    public static boolean contains(ConfigFields field) {
        JsonElement element = JsonParser.parseString(configValues);
        JsonObject object = element.getAsJsonObject();
        switch (field) {
            case ITEM_QUANTITIES_MIN_AMOUNT, ITEM_QUANTITIES_MAX_AMOUNT -> {
                return object.has("itemQuantities");
            }
            case ITEM_PRICE_MIN_AMOUNT, ITEM_PRICE_MAX_AMOUNT -> {
                return object.has("itemPrice");
            }
            case START_BALANCE -> {
                return object.has("startBalance");
            }
            case INTEREST -> {
                return object.has("interest");
            }
            case LANGUAGE -> {
                return object.has("language");
            }
            default -> {
                return false;
            }
        }
    }

    public static void reloadConfig() {
        configValues = new LoadConfig().getConfigValues(false);
    }

    public static void resetConfig() {
        configValues = new LoadConfig().getConfigValues(true);
    }

    private static String buildJson(int itemQuantitiesMinAmount, int itemQuantitiesMaxAmount, int itemPriceMinAmount, int itemPriceMaxAmount, double startBalance, double interest, String language) {
        JsonObject configValues = new JsonObject();
        JsonObject itemQuantities = new JsonObject();
        JsonObject itemPrice = new JsonObject();

        itemQuantities.addProperty("minAmount", itemQuantitiesMinAmount);
        itemQuantities.addProperty("maxAmount", itemQuantitiesMaxAmount);
        itemPrice.addProperty("minAmount", itemPriceMinAmount);
        itemPrice.addProperty("maxAmount", itemPriceMaxAmount);

        configValues.add("itemQuantities", itemQuantities);
        configValues.add("itemPrice", itemPrice);
        configValues.addProperty("startBalance", startBalance);
        configValues.addProperty("interest", interest);
        configValues.addProperty("language", language);

        return new GsonBuilder().setPrettyPrinting().create().toJson(configValues);
    }
}