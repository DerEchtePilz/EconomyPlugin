package io.github.derechtepilz.economy

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIConfig
import io.github.derechtepilz.database.Database
import io.github.derechtepilz.economy.commands.*
import io.github.derechtepilz.economy.updatemanagement.UpdateChecker
import io.github.derechtepilz.economy.updatemanagement.UpdateDownload
import io.github.derechtepilz.economy.updatemanagement.UpdateInformation
import io.github.derechtepilz.economy.utils.Config
import io.github.derechtepilz.economy.utils.LanguageManager
import io.github.derechtepilz.economy.utils.SuggestionProvider
import io.github.derechtepilz.economy.utils.TranslatableComponent
import io.github.derechtepilz.economycore.EconomyAPI
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.permissions.PermissionAttachment
import org.bukkit.plugin.java.JavaPlugin
import org.fusesource.jansi.Ansi
import java.util.*
import java.util.logging.Logger

class Main : JavaPlugin() {

	private val isDevelopment = true
	private var isVersionSupported = false

	companion object {
		@JvmStatic
		lateinit var main: Main
	}

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

	lateinit var languageManager: LanguageManager
	lateinit var language: LanguageManager.Language
	lateinit var config: Config

	// Permissions
	val permissions: MutableMap<UUID, PermissionAttachment> = mutableMapOf()

	// Commands
	lateinit var suggestionProvider: SuggestionProvider
	lateinit var commandExecution: CommandExecution

	private lateinit var auctionCommand: AuctionCommand
	private lateinit var balanceCommand: BalanceCommand
	private lateinit var friendCommand: FriendCommand
	private lateinit var permissionCommand: PermissionCommand

	// Update stuff
	private var shouldRegisterUpdateInformation = false
	private var isNewUpdateAvailable = false
	private lateinit var updateDownload: UpdateDownload
	private lateinit var updateChecker: UpdateChecker

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

			val setupPlayer: Listener = object : Listener {
				@EventHandler
				fun onJoin(event: PlayerJoinEvent) {
					val player: Player = event.player
					permissions[player.uniqueId] = player.addAttachment(main)
				}
			}
			Bukkit.getPluginManager().registerEvents(setupPlayer, this)

			getLogger().info(Ansi.ansi().fgGreen().a("You are on version ").fgYellow().a("v" + description.version).toString())
		}
	}

	override fun onLoad() {
		main = this

		config = Config(main)
		languageManager = LanguageManager(main)

		languageManager.init()
		config.loadConfig()
		language = LanguageManager.Language.valueOf(config.get("language")!!)

		suggestionProvider = SuggestionProvider(main)
		commandExecution = CommandExecution(main)

		auctionCommand = AuctionCommand(main)
		balanceCommand = BalanceCommand(main)
		friendCommand = FriendCommand(main)
		permissionCommand = PermissionCommand(main)

		updateDownload = UpdateDownload(main)
		updateChecker = UpdateChecker(main)

		isNewUpdateAvailable = updateChecker.isUpdateAvailable()
		val isDirectDownload = java.lang.Boolean.parseBoolean(config.get("allowDirectDownloads"))
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
				CommandAPI.onLoad(CommandAPIConfig().missingExecutorImplementationMessage(TranslatableComponent("main.on_load.command_missing_executor").toMessage()))
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
		auctionCommand.register()
		balanceCommand.register()
		friendCommand.register()
		permissionCommand.register()
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