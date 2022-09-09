package me.derechtepilz.economy.updatemanagement

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import me.derechtepilz.economy.utility.APIRequest

class UpdateChecker {
    private val currentPluginVersion: String = "v2.0.0"

    fun isUpdateAvailable(): Boolean {
        val apiResponse: String = APIRequest("https://api.github.com/repos/DerEchtePilz/EconomyPlugin/releases").request()
        val checkVersionUpdateArray: JsonArray = JsonParser.parseString(apiResponse).asJsonArray
        val checkVersionUpdateObject: JsonObject = checkVersionUpdateArray.get(0).asJsonObject

        val currentReleaseVersion: String = checkVersionUpdateObject.get("tag_name").asString
        val isPreRelease: Boolean = checkVersionUpdateObject.get("prerelease").asBoolean

        return !isPreRelease && isNewUpdate(currentReleaseVersion)
    }

    private fun isNewUpdate(currentReleaseVersion: String): Boolean {
        val pluginVersionArray: Array<String> = currentPluginVersion.replace("v", "").split(".").toTypedArray()
        val releaseVersionArray: Array<String> = currentReleaseVersion.replace("v", "").split(".").toTypedArray()
        if (pluginVersionArray[0].toInt() < releaseVersionArray[0].toInt()) {
            return true
        }
        if (pluginVersionArray[1].toInt() < releaseVersionArray[1].toInt()) {
            return true
        }
        return pluginVersionArray[2].toInt() < releaseVersionArray[2].toInt()
    }

}