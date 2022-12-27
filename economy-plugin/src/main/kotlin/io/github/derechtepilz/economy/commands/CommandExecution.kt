package io.github.derechtepilz.economy.commands

import io.github.derechtepilz.economy.Main
import io.github.derechtepilz.economy.utils.PermissionHandler
import io.github.derechtepilz.economy.utils.TranslatableComponent
import io.github.derechtepilz.economy.utils.sendMessage
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionAttachment

class CommandExecution(private val main: Main) {

	@Suppress("UNCHECKED_CAST")
	fun setPermission(sender: CommandSender, args: Array<Any>) {
		val player: Player = sender as Player
		val target: Player = args[0] as Player
		val stringPermissions: MutableList<String> = args[1] as MutableList<String>

		val permissions: MutableList<Permission> = mutableListOf()
		val economyPermissions: MutableList<Permission> = PermissionHandler(main).getEconomyPermissions()
		for (i in 0 until economyPermissions.size) {
			val permission: Permission = economyPermissions[i]
			if (stringPermissions.contains(permission.name)) {
				permissions.add(permission)
			}
		}

		val permissionAttachment: PermissionAttachment = main.permissions[target.uniqueId]!!
		for (permission in permissions) {
			if (target.isPermissionSet(permission)) {
				continue
			}
			permissionAttachment.setPermission(permission, true)
			target.sendMessage(TranslatableComponent("command_execution.set_permission.target", permission.name))
			player.sendMessage(TranslatableComponent("command_execution.set_permission.player", target.name, permission.name))
		}
	}

	fun getPermission(sender: CommandSender, args: Array<Any>) {
		val player: Player = sender as Player
		val target: Player = args[0] as Player

		val permissionAttachment: PermissionAttachment = main.permissions[target.uniqueId]!!

		val economyPermissions: MutableList<Permission> = PermissionHandler(main).getEconomyPermissions()

		val permissions: MutableMap<String, String> = mutableMapOf()

		for (permission in economyPermissions) {
			permissions[permission.name] = permission.description
		}

		player.sendMessage(TranslatableComponent("command_execution.get_permission.intro", target.name))
		for (permission in permissionAttachment.permissions.keys) {
			player.sendMessage(TranslatableComponent("command_execution.get_permission.permission", permission))
			player.sendMessage(TranslatableComponent("command_execution.get_permission.description", permissions[permission]!!))
		}
	}

	@Suppress("UNCHECKED_CAST")
	fun removePermission(sender: CommandSender, args: Array<Any>) {
		val player: Player = sender as Player
		val target: Player = args[0] as Player
		val stringPermissions: MutableList<String> = args[1] as MutableList<String>

		val permissions: MutableList<Permission> = mutableListOf()
		val economyPermissions: MutableList<Permission> = PermissionHandler(main).getEconomyPermissions()
		for (i in 0 until economyPermissions.size) {
			val permission: Permission = economyPermissions[i]
			if (stringPermissions.contains(permission.name)) {
				permissions.add(permission)
			}
		}

		val permissionAttachment: PermissionAttachment = main.permissions[target.uniqueId]!!
		for (permission in permissions) {
			if (!target.isPermissionSet(permission)) {
				continue
			}
			permissionAttachment.unsetPermission(permission)
			target.sendMessage(TranslatableComponent("command_execution.remove_permission.target", permission.name))
			player.sendMessage(TranslatableComponent("command_execution.remove_permission.player", permission.name, target.name))
		}
	}

}