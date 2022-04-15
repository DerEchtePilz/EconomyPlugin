/**
 * MIT License
 *
 * Copyright (c) 2022 DerEchtePilz
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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

    public static String getLanguages() {
        return languages;
    }
}
