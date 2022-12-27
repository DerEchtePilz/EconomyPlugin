package io.github.derechtepilz.economy.utils

import io.github.derechtepilz.economy.Main
import org.bukkit.permissions.Permission

class PermissionHandler(private val main: Main) {

	fun getEconomyPermissions(): MutableList<Permission> {
		return main.description.permissions
	}

}