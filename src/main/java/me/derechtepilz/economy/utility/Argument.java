package me.derechtepilz.economy.utility;

import dev.jorel.commandapi.arguments.*;
import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.playermanager.permission.Permission;
import me.derechtepilz.economy.playermanager.permission.PermissionGroup;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class Argument<T> {
    private dev.jorel.commandapi.arguments.Argument<T> argument;
    public Argument(ArgumentType type) {
        switch (type) {
            case PLAYER_SINGLE -> this.argument = (dev.jorel.commandapi.arguments.Argument<T>) new PlayerArgument("player").replaceSuggestions(ArgumentSuggestions.strings(info -> getPlayers()));
            case PLAYER_MULTIPLE -> this.argument = (dev.jorel.commandapi.arguments.Argument<T>) getListArgument("players", ",", false, this::getPlayerList);
            case PERMISSION_SINGLE -> this.argument = (dev.jorel.commandapi.arguments.Argument<T>) getListArgument("permissions", ",", false, getPermissions());
            case PERMISSION_GROUP -> this.argument = (dev.jorel.commandapi.arguments.Argument<T>) getListArgument("permissionGroups", ",", false, this::getPermissionGroup);
        }
    }

    public dev.jorel.commandapi.arguments.Argument<T> getArgument() {
        return argument;
    }

    public enum ArgumentType {
        PLAYER_SINGLE,
        PLAYER_MULTIPLE,
        PERMISSION_SINGLE,
        PERMISSION_GROUP
    }

    private String[] getPlayers() {
        List<String> players = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            players.add(player.getName());
        }
        return players.toArray(new String[0]);
    }

    private List<String> getPlayerList() {
        List<String> players = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            players.add(player.getName());
        }
        return players;
    }

    private List<String> getPermissions() {
        List<String> permissions = new ArrayList<>();
        for (Permission permission : Permission.values()) {
            permissions.add(permission.getName());
        }
        return permissions;
    }

    private List<String> getPermissionGroup() {
        List<String> permissionGroups = new ArrayList<>();
        for (PermissionGroup permissionGroup : PermissionGroup.values()) {
            permissionGroups.add(permissionGroup.getGroupName());
        }
        for (String customPermissionGroup : Main.getInstance().getCustomPermissionGroup().getGroupNames()) {
            permissionGroups.add(customPermissionGroup);
        }
        return permissionGroups;
    }

    private <T> ListArgument<T> getListArgument(String nodeName, String delimiter, boolean duplicates, List<T> suggestions) {
        return new ListArgumentBuilder<T>(nodeName, delimiter).allowDuplicates(duplicates).withList(suggestions).withStringMapper().build();
    }

    private <T> ListArgument<T> getListArgument(String nodeName, String delimiter, boolean duplicates, Supplier<Collection<T>> supplier) {
        return new ListArgumentBuilder<T>(nodeName, delimiter).allowDuplicates(duplicates).withList(supplier).withStringMapper().build();
    }
}
