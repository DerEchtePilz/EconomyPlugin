package me.derechtepilz.economy.utility.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

class SaveConfig {
    public SaveConfig(String configValues) {
        try {
            File config = new File(new File("./plugins/Economy"), "config.json");
            if (!config.exists()) {
                new LoadConfig().getConfigValues(false);
            }
            Writer writer = new FileWriter(config);
            writer.write(configValues);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
