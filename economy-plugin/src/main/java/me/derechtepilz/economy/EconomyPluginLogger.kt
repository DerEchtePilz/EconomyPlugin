package me.derechtepilz.economy

import org.bukkit.Bukkit
import java.util.logging.Level
import java.util.logging.Logger

@Suppress("ProtectedInFinal")
internal class EconomyPluginLogger protected constructor() : Logger("EconomyPlugin", null) {
	init {
		parent = Bukkit.getLogger().parent
		level = Level.ALL
	}
}