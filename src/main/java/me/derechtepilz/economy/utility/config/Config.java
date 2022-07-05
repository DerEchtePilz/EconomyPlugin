package me.derechtepilz.economy.utility.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.utility.TranslatableChatComponent;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Config {

    private static Map<String, String> config = new HashMap<>();
    private static Map<String, String> defaultConfigValues = new HashMap<>();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final File configFile = new File(new File("./plugins/Economy"), "config.json");

    private static boolean isLoaded = false;

    private Config() {

    }

    @SuppressWarnings("unchecked")
    public static void loadConfig() throws FileNotFoundException {
        if (isLoaded) {
            Main.getInstance().getLogger().severe(TranslatableChatComponent.read("config.loadConfig.is_loaded"));
            return;
        }
        if (!configFile.exists()) {
            resetConfig();
        }
        config = gson.fromJson(new FileReader(configFile), HashMap.class);
        defaultConfigValues = gson.fromJson(getValuesFromDefaultConfig(), HashMap.class);
        checkConfigValues(config, defaultConfigValues);
        saveConfig();
        isLoaded = true;
    }

    public static String get(String value) {
        return config.get(value);
    }

    public static void set(String key, String value) {
        config.put(key, value);
        saveConfig();
    }

    public static void saveConfig() {
        save(gson.toJson(config));
    }

    public static boolean contains(String value) {
        return config.containsKey(value);
    }

    @SuppressWarnings("unchecked")
    public static void reloadConfig() {
        try {
            config = gson.fromJson(new FileReader(configFile), HashMap.class);
        } catch (IOException exception) {
            IOException ioException = new IOException("Failed to reload config!");
            Main.getInstance().getLogger().severe(ioException.getMessage());
        }
    }

    public static void resetConfig() {
        save(getValuesFromDefaultConfig());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void deleteConfig() {
        configFile.delete();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void save(String configValues) {
        try {
            File config = new File(new File("./plugins/Economy"), "config.json");
            if (!config.exists()) {
                config.createNewFile();
            }
            Writer writer = new FileWriter(config);
            writer.write(configValues);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getValuesFromDefaultConfig() {
        InputStream config = Main.getInstance().getResource("config.json");
        BufferedReader reader = new BufferedReader(new InputStreamReader(config));
        StringBuilder builder = new StringBuilder();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException exception) {
            IOException ioException = new IOException("Failed to load default config!");
            Main.getInstance().getLogger().severe(ioException.getMessage());
            Main.getInstance().getLogger().severe(Arrays.toString(ioException.getStackTrace()));
        }
        return builder.toString();
    }

    private static void checkConfigValues(Map<String, String> config, Map<String, String> defaultConfig) {
        if (config.size() == defaultConfig.size()) {
            return;
        }
        if (config.size() < defaultConfig.size()) {
            for (String key : defaultConfig.keySet()) {
                if (config.containsValue(key)) {
                    continue;
                }
                config.put(key, defaultConfig.get(key));
            }
        } else {
            for (String key : config.keySet()) {
                if (defaultConfig.containsKey(key)) {
                    continue;
                }
                config.remove(key);
            }
        }
        Config.config = config;
        Config.defaultConfigValues = defaultConfig;
    }
}