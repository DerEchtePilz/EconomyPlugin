package me.derechtepilz.economy.utility;

import com.google.gson.*;

import java.io.IOException;

public class CheckUpdate {

    private static final String pluginVersion = "v2.0.0";

    private CheckUpdate() {

    }

    public static boolean checkForUpdate() {
        try {
            String apiResponse = new APIRequest().request("https://api.github.com/repos/DerEchtePilz/EconomyPlugin/releases");
            JsonArray checkVersionUpdateArray = JsonParser.parseString(apiResponse).getAsJsonArray();
            JsonObject checkVersionUpdateObject = checkVersionUpdateArray.get(0).getAsJsonObject();

            String pluginVersion = checkVersionUpdateObject.get("tag_name").getAsString();
            boolean isPreRelease = checkVersionUpdateObject.get("prerelease").getAsBoolean();

            return !isPreRelease && !pluginVersion.equals(CheckUpdate.pluginVersion);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
