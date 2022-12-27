package io.github.derechtepilz.economy.utils

import io.github.derechtepilz.economy.Main
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.permissions.Permission

class PermissionHandler(private val main: Main) {

	fun getEconomyPermissions(): MutableList<Permission> {
		return main.description.permissions
	}

}

fun CommandSender.hasAnyPermission(vararg permissions: Permission): Boolean {
	for (permission in permissions) {
		if (hasPermission(permission)) {
			return true
		}
	}
	return false
}