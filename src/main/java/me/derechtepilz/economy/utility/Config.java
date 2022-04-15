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

import me.derechtepilz.economy.Main;

import java.util.Arrays;
import java.util.List;

public class Config {

    private static int itemQuantitiesMinAmount = 1;
    private static int itemQuantitiesMaxAmount = 10;
    private static int itemPriceMinAmount = 50;
    private static int itemPriceMaxAmount = 5000;
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

            set("language", language);

            Main.getInstance().getConfig().setComments("itemQuantities", Arrays.asList(
                    "---------- CONFIG ----------",
                    "Please do only edit this file if you know what you are doing",
                    "Everything is configurable with an in-game command",
                    null,
                    "Saves the amounts of items that can be sold at once"
            ));
            Main.getInstance().getConfig().setComments("itemPrice", List.of(
                    "Saves the minimum and maximum amount items can be sold for"
            ));
            Main.getInstance().getConfig().setComments("language", List.of(
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

    public static void saveConfig() {
        Main.getInstance().saveConfig();
    }

    public static void reloadConfig() {
        Main.getInstance().reloadConfig();
    }

}
