package me.derechtepilz.economy.playermanager.permission;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.derechtepilz.economy.utility.NamespacedKeys;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;

public class CustomPermissionGroup {
    private Map<String, List<String>> customPermissionGroups = new HashMap<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File permissionGroup = new File(new File("./plugins/Economy"), "permission_groups.json");

    public void registerPermissionGroup(@NotNull String groupName, @NotNull List<String> groupPermissions) throws IOException {
        customPermissionGroups.put(groupName, groupPermissions);
        buildPermissionGroup();
    }

    public void deletePermissionGroup(@NotNull String groupName) throws IOException {
        if (!customPermissionGroups.containsKey(groupName)) {
            return;
        }
        customPermissionGroups.remove(groupName);
        buildPermissionGroup();
    }

    public List<String> getPermissionGroup(@NotNull String groupName) {
        if (!isPermissionGroup(groupName)) {
            return new ArrayList<>();
        }
        return customPermissionGroups.get(groupName);
    }

    public void addPermissionGroupToPlayer(@NotNull String groupName, @NotNull Player player) {
        for (String permission : getPermissionGroup(groupName)) {
            if (Permission.hasPermission(player, Permission.valueOf(permission.toUpperCase()))) {
                continue;
            }
            Permission.addPermission(player, Permission.valueOf(permission.toUpperCase()));
        }
    }

    public void removePermissionGroupFromPlayer(@NotNull String groupName, @NotNull Player player) {
        for (String permission : getPermissionGroup(groupName)) {
            if (!Permission.hasPermission(player, Permission.valueOf(permission.toUpperCase()))) {
                continue;
            }
            Permission.removePermission(player, Permission.valueOf(permission.toUpperCase()));
        }
    }

    public boolean hasPlayerPermissionGroup(@NotNull String groupName, @NotNull Player player) {
        int[] playerPermissions = player.getPersistentDataContainer().get(NamespacedKeys.PERMISSION.getKey(), PersistentDataType.INTEGER_ARRAY);
        int checkedMatchingPermissions = 0;
        List<String> customPermissionGroup = getPermissionGroup(groupName);
        for (int permissionId : playerPermissions) {
            for (String permission : customPermissionGroup) {
                if (Permission.valueOf(permission.toUpperCase()).getId() == permissionId) {
                    checkedMatchingPermissions += 1;
                }
            }
        }
        return checkedMatchingPermissions == customPermissionGroup.size();
    }

    public boolean isPermissionGroup(@NotNull String groupName) {
        return customPermissionGroups.containsKey(groupName);
    }

    public List<String> getGroupNames() {
        List<String> groupNames = new ArrayList<>();
        for (String groupName : customPermissionGroups.keySet()) {
            groupNames.add(groupName);
        }
        return groupNames;
    }

    @SuppressWarnings("unchecked")
    public void loadPermissionGroup() throws IOException {
        customPermissionGroups.clear();
        if (!permissionGroup.exists()) {
            return;
        }
        customPermissionGroups = gson.fromJson(new FileReader(permissionGroup), HashMap.class);
    }

    public void buildPermissionGroup() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (!permissionGroup.exists()) {
            permissionGroup.createNewFile();
        }
        Writer writer = new FileWriter(permissionGroup);
        writer.write(gson.toJson(customPermissionGroups));
        writer.close();
    }
}
