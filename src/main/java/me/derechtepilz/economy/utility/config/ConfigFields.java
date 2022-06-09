package me.derechtepilz.economy.utility.config;

import com.google.gson.JsonParser;

public enum ConfigFields {
    ITEM_QUANTITIES_MIN_AMOUNT("itemQuantities", "minAmount"),
    ITEM_QUANTITIES_MAX_AMOUNT("itemQuantities", "maxAmount"),
    ITEM_PRICE_MIN_AMOUNT("itemPrice", "minAmount"),
    ITEM_PRICE_MAX_AMOUNT("itemPrice", "maxAmount"),
    START_BALANCE("startBalance"),
    INTEREST("interest"),
    LANGUAGE("language"),
    DISCORD_BOT_TOKEN("discordToken"),
    DISCORD_GUILD_ID("guildId");

    private final String primitive;
    private String object;

    ConfigFields(String jsonPrimitive) {
        this.primitive = jsonPrimitive;
    }

    ConfigFields(String jsonObject, String jsonPrimitive) {
        this.object = jsonObject;
        this.primitive = jsonPrimitive;
    }

    public String getValue() {
        if (this.object != null) {
            return JsonParser.parseString(Config.configValues).getAsJsonObject().getAsJsonObject(object).getAsJsonPrimitive(primitive).getAsString();
        } else {
            return JsonParser.parseString(Config.configValues).getAsJsonObject().getAsJsonPrimitive(primitive).getAsString();
        }
    }

    public boolean containsValue(String key) {
        return JsonParser.parseString(Config.configValues).getAsJsonObject().has(key);
    }

    public String getObject() {
        return object;
    }

    public String getPrimitive() {
        return primitive;
    }
}
