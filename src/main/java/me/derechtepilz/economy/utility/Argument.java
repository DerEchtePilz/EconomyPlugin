package me.derechtepilz.economy.utility;

import dev.jorel.commandapi.arguments.*;
import me.derechtepilz.economy.playermanager.Permission;
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
            case ONE_PLAYER -> this.argument = (dev.jorel.commandapi.arguments.Argument<T>) new PlayerArgument("player").replaceSuggestions(ArgumentSuggestions.strings(info -> getPlayers()));
            case PERMISSION -> this.argument = (dev.jorel.commandapi.arguments.Argument<T>) getListArgument("permissions", ",", false, getPermissions());
            case MULTIPLE_PLAYERS -> this.argument = (dev.jorel.commandapi.arguments.Argument<T>) getListArgument("players", ",", false, this::getPlayerList);
        }
    }

    public dev.jorel.commandapi.arguments.Argument<T> getArgument() {
        return argument;
    }

    public enum ArgumentType {
        ONE_PLAYER,
        MULTIPLE_PLAYERS,
        PERMISSION
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

    private <T> ListArgument<T> getListArgument(String nodeName, String delimiter, boolean duplicates, List<T> suggestions) {
        return new ListArgumentBuilder<T>(nodeName, delimiter).allowDuplicates(duplicates).withList(suggestions).withStringMapper().build();
    }

    private <T> ListArgument<T> getListArgument(String nodeName, String delimiter, boolean duplicates, Supplier<Collection<T>> supplier) {
        return new ListArgumentBuilder<T>(nodeName, delimiter).allowDuplicates(duplicates).withList(supplier).withStringMapper().build();
    }
}
