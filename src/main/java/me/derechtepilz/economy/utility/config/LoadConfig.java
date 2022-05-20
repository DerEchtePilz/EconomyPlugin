package me.derechtepilz.economy.utility.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.*;

class LoadConfig {
    public String getConfigValues(boolean overwrite) {
        try {
            File config = new File(new File("./plugins/Economy"), "config.json");
            if (!config.exists()) {
                save(config);
            }
            if (overwrite) {
                save(config);
            }
            return load(config);
        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
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

    private void save(File config) throws IOException {
        config.createNewFile();
        Writer writer = new FileWriter(config);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject configValues = new JsonObject();
        JsonObject itemQuantities = new JsonObject();
        JsonObject itemPrice = new JsonObject();

        itemQuantities.addProperty("minAmount", Config.itemQuantitiesMinAmount);
        itemQuantities.addProperty("maxAmount", Config.itemQuantitiesMaxAmount);
        itemPrice.addProperty("minAmount", Config.itemPriceMinAmount);
        itemPrice.addProperty("maxAmount", Config.itemPriceMaxAmount);

        configValues.add("itemQuantities", itemQuantities);
        configValues.add("itemPrice", itemPrice);
        configValues.addProperty("startBalance", Config.startBalance);
        configValues.addProperty("interest", Config.interest);
        configValues.addProperty("language", Config.language);

        writer.write(gson.toJson(configValues));
        writer.close();
    }
}
