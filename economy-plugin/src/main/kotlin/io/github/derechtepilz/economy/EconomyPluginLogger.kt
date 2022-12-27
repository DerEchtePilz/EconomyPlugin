package io.github.derechtepilz.economy

import org.bukkit.Bukkit
import java.util.logging.Level
import java.util.logging.Logger

@Suppress("ProtectedInFinal")
internal class EconomyPluginLogger() : Logger("EconomyPlugin", null) {
	init {
		parent = Bukkit.getLogger().parent
		level = Level.ALL
	}
}