package me.derechtepilz.economy.updatemanagement

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import me.derechtepilz.economy.Main
import me.derechtepilz.economy.utility.APIRequest
import java.io.File

class UpdateChecker(private val main: Main) {
	private val currentPluginVersion: String = "v${main.description.version}"

	fun isUpdateAvailable(): Boolean {
		val apiResponse: String = APIRequest("https://api.github.com/repos/DerEchtePilz/EconomyPlugin/releases").request()
		val checkVersionUpdateArray: JsonArray = JsonParser.parseString(apiResponse).asJsonArray
		val checkVersionUpdateObject: JsonObject = checkVersionUpdateArray.get(0).asJsonObject

		val currentReleaseVersion: String = checkVersionUpdateObject.get("tag_name").asString
		val isPreRelease: Boolean = checkVersionUpdateObject.get("prerelease").asBoolean

		return !isPreRelease && isNewUpdate(currentReleaseVersion)
	}

	fun isLatestVersion(): Boolean {
		val apiResponse: String = APIRequest("https://api.github.com/repos/DerEchtePilz/EconomyPlugin/releases").request()
		val checkVersionUpdateArray: JsonArray = JsonParser.parseString(apiResponse).asJsonArray
		val checkVersionUpdateObject: JsonObject = checkVersionUpdateArray.get(0).asJsonObject

		val currentReleaseVersion: String = checkVersionUpdateObject.get("tag_name").asString
		val isPreRelease: Boolean = checkVersionUpdateObject.get("prerelease").asBoolean

		return !isPreRelease && (currentReleaseVersion == currentPluginVersion)
	}

	fun isPreviousVersionPresent(): Boolean {
		val apiResponse: String = APIRequest("https://api.github.com/repos/DerEchtePilz/EconomyPlugin/releases").request()
		val checkVersionUpdateArray: JsonArray = JsonParser.parseString(apiResponse).asJsonArray
		val checkVersionUpdateObject: JsonObject = checkVersionUpdateArray.get(0).asJsonObject

		val releaseName: String = checkVersionUpdateArray.get(1).asJsonObject.get("assets").asJsonArray.get(0).asJsonObject["name"].asString
		val isPreRelease: Boolean = checkVersionUpdateObject.get("prerelease").asBoolean

		val previousVersion = File(main.server.worldContainer.absolutePath + "/plugins/$releaseName")

		return !isPreRelease && previousVersion.exists()
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