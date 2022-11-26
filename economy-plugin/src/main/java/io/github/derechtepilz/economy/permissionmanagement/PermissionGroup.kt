package io.github.derechtepilz.economy.permissionmanagement

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.bukkit.entity.Player
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

object PermissionGroup {

	private val permissionGroupFile: File = File("./plugins/Economy/permissionGroups.bin")
	private val permissionGroups: MutableMap<String, MutableList<String>> = mutableMapOf()

	fun savePermissionGroups() {
		if (!permissionGroupFile.exists()) {
			permissionGroupFile.createNewFile()
		}
		val jsonObject = JsonObject()
		for (permissionGroup in permissionGroups.keys) {
			val permissions: MutableList<String> = permissionGroups[permissionGroup]!!
			val jsonArray = JsonArray()
			for (permission in permissions) {
				jsonArray.add(permission)
			}
			jsonObject.add(permissionGroup, jsonArray)
		}
		val permissionGroups = Gson().toJson(jsonObject)
		val fileOutputStream = FileOutputStream(permissionGroupFile)
		val objectOutputStream = ObjectOutputStream(fileOutputStream)
		objectOutputStream.writeObject(permissionGroups)
		objectOutputStream.close()
	}

	fun loadPermissionGroups() {
		if (!permissionGroupFile.exists()) {
			permissionGroupFile.createNewFile()
			return
		}
		val fileInputStream = FileInputStream(permissionGroupFile)
		val objectInputStream = ObjectInputStream(fileInputStream)
		val savedPermissionGroups: String = objectInputStream.readObject() as String

		val jsonObject = JsonParser.parseString(savedPermissionGroups).asJsonObject
		for (permissionGroup in jsonObject.keySet()) {
			val jsonArray = jsonObject.get(permissionGroup).asJsonArray
			val permissions: MutableList<String> = mutableListOf()
			for (i in 0 until jsonArray.size()) {
				permissions.add(jsonArray[i].asString)
			}
			permissionGroups[permissionGroup] = permissions
		}
	}

	fun registerPermissionGroup(groupName: String, permissions: MutableList<String>) {
		if (exists(groupName.lowercase())) {
			return
		}
		permissionGroups[groupName.lowercase()] = permissions
	}

	fun deletePermissionGroup(groupName: String) {
		if (!exists(groupName.lowercase())) {
			return
		}
		permissionGroups.remove(groupName.lowercase())
	}

	fun exists(groupName: String): Boolean {
		return permissionGroups.containsKey(groupName.lowercase())
	}

	fun getPermissionGroups(): MutableList<String> {
		val permissions: MutableList<String> = mutableListOf()
		for (permissionGroup in permissionGroups.keys) {
			permissions.add(permissionGroup)
		}
		return permissions
	}

	fun hasPermissionGroup(player: Player, permissionGroup: String): Boolean {
		var hasPermissionGroup = true
		val playerPermissions = Permission.getPermissions(player)
		val permissionGroupPermissions = permissionGroups[permissionGroup]!!
		for (permission in permissionGroupPermissions) {
			if (!playerPermissions.contains(permission)) {
				hasPermissionGroup = false
			}
		}
		return hasPermissionGroup
	}

	fun setPermissionGroup(player: Player, permissionGroup: String) {
		val permissionGroupPermissions = permissionGroups[permissionGroup]!!
		for (permission in permissionGroupPermissions) {
			Permission.addPermission(player, Permission.valueOf(permission))
		}
	}

	fun removePermissionGroup(player: Player, permissionGroup: String) {
		val permissionGroupPermissions = permissionGroups[permissionGroup]!!
		for (permission in permissionGroupPermissions) {
			Permission.removePermission(player, Permission.valueOf(permission))
		}
	}

}