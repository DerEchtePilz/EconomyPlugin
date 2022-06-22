package me.derechtepilz.economy.utility;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.derechtepilz.economy.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TranslatableChatComponent {

    private static String languages = "";

    private TranslatableChatComponent() {

    }

    public static String read(String translationKey) {
        if (languages.equals("")) {
            String line;
            try {
                InputStream inputStream = Main.getInstance().getResource("lang.json");
                if (inputStream == null) {
                    return "§cNo lang.json file was found!";
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuilder builder = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                languages = builder.toString();
            } catch (IOException exception) {
                languages = "§cNo lang.json file was found!";
            }
        }

        JsonElement element = JsonParser.parseString(languages);
        JsonObject object = element.getAsJsonObject();

        if (languages.equals("§cNo lang.json file was found!")) {
            return "§cNo lang.json file was found!";
        }
        switch (Main.getInstance().getLanguage()) {
            case EN_US -> {
                if (object.has(translationKey)) {
                    return object.getAsJsonObject(translationKey).get("en_us").getAsString();
                } else {
                    return translationKey;
                }
            }
            case DE_DE -> {
                if (object.has(translationKey)) {
                    return object.getAsJsonObject(translationKey).get("de_de").getAsString();
                } else {
                    return translationKey;
                }
            }
            default -> {
                return translationKey;
            }
        }
    }

    public static String getLanguages() {
        return languages;
    }
}
