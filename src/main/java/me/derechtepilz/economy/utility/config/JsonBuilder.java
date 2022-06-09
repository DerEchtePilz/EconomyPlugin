package me.derechtepilz.economy.utility.config;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class JsonBuilder {
    String itemQuantitiesMinAmount = "1";
    String itemQuantitiesMaxAmount = "10";
    String itemPriceMinAmount = "50";
    String itemPriceMaxAmount = "5000";
    String startBalance = "50.0";
    String interest = "1.0";
    String language = "EN_US";
    String discordToken = "";
    String guildId = "";

    public JsonBuilder setItemQuantitiesMinAmount(String itemQuantitiesMinAmount) {
        this.itemQuantitiesMinAmount = itemQuantitiesMinAmount;
        return this;
    }

    public JsonBuilder setItemQuantitiesMaxAmount(String itemQuantitiesMaxAmount) {
        this.itemQuantitiesMaxAmount = itemQuantitiesMaxAmount;
        return this;
    }

    public JsonBuilder setItemPriceMinAmount(String itemPriceMinAmount) {
        this.itemPriceMinAmount = itemPriceMinAmount;
        return this;
    }

    public JsonBuilder setItemPriceMaxAmount(String itemPriceMaxAmount) {
        this.itemPriceMaxAmount = itemPriceMaxAmount;
        return this;
    }

    public JsonBuilder setStartBalance(String startBalance) {
        this.startBalance = startBalance;
        return this;
    }

    public JsonBuilder setInterest(String interest) {
        this.interest = interest;
        return this;
    }

    public JsonBuilder setLanguage(String language) {
        this.language = language;
        return this;
    }

    public JsonBuilder setDiscordToken(String discordToken) {
        this.discordToken = discordToken;
        return this;
    }

    public JsonBuilder setDiscordGuild(String guildId) {
        this.guildId = guildId;
        return this;
    }

    public String buildJson() {
        JsonObject configValues = new JsonObject();
        JsonObject itemQuantities = new JsonObject();
        JsonObject itemPrice = new JsonObject();

        if (!LoadConfig.equalValues(itemQuantitiesMinAmount, "minAmount", "itemQuantities")) {
            itemQuantities.addProperty("minAmount", itemQuantitiesMinAmount);
        } else {
            itemQuantities.addProperty("minAmount", Config.get(ConfigFields.ITEM_QUANTITIES_MIN_AMOUNT));
        }

        if (!LoadConfig.equalValues(itemQuantitiesMaxAmount, "maxAmount", "itemQuantities")) {
            itemQuantities.addProperty("maxAmount", itemQuantitiesMaxAmount);
        } else {
            itemQuantities.addProperty("maxAmount", Config.get(ConfigFields.ITEM_QUANTITIES_MAX_AMOUNT));
        }

        if (!LoadConfig.equalValues(itemPriceMinAmount, "minAmount", "itemPrice")) {
            itemPrice.addProperty("minAmount", itemPriceMinAmount);
        } else {
            itemPrice.addProperty("minAmount", Config.get(ConfigFields.ITEM_PRICE_MIN_AMOUNT));
        }

        if (!LoadConfig.equalValues(itemPriceMaxAmount, "maxAmount", "itemPrice")) {
            itemPrice.addProperty("maxAmount", itemPriceMaxAmount);
        } else {
            itemPrice.addProperty("maxAmount", Config.get(ConfigFields.ITEM_PRICE_MAX_AMOUNT));
        }

        configValues.add("itemQuantities", itemQuantities);
        configValues.add("itemPrice", itemPrice);

        if (!LoadConfig.equalValues(startBalance, "startBalance")) {
            configValues.addProperty("startBalance", startBalance);
        } else {
            configValues.addProperty("startBalance", Config.get(ConfigFields.START_BALANCE));
        }

        if (!LoadConfig.equalValues(interest, "interest")) {
            configValues.addProperty("interest", interest);
        } else {
            configValues.addProperty("interest", Config.get(ConfigFields.INTEREST));
        }

        if (!LoadConfig.equalValues(language, "language")) {
            configValues.addProperty("language", language);
        } else {
            configValues.addProperty("language", Config.get(ConfigFields.LANGUAGE));
        }

        if (!LoadConfig.equalValues(discordToken, "discordToken")) {
            configValues.addProperty("discordToken", discordToken);
        } else {
            configValues.addProperty("discordToken", Config.get(ConfigFields.DISCORD_BOT_TOKEN));
        }

        if (!LoadConfig.equalValues(guildId, "guildId")) {
            configValues.addProperty("guildId", guildId);
        } else {
            configValues.addProperty("guildId", Config.get(ConfigFields.DISCORD_GUILD_ID));
        }

        return new GsonBuilder().setPrettyPrinting().create().toJson(configValues);
    }
}
