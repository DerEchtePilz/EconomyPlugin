package me.derechtepilz.economy.utility;

import com.google.gson.*;

import java.io.IOException;

public class CheckUpdate {

    private final String currentPluginVersion = "v2.0.0";

    public boolean checkForUpdate() {
        try {
            String apiResponse = new APIRequest().request("https://api.github.com/repos/DerEchtePilz/EconomyPlugin/releases");
            JsonArray checkVersionUpdateArray = JsonParser.parseString(apiResponse).getAsJsonArray();
            JsonObject checkVersionUpdateObject = checkVersionUpdateArray.get(0).getAsJsonObject();

            String currentReleaseVersion = checkVersionUpdateObject.get("tag_name").getAsString(); // this is one version behind currentPluginVersion
            boolean isPreRelease = checkVersionUpdateObject.get("prerelease").getAsBoolean();

            return !isPreRelease && !currentReleaseVersion.equals(this.currentPluginVersion);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
