package io.github.derechtepilz.economy.updatemanagement

import com.google.gson.JsonArray
import com.google.gson.JsonParser
import io.github.derechtepilz.economy.Main
import io.github.derechtepilz.economy.utility.APIRequest
import org.bukkit.Bukkit
import org.bukkit.plugin.PluginDescriptionFile
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

class UpdateDownload(private val main: Main) {
    private val checkUpdate: UpdateChecker = UpdateChecker(main)

    fun downloadUpdate(): String {
        if (!checkUpdate.isUpdateAvailable()) {
            return ""
        }
        val releases: JsonArray = JsonParser.parseString(APIRequest("https://api.github.com/repos/DerEchtePilz/EconomyPlugin/releases").request()).asJsonArray
        val releaseName: String = releases.get(0).asJsonObject.get("assets").asJsonArray.get(0).asJsonObject["name"].asString
        val releaseTag: String = releases.get(0).asJsonObject.get("tag_name").asString
        val directDownloadLatestRelease = "https://github.com/DerEchtePilz/EconomyPlugin/releases/download/$releaseTag/$releaseName"
        URL(directDownloadLatestRelease).openStream().use { stream -> Files.copy(stream, Paths.get("./plugins/$releaseName"), StandardCopyOption.REPLACE_EXISTING) }
        return releaseName
    }

    fun enablePlugin(pluginName: String) {
        val pluginFile = File(main.server.worldContainer.absolutePath + "/plugins/$pluginName")
        val plugin = Bukkit.getPluginManager().loadPlugin(pluginFile)
        if (plugin != null) {
            plugin.onLoad()
            Bukkit.getPluginManager().enablePlugin(plugin)
        }
    }

    fun disableAndDeleteOutdatedPlugin() {
        val releases: JsonArray = JsonParser.parseString(APIRequest("https://api.github.com/repos/DerEchtePilz/EconomyPlugin/releases").request()).asJsonArray
        val previousReleaseTag: String = releases.get(1).asJsonObject.get("tag_name").asString.replace("v", "")

        for (plugin in Bukkit.getPluginManager().plugins) {
            val descriptionFile: PluginDescriptionFile = plugin.description
            val pluginName: String = descriptionFile.name
            val pluginVersion: String = descriptionFile.version
            val pluginAuthor: String = descriptionFile.authors[0]
            if (pluginName == "Economy" && pluginAuthor == "DerEchtePilz") {
                if (pluginVersion == previousReleaseTag) {
                    plugin.onDisable()
                    Bukkit.getPluginManager().disablePlugin(plugin)
                    main.logger.info("Disabling outdated plugin: EconomyPlugin-$previousReleaseTag.jar")
                    val pluginFile = File("./plugins/EconomyPlugin-$previousReleaseTag.jar")
                    if (!pluginFile.exists()) {
                        main.logger.warning("Cannot delete plugin $pluginName with version $pluginVersion because the file EconomyPlugin-$previousReleaseTag.jar does not exist but should!")
                        main.logger.warning("You should be able to delete the old version though!")
                        return
                    }
                    pluginFile.delete()
                }
            }
        }
    }

}