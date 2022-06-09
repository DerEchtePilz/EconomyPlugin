package me.derechtepilz.economy.utility.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.derechtepilz.economy.Main;

import java.io.*;

class LoadConfig {
    private final String itemQuantitiesMinAmount = "1";
    private final String itemQuantitiesMaxAmount = "10";
    private final String itemPriceMinAmount = "50";
    private final String itemPriceMaxAmount = "5000";
    private final String startBalance = "50.0";
    private final String interest = "1.0";
    private final String language = "EN_US";

    static String configBackup = "";

    public void getConfigValues(boolean overwrite) {
        try {
            File config = new File(new File("./plugins/Economy"), "config.json");
            if (!config.exists()) {
                save(config);
            }
            if (overwrite) {
                save(config);
            }
            String configValues = load(config);
            configBackup = configValues;
            Config.configValues = configValues;
            updateJsonBuilderValues(configBackup);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static boolean equalValues(String value, String jsonPrimitive) {
        return value.equals(JsonParser.parseString(configBackup).getAsJsonObject().getAsJsonPrimitive(jsonPrimitive).getAsString());
    }

    public static boolean equalValues(String value, String jsonPrimitive, String jsonObject) {
        return value.equals(JsonParser.parseString(configBackup).getAsJsonObject().getAsJsonObject(jsonObject).getAsJsonPrimitive(jsonPrimitive).getAsString());
    }

    private String load(File config) throws IOException {
        FileReader fileReader = new FileReader(config);
        BufferedReader reader = new BufferedReader(fileReader);
        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        return builder.toString();
    }

    private void updateJsonBuilderValues(String configValues) {
        JsonObject object = JsonParser.parseString(configValues).getAsJsonObject();
        Main.getInstance().getJsonBuilder()
                .setItemQuantitiesMinAmount(object.getAsJsonObject("itemQuantities").getAsJsonPrimitive("minAmount").getAsString())
                .setItemQuantitiesMaxAmount(object.getAsJsonObject("itemQuantities").getAsJsonPrimitive("maxAmount").getAsString())
                .setItemPriceMinAmount(object.getAsJsonObject("itemPrice").getAsJsonPrimitive("minAmount").getAsString())
                .setItemPriceMaxAmount(object.getAsJsonObject("itemPrice").getAsJsonPrimitive("maxAmount").getAsString())
                .setStartBalance(object.getAsJsonPrimitive("startBalance").getAsString())
                .setInterest(object.getAsJsonPrimitive("interest").getAsString())
                .setLanguage(object.getAsJsonPrimitive("language").getAsString())
                .buildJson();
    }

    private void save(File config) throws IOException {
        config.createNewFile();
        Writer writer = new FileWriter(config);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
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

        writer.write(gson.toJson(configValues));
        writer.close();
    }
}
