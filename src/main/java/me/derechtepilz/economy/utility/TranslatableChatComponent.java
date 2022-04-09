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
        if (object.has(translationKey)) {
            return object.get(translationKey).getAsString();
        } else {
            return translationKey;
        }
    }
}
