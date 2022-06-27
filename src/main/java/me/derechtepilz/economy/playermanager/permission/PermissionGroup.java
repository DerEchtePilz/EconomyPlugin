package me.derechtepilz.economy.playermanager.permission;

import me.derechtepilz.economy.utility.NamespacedKeys;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public enum PermissionGroup {
    COINS("coins", new Permission[]{Permission.GIVE_COINS, Permission.SET_COINS, Permission.TAKE_COINS}),
    OFFER("offer", new Permission[]{Permission.BUY_OFFER, Permission.CANCEL_OFFER, Permission.CREATE_OFFER}),
    CONFIG("config", new Permission[]{Permission.MODIFY_CONFIG, Permission.RESET_CONFIG, Permission.DELETE_CONFIG}),
    MISCELLANEOUS("miscellaneous", new Permission[]{Permission.TRADE, Permission.FRIEND}),
    DISCORD("discord", new Permission[]{Permission.DISCORD_MESSAGE_USER, Permission.DISCORD_SEARCH_ID});

    private final Permission[] permissionGroup;
    private final String groupName;
    PermissionGroup(String groupName, Permission[] permissionGroup) {
        this.permissionGroup = permissionGroup;
        this.groupName = groupName;
    }

    public Permission[] getPermissionGroup() {
        return permissionGroup;
    }

    public String getGroupName() {
        return groupName;
    }

    public List<Permission> getPermissionGroupAsList() {
        return List.of(permissionGroup);
    }

    public static void addPermissionGroup(Player player, PermissionGroup permissionGroup) {
        for (Permission permission : permissionGroup.getPermissionGroupAsList()) {
            if (Permission.hasPermission(player, permission)) {
                continue;
            }
            Permission.addPermission(player, permission);
        }
    }

    public static void removePermissionGroup(Player player, PermissionGroup permissionGroup) {
        for (Permission permission : permissionGroup.getPermissionGroupAsList()) {
            if (!Permission.hasPermission(player, permission)) {
                continue;
            }
            Permission.removePermission(player, permission);
        }
    }

    public static boolean hasPermissionGroup(Player player, PermissionGroup permissionGroup) {
        int[] playerPermissions = player.getPersistentDataContainer().get(NamespacedKeys.PERMISSION.getKey(), PersistentDataType.INTEGER_ARRAY);
        int checkedMatchingPermissions = 0;
        for (int permissionId : playerPermissions) {
            for (Permission permission : permissionGroup.getPermissionGroup()) {
                if (permission.getId() == permissionId) {
                    checkedMatchingPermissions += 1;
                }
            }
        }
        return checkedMatchingPermissions == permissionGroup.getPermissionGroupAsList().size();
    }
}
