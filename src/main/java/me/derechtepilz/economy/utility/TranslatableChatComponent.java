package me.derechtepilz.economy.utility;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.derechtepilz.economy.Main;

import java.io.*;

public class TranslatableChatComponent {
    private TranslatableChatComponent() {

    }

    private static String line;
    private static String languages;

    public static String read(String translationKey) {
        try {
            InputStream inputStream = Main.getInstance().getResource("lang.json");
            if (inputStream == null) return null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder builder = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            languages = builder.toString();

            JsonElement element = JsonParser.parseString(languages);
            JsonObject object = element.getAsJsonObject();
            if (object.has(translationKey)) {
                return object.get(translationKey).getAsString();
            } else {
                return "§cTranslation key '" + translationKey + "' not found!";
            }
        } catch (IOException exception) {
            return "§cNo lang.json file was found!";
        }
    }
}
