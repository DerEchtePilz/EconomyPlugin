package io.github.derechtepilz.economy

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIConfig
import io.github.derechtepilz.database.Database
import io.github.derechtepilz.economy.updatemanagement.UpdateChecker
import io.github.derechtepilz.economy.updatemanagement.UpdateDownload
import io.github.derechtepilz.economy.updatemanagement.UpdateInformation
import io.github.derechtepilz.economycore.EconomyAPI
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.fusesource.jansi.Ansi
import java.util.*
import java.util.logging.Logger

class Main : JavaPlugin() {
	private val isDevelopment = true
	private var isVersionSupported = false
	private val main = this
	private var updatedPluginName = ""
	private val inventoryManagementTaskId = 0
	private val coinDisplayTaskId = 0
	private var logger: Logger? = null

	override fun getLogger(): Logger {
		if (logger == null) {
			logger = EconomyPluginLogger()
		}
		return logger!!
	}

	// Update stuff
	private var shouldRegisterUpdateInformation = false
	private var isNewUpdateAvailable = false
	private val updateDownload = UpdateDownload(main)
	private val updateChecker = UpdateChecker(main)

	// Database stuff (if I need it)
	private var database: Database? = null

	override fun onEnable() {
		if (updatedPluginName != "") {
			updateDownload.enablePlugin(updatedPluginName)
		}
		if (updateChecker.isLatestVersion() && updateChecker.isPreviousVersionPresent()) {
			updateDownload.disableAndDeleteOutdatedPlugin()
		}
		if (!isNewUpdateAvailable) {
			database = EconomyAPI.onEnable(main)

			if (isDevelopment) {
			}

			if (isVersionSupported) {
				CommandAPI.onEnable(main)
				commandRegistration()

			}
			listenerRegistration()

			getLogger().info(Ansi.ansi().fgGreen().a("You are on version ").fgYellow().a("v" + description.version).toString())
		}
	}

	override fun onLoad() {
		isNewUpdateAvailable = UpdateChecker(main).isUpdateAvailable()
		val isDirectDownload = false
		if (isNewUpdateAvailable && !isDirectDownload) {
			getLogger().info(Ansi.ansi().fgYellow().a("There is a new update available for the EconomyPlugin! Please download the latest version at https:/github.com/DerEchtePilz/EconomyPlugin/releases/latest").toString())
			shouldRegisterUpdateInformation = true
		}
		if (isNewUpdateAvailable && isDirectDownload) {
			getLogger().info(Ansi.ansi().fgYellow().a("A new update is available for the EconomyPlugin! Downloading now...").toString())
			updatedPluginName = updateDownload.downloadUpdate()
		}
		if (!isNewUpdateAvailable) {
			EconomyAPI.onLoad()
			val version = Bukkit.getBukkitVersion().split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
			isVersionSupported = VersionHandler.isVersionSupported(version)
			if (isVersionSupported) {
				CommandAPI.onLoad(CommandAPIConfig().missingExecutorImplementationMessage("You cannot execute this command!"))
			}
		}
	}

	override fun onDisable() {
		if (!isNewUpdateAvailable) {
			Bukkit.getScheduler().cancelTask(inventoryManagementTaskId)
			Bukkit.getScheduler().cancelTask(coinDisplayTaskId)
			EconomyAPI.onDisable()
			CommandAPI.onDisable()
		}
	}

	private fun commandRegistration() {
		if (isDevelopment) {
		}
	}

	private fun listenerRegistration() {
		val manager = Bukkit.getPluginManager()
		if (shouldRegisterUpdateInformation) {
			manager.registerEvents(UpdateInformation(main), this)
			shouldRegisterUpdateInformation = false
		}
		if (isDevelopment) {
		}
	}
}