package me.derechtepilz.economy.playermanager;

import me.derechtepilz.economy.utility.NamespacedKeys;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public enum Permission {
    GIVE_COINS("give_coins", 0),
    SET_COINS("set_coins", 1),
    TAKE_COINS("take_coins", 2),
    BUY_OFFER("buy_offer", 3),
    CANCEL_OFFER("cancel_offer", 4),
    CREATE_OFFER("create_offer", 5),
    MODIFY_CONFIG("modify_config", 6),
    RESET_CONFIG("reset_config", 7),
    DELETE_CONFIG("delete_config", 9),
    TRADE("trade", 8),
    DISCORD_SEARCH_ID("discord_id", 10),
    DISCORD_MESSAGE_USER("discord_message_user", 11);

    private final String name;
    private final int id;
    Permission(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public static boolean hasPermission(Player player, Permission permission) {
        if (player.getPersistentDataContainer().has(NamespacedKeys.PERMISSION.getKey(), PersistentDataType.INTEGER_ARRAY)) {
            int[] permissions = player.getPersistentDataContainer().get(NamespacedKeys.PERMISSION.getKey(), PersistentDataType.INTEGER_ARRAY);
            for (int permissionCode : permissions) {
                if (permissionCode == permission.getId()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void addPermission(Player player, Permission permission) {
        List<Integer> permissions = new ArrayList<>();
        int[] playerPermissions = (player.getPersistentDataContainer().has(NamespacedKeys.PERMISSION.getKey(), PersistentDataType.INTEGER_ARRAY)) ? player.getPersistentDataContainer().get(NamespacedKeys.PERMISSION.getKey(), PersistentDataType.INTEGER_ARRAY) : new int[0];
        for (int permissionId : playerPermissions) {
            permissions.add(permissionId);
        }
        if (!permissions.contains(permission.getId())) {
            permissions.add(permission.getId());
        }
        int[] updatedPermissions = new int[permissions.size()];
        for (int i = 0; i < permissions.size(); i++) {
            updatedPermissions[i] = permissions.get(i);
        }
        player.getPersistentDataContainer().set(NamespacedKeys.PERMISSION.getKey(), PersistentDataType.INTEGER_ARRAY, updatedPermissions);
    }

    public static void removePermission(Player player, Permission permission) {
        List<String> permissions = new ArrayList<>();
        int[] playerPermissions = (player.getPersistentDataContainer().has(NamespacedKeys.PERMISSION.getKey(), PersistentDataType.INTEGER_ARRAY)) ? player.getPersistentDataContainer().get(NamespacedKeys.PERMISSION.getKey(), PersistentDataType.INTEGER_ARRAY) : new int[0];
        for (int permissionId : playerPermissions) {
            permissions.add(String.valueOf(permissionId));
        }
        permissions.remove(String.valueOf(permission.getId()));

        int[] updatedPermissions = new int[permissions.size()];
        for (int i = 0; i < permissions.size(); i++) {
            updatedPermissions[i] = Integer.parseInt(permissions.get(i));
        }
        player.getPersistentDataContainer().set(NamespacedKeys.PERMISSION.getKey(), PersistentDataType.INTEGER_ARRAY, updatedPermissions);
    }

    public static String[] getPermissions(Player player) {
        if (!player.getPersistentDataContainer().has(NamespacedKeys.PERMISSION.getKey(), PersistentDataType.INTEGER_ARRAY)) {
            return new String[0];
        }
        int[] permissions = player.getPersistentDataContainer().get(NamespacedKeys.PERMISSION.getKey(), PersistentDataType.INTEGER_ARRAY);
        String[] permissionList = new String[permissions.length];
        for (int i = 0; i < permissions.length; i++) {
            int permissionId = permissions[i];
            for (Permission permission : Permission.values()) {
                if (permission.getId() == permissionId) {
                    permissionList[i] = permission.getName();
                }
            }
        }
        return permissionList;
    }
}
