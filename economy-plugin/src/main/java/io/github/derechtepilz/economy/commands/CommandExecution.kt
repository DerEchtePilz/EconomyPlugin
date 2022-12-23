package io.github.derechtepilz.economy.commands

import io.github.derechtepilz.economy.Main
import io.github.derechtepilz.economy.utils.PermissionHandler
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionAttachment
import java.security.Permissions

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
			target.sendMessage("§7You have got the permission §6${permission.name}§7!")
			player.sendMessage("§7You granted §6${target.name} §7the permission §6${permission.name}§7!")
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

		player.sendMessage("§6${target.name} §7has the following permissions:")
		for (permission in permissionAttachment.permissions.keys) {
			player.sendMessage("§6- §7${permission}")
			player.sendMessage("§6\t -> Description: §7${permissions[permission]}")
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
			target.sendMessage("§7The permission §6${permission.name} §7has been removed from you!")
			player.sendMessage("§7You removed the permission §6${permission.name} §7from §6${target.name}§7!")
		}
	}

}