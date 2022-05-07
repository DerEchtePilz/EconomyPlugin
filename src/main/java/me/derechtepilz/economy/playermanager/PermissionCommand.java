package me.derechtepilz.economy.playermanager;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PermissionCommand {
    public PermissionCommand() {
        new CommandAPICommand("permission")
                .withPermission(CommandPermission.OP)
                .withSubcommand(new CommandAPICommand("set")
                        .withArguments(playerArgument, permissionArgument)
                        .executes((sender, args) -> {
                            if (sender instanceof Player player) {
                                Player target = (Player) args[0];
                                String permissionName = (String) args[1];
                                Permission permissionToAssign = null;
                                for (Permission permission : Permission.values()) {
                                    if (permission.getName().equals(permissionName)) {
                                        permissionToAssign = permission;
                                    }
                                }
                                if (permissionToAssign == null) {
                                    player.sendMessage(TranslatableChatComponent.read("permissionCommand.permission_not_found").replace("%s", (String) args[1]));
                                    return;
                                }
                                if (Permission.hasPermission(target, permissionToAssign)) {
                                    player.sendMessage(TranslatableChatComponent.read("permissionCommand.permission_already_assigned"));
                                    return;
                                }
                                Permission.addPermission(target, permissionToAssign);
                                if (target.equals(player)) {
                                    player.sendMessage(TranslatableChatComponent.read("permissionCommand.assigned_permission").replace("%s", permissionToAssign.getName()));
                                } else {
                                    target.sendMessage(TranslatableChatComponent.read("permissionCommand.assigned_permission").replace("%s", permissionToAssign.getName()));
                                    player.sendMessage(TranslatableChatComponent.read("permissionCommand.assigned_permission_other").replace("%%s", permissionToAssign.getName()).replace("%s", target.getName()));
                                }
                                return;
                            }
                            if (sender instanceof ConsoleCommandSender console) {
                                Player target = (Player) args[0];
                                String permissionName = (String) args[1];
                                Permission permissionToAssign = null;
                                for (Permission permission : Permission.values()) {
                                    if (permission.getName().equals(permissionName)) {
                                        permissionToAssign = permission;
                                    }
                                }
                                if (permissionToAssign == null) {
                                    console.sendMessage(TranslatableChatComponent.read("permissionCommand.permission_not_found").replace("%s", (String) args[1]));
                                    return;
                                }
                                if (Permission.hasPermission(target, permissionToAssign)) {
                                    console.sendMessage(TranslatableChatComponent.read("permissionCommand.permission_already_assigned"));
                                    return;
                                }
                                Permission.addPermission(target, permissionToAssign);

                                target.sendMessage(TranslatableChatComponent.read("permissionCommand.assigned_permission").replace("%s", permissionToAssign.getName()));
                                console.sendMessage(TranslatableChatComponent.read("permissionCommand.assigned_permission_other").replace("%%s", permissionToAssign.getName()).replace("%s", target.getName()));
                                return;
                            }
                            sender.sendMessage(TranslatableChatComponent.read("command.wrong_executor"));
                        })
                )
                .withSubcommand(new CommandAPICommand("get")
                        .withArguments(playerArgument)
                        .executes((sender, args) -> {
                            if (sender instanceof Player player) {
                                Player target = (Player) args[0];
                                String[] permissions = Permission.getPermissions(target);
                                player.sendMessage(TranslatableChatComponent.read("permissionCommand.player_permissions").replace("%s", target.getName()));
                                for (String permission : permissions) {
                                    player.sendMessage("\u00A76- \u00A7a" + permission);
                                }
                                return;
                            }
                            sender.sendMessage(TranslatableChatComponent.read("command.wrong_executor"));
                        })
                )
                .withSubcommand(new CommandAPICommand("remove")
                        .withArguments(playerArgument, permissionArgument)
                        .executes((sender, args) -> {
                            if (sender instanceof Player player) {
                                Player target = (Player) args[0];
                                String permissionName = (String) args[1];
                                Permission permissionToRemove = null;
                                for (Permission permission : Permission.values()) {
                                    if (permission.getName().equals(permissionName)) {
                                        permissionToRemove = permission;
                                    }
                                }
                                if (permissionToRemove == null) {
                                    player.sendMessage(TranslatableChatComponent.read("permissionCommand.permission_not_found").replace("%s", (String) args[1]));
                                    return;
                                }
                                if (!Permission.hasPermission(target, permissionToRemove)) {
                                    player.sendMessage(TranslatableChatComponent.read("permissionCommand.permission_not_present"));
                                    return;
                                }
                                Permission.removePermission(player, permissionToRemove);
                                if (target.equals(player)) {
                                    player.sendMessage(TranslatableChatComponent.read("permissionCommand.removed_permission").replace("%s", permissionToRemove.getName()));
                                } else {
                                    target.sendMessage(TranslatableChatComponent.read("permissionCommand.removed_permission").replace("%s", permissionToRemove.getName()));
                                    player.sendMessage(TranslatableChatComponent.read("permissionCommand.removed_permission_other").replace("%%s", permissionToRemove.getName()).replace("%s", target.getName()));
                                }
                                return;
                            }
                            if (sender instanceof ConsoleCommandSender console) {
                                Player target = (Player) args[0];
                                String permissionName = (String) args[1];
                                Permission permissionToRemove = null;
                                for (Permission permission : Permission.values()) {
                                    if (permission.getName().equals(permissionName)) {
                                        permissionToRemove = permission;
                                    }
                                }
                                if (permissionToRemove == null) {
                                    console.sendMessage(TranslatableChatComponent.read("permissionCommand.permission_not_found").replace("%s", (String) args[1]));
                                    return;
                                }
                                if (!Permission.hasPermission(target, permissionToRemove)) {
                                    console.sendMessage(TranslatableChatComponent.read("permissionCommand.permission_not_present"));
                                    return;
                                }
                                Permission.removePermission(target, permissionToRemove);

                                target.sendMessage(TranslatableChatComponent.read("permissionCommand.removed_permission").replace("%s", permissionToRemove.getName()));
                                console.sendMessage(TranslatableChatComponent.read("permissionCommand.removed_permission_other").replace("%%s", permissionToRemove.getName()).replace("%s", target.getName()));
                                return;
                            }
                            sender.sendMessage(TranslatableChatComponent.read("command.wrong_executor"));
                        })
                )
                .register();
    }

    private final Argument playerArgument = new PlayerArgument("player").replaceSuggestions(ArgumentSuggestions.strings(getPlayers()));
    private final Argument permissionArgument = new StringArgument("permission").replaceSuggestions(ArgumentSuggestions.strings(getPermissions()));

    private String[] getPlayers() {
        List<String> players = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            players.add(player.getName());
        }
        String[] suggestions = new String[players.size()];
        for (int i = 0; i < players.size(); i++) {
            suggestions[i] = players.get(i);
        }
        return suggestions;
    }

    private String[] getPermissions() {
        List<String> permissions = new ArrayList<>();
        for (Permission permission : Permission.values()) {
            permissions.add(permission.getName());
        }
        String[] suggestions = new String[permissions.size()];
        for (int i = 0; i < permissions.size(); i++) {
            suggestions[i] = permissions.get(i);
        }
        return suggestions;
    }
}
