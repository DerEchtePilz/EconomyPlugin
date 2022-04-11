package me.derechtepilz.economy.utility;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.derechtepilz.economy.Main;

public class TranslatableChatComponent {
    private TranslatableChatComponent() {

    }

    public static String read(String translationKey) {
        JsonElement element = JsonParser.parseString(Main.getInstance().getLanguages());
        JsonObject object = element.getAsJsonObject();
        switch (Main.getInstance().getLanguage()) {
            case EN_US -> {
                if (object.getAsJsonObject("en_us").has(translationKey)) {
                    return object.getAsJsonObject("en_us").get(translationKey).getAsString();
                } else {
                    return translationKey;
                }
            }
            case EN_EN -> {
                if (object.getAsJsonObject("en_en").has(translationKey)) {
                    return object.getAsJsonObject("en_en").get(translationKey).getAsString();
                } else {
                    return translationKey;
                }
            }
            case DE_DE -> {
                if (object.getAsJsonObject("de_de").has(translationKey)) {
                    return object.getAsJsonObject("de_de").get(translationKey).getAsString();
                } else {
                    return translationKey;
                }
            }
            default -> {
                return translationKey;
            }
        }
    }
}
