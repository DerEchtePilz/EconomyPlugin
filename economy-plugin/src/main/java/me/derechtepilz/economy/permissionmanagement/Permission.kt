package me.derechtepilz.economy.permissionmanagement

import me.derechtepilz.economy.utility.NamespacedKeys
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType


enum class Permission(private val permission: String, private val id: Int) {
    GIVE_COINS("give_coins", 0),
    SET_COINS("set_coins", 1),
    TAKE_COINS("take_coins", 2),
    MODIFY_CONFIG("modify_config", 3),
    RESET_CONFIG("reset_config", 4),
    PAUSE_RESUME_AUCTIONS("pause_resume_auctions", 5);

    fun getPermission(): String {
        return name
    }

    fun getId(): Int {
        return id;
    }

    companion object {
        @JvmStatic
        fun clearPermissions(player: Player) {
            if (player.persistentDataContainer.has(NamespacedKeys.PERMISSION, PersistentDataType.INTEGER_ARRAY)) {
                player.persistentDataContainer.remove(NamespacedKeys.PERMISSION)
            }
        }

        @JvmStatic
        fun hasPermission(player: Player, permission: Permission): Boolean {
            if (player.persistentDataContainer.has(NamespacedKeys.PERMISSION, PersistentDataType.INTEGER_ARRAY)) {
                val permissions: IntArray? =
                    player.persistentDataContainer.get(NamespacedKeys.PERMISSION, PersistentDataType.INTEGER_ARRAY)
                for (permissionCode: Int in permissions!!) {
                    if (permissionCode == permission.getId()) {
                        return true;
                    }
                }
            }
            return false;
        }

        @JvmStatic
        fun addPermission(player: Player, permission: Permission) {
            val permissions: MutableList<Int> = ArrayList()
            val playerPermissions =
                if (player.persistentDataContainer.has(NamespacedKeys.PERMISSION, PersistentDataType.INTEGER_ARRAY)) {
                    player.persistentDataContainer.get(NamespacedKeys.PERMISSION, PersistentDataType.INTEGER_ARRAY)
                } else IntArray(0)
            for (permissionId: Int in playerPermissions!!) {
                permissions.add(permissionId)
            }
            if (!permissions.contains(permission.getId())) {
                permissions.add(permission.getId())
            }
            val updatedPermissions = IntArray(permissions.size)
            for (i in permissions.indices) {
                updatedPermissions[i] = permissions[i]
            }
            player.persistentDataContainer.set(
                NamespacedKeys.PERMISSION,
                PersistentDataType.INTEGER_ARRAY,
                updatedPermissions
            )
        }

        @JvmStatic
        fun removePermission(player: Player, permission: Permission) {
            val permissions: MutableList<String> = ArrayList()
            val playerPermissions =
                if (player.persistentDataContainer.has(NamespacedKeys.PERMISSION, PersistentDataType.INTEGER_ARRAY)) {
                    player.persistentDataContainer.get(NamespacedKeys.PERMISSION, PersistentDataType.INTEGER_ARRAY)
                } else IntArray(0)
            for (permissionId in playerPermissions!!) {
                permissions.add(permissionId.toString())
            }
            permissions.remove(permission.getId().toString())
            val updatedPermissions = IntArray(permissions.size)
            for (i in permissions.indices) {
                updatedPermissions[i] = permissions[i].toInt()
            }
            player.persistentDataContainer.set(
                NamespacedKeys.PERMISSION,
                PersistentDataType.INTEGER_ARRAY,
                updatedPermissions
            )
        }

        @JvmStatic
        fun getPermissionFromId(permissionId: Int): Permission? {
            var requestedPermission: Permission? = null
            for (permission in Permission.values()) {
                if (permission.getId() == permissionId) {
                    requestedPermission = permission
                }
            }
            return requestedPermission
        }

        @JvmStatic
        fun getPermissionIdFromName(name: String): Int {
            var requestedPermission = - 1
            for (permission in Permission.values()) {
                if (permission.getPermission() == name) {
                    requestedPermission = permission.getId()
                }
            }
            return requestedPermission;
        }

        @JvmStatic
        fun getPermissions(player: Player): Array<String?> {
            if (!player.persistentDataContainer.has(NamespacedKeys.PERMISSION, PersistentDataType.INTEGER_ARRAY)) {
                return arrayOfNulls(0)
            }
            val permissions = player.persistentDataContainer.get(NamespacedKeys.PERMISSION, PersistentDataType.INTEGER_ARRAY)
            val permissionList = arrayOfNulls<String>(permissions!!.size)
            for (i in permissions.indices) {
                val permissionId: Int = permissions [i]
                for (permission in Permission.values()) {
                    if (permission.getId() == permissionId) {
                        permissionList[i] = permission.getPermission()
                    }
                }
            }
            return permissionList
        }

        @JvmStatic
        fun getPermissions(): List<String> {
            val permissions: MutableList<String> = ArrayList()
            for (permission in Permission.values()) {
                permissions.add(permission.getPermission().lowercase())
            }
            return permissions
        }
    }

}