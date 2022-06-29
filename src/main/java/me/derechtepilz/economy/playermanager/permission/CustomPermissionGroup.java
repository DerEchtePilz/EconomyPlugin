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
    private Map<String, List<Double>> customPermissionGroups = new HashMap<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File permissionGroup = new File(new File("./plugins/Economy"), "permission_groups.json");

    public void registerPermissionGroup(@NotNull String groupName, @NotNull List<Double> groupPermissions) throws IOException {
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

    public List<Permission> getPermissionGroup(@NotNull String groupName) {
        if (!isPermissionGroup(groupName)) {
            return new ArrayList<>();
        }
        List<Permission> permissionGroup = new ArrayList<>();
        for (double permissionId : customPermissionGroups.get(groupName)) {
            permissionGroup.add(Permission.getPermissionFromId((int) permissionId));
        }
        return permissionGroup;
    }

    public void addPermissionGroupToPlayer(@NotNull String groupName, @NotNull Player player) {
        for (Permission permission : getPermissionGroup(groupName)) {
            if (Permission.hasPermission(player, permission)) {
                continue;
            }
            Permission.addPermission(player, permission);
        }
    }

    public void removePermissionGroupFromPlayer(@NotNull String groupName, @NotNull Player player) {
        for (Permission permission : getPermissionGroup(groupName)) {
            if (!Permission.hasPermission(player, permission)) {
                continue;
            }
            Permission.removePermission(player, permission);
        }
    }

    public boolean hasPlayerPermissionGroup(@NotNull String groupName, @NotNull Player player) {
        int[] playerPermissions = player.getPersistentDataContainer().get(NamespacedKeys.PERMISSION.getKey(), PersistentDataType.INTEGER_ARRAY);
        int checkedMatchingPermissions = 0;
        List<Permission> customPermissionGroup = new ArrayList<>();
        if (isPermissionGroup(groupName)) {
            customPermissionGroup = getPermissionGroup(groupName);
            for (int permissionId : playerPermissions) {
                for (Permission permission : customPermissionGroup) {
                    if (permission.getId() == permissionId) {
                        checkedMatchingPermissions += 1;
                    }
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

    public void loadPermissionGroup() throws IOException {
        customPermissionGroups.clear();
        if (!permissionGroup.exists()) {
            return;
        }
        customPermissionGroups = gson.fromJson(new FileReader(permissionGroup), HashMap.class);
    }

    public void buildPermissionGroup() throws IOException {
        if (!permissionGroup.exists()) {
            permissionGroup.createNewFile();
        }
        Writer writer = new FileWriter(permissionGroup);
        writer.write(gson.toJson(customPermissionGroups));
        writer.close();
    }
}
