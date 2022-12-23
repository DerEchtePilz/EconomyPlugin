package io.github.derechtepilz.economy.utils

import io.github.derechtepilz.economy.Main
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.permissions.Permission

class SuggestionProvider(private val main: Main) {

	fun provideSuggestions(suggestionType: SuggestionType): Array<String> {
		return when (suggestionType) {
			SuggestionType.PERMISSION -> {
				val permissions: MutableList<Permission> = main.description.permissions
				val economyPermissions: MutableList<String> = mutableListOf()
				permissions.forEach { permission ->
					if (permission.name.startsWith("economy.")) {
						economyPermissions.add(permission.name)
					}
				}
				economyPermissions.toTypedArray()
			}
			SuggestionType.PLAYER -> {
				Bukkit.getOnlinePlayers().stream().map { player: Player -> player.name }.toList().toTypedArray()
			}
		}
	}

	enum class SuggestionType {
		PERMISSION,
		PLAYER
	}

}