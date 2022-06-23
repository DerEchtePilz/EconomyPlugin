package me.derechtepilz.economy.playermanager;

import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.*;
import me.derechtepilz.economy.utility.Argument;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class PermissionCommand {
    public PermissionCommand() {
        new CommandTree("permission")
                .withPermission(CommandPermission.OP)
                .then(new LiteralArgument("set")
                        .then(new Argument<Player>(Argument.ArgumentType.ONE_PLAYER).getArgument()
                                .then(new Argument<String>(Argument.ArgumentType.PERMISSION).getArgument()
                                        .executes((sender, args) -> {
                                            if (sender instanceof Player player) {
                                                Player target = (Player) args[0];
                                                List<String> permissions = (List<String>) args[1];
                                                for (String permissionName : permissions) {
                                                    Permission permissionToAssign = null;
                                                    for (Permission permission : Permission.values()) {
                                                        if (permission.getName().equals(permissionName)) {
                                                            permissionToAssign = permission;
                                                        }
                                                    }
                                                    if (permissionToAssign == null) {
                                                        player.sendMessage(TranslatableChatComponent.read("permissionCommand.permission_not_found").replace("%s", permissionName));
                                                        if (permissions.indexOf(permissionName) != permissions.size() - 1) {
                                                            continue;
                                                        }
                                                        return;
                                                    }
                                                    if (Permission.hasPermission(target, permissionToAssign)) {
                                                        player.sendMessage(TranslatableChatComponent.read("permissionCommand.permission_already_assigned").replace("%s", permissionName));
                                                        if (permissions.indexOf(permissionName) != permissions.size() - 1) {
                                                            continue;
                                                        }
                                                        return;
                                                    }
                                                    Permission.addPermission(target, permissionToAssign);
                                                    if (target.equals(player)) {
                                                        player.sendMessage(TranslatableChatComponent.read("permissionCommand.assigned_permission").replace("%s", permissionToAssign.getName()));
                                                    } else {
                                                        target.sendMessage(TranslatableChatComponent.read("permissionCommand.assigned_permission").replace("%s", permissionToAssign.getName()));
                                                        player.sendMessage(TranslatableChatComponent.read("permissionCommand.assigned_permission_other").replace("%%s", target.getName()).replace("%s", permissionToAssign.getName()));
                                                    }
                                                }
                                                return;
                                            }
                                            if (sender instanceof ConsoleCommandSender console) {
                                                Player target = (Player) args[0];
                                                List<String> permissions = (List<String>) args[1];
                                                for (String permissionName : permissions) {
                                                    Permission permissionToAssign = null;
                                                    for (Permission permission : Permission.values()) {
                                                        if (permission.getName().equals(permissionName)) {
                                                            permissionToAssign = permission;
                                                        }
                                                    }
                                                    if (permissionToAssign == null) {
                                                        console.sendMessage(TranslatableChatComponent.read("permissionCommand.permission_not_found").replace("%s", permissionName));
                                                        if (permissions.indexOf(permissionName) != permissions.size() - 1) {
                                                            continue;
                                                        }
                                                        return;
                                                    }
                                                    if (Permission.hasPermission(target, permissionToAssign)) {
                                                        console.sendMessage(TranslatableChatComponent.read("permissionCommand.permission_already_assigned").replace("%s", permissionName));
                                                        if (permissions.indexOf(permissionName) != permissions.size() - 1) {
                                                            continue;
                                                        }
                                                        return;
                                                    }
                                                    Permission.addPermission(target, permissionToAssign);
                                                    target.sendMessage(TranslatableChatComponent.read("permissionCommand.assigned_permission").replace("%s", permissionToAssign.getName()));
                                                    console.sendMessage(TranslatableChatComponent.read("permissionCommand.assigned_permission_other").replace("%%s", target.getName()).replace("%s", permissionToAssign.getName()));
                                                }
                                                return;
                                            }
                                            sender.sendMessage(TranslatableChatComponent.read("command.wrong_executor"));
                                        }))))
                .then(new LiteralArgument("get")
                        .then(new Argument<Player>(Argument.ArgumentType.ONE_PLAYER).getArgument()
                                .executesPlayer((player, args) -> {
                                    Player target = (Player) args[0];
                                    String[] permissions = Permission.getPermissions(target);
                                    player.sendMessage(TranslatableChatComponent.read("permissionCommand.player_permissions").replace("%s", target.getName()));
                                    for (String permission : permissions) {
                                        player.sendMessage("\u00A76- \u00A7a" + permission);
                                    }
                                })))
                .then(new LiteralArgument("remove")
                        .then(new Argument<Player>(Argument.ArgumentType.ONE_PLAYER).getArgument()
                                .then(new Argument<String>(Argument.ArgumentType.PERMISSION).getArgument()
                                        .executes((sender, args) -> {
                                            if (sender instanceof Player player) {
                                                Player target = (Player) args[0];
                                                List<String> permissions = (List<String>) args[1];
                                                for (String permissionName : permissions) {
                                                    Permission permissionToRemove = null;
                                                    for (Permission permission : Permission.values()) {
                                                        if (permission.getName().equals(permissionName)) {
                                                            permissionToRemove = permission;
                                                        }
                                                    }
                                                    if (permissionToRemove == null) {
                                                        player.sendMessage(TranslatableChatComponent.read("permissionCommand.permission_not_found").replace("%s", permissionName));
                                                        if (permissions.indexOf(permissionName) != permissions.size() - 1) {
                                                            continue;
                                                        }
                                                        return;
                                                    }
                                                    if (!Permission.hasPermission(target, permissionToRemove)) {
                                                        player.sendMessage(TranslatableChatComponent.read("permissionCommand.permission_not_present").replace("%s", permissionName));
                                                        if (permissions.indexOf(permissionName) != permissions.size() - 1) {
                                                            continue;
                                                        }
                                                        return;
                                                    }
                                                    Permission.removePermission(player, permissionToRemove);
                                                    if (target.equals(player)) {
                                                        player.sendMessage(TranslatableChatComponent.read("permissionCommand.removed_permission").replace("%s", permissionToRemove.getName()));
                                                    } else {
                                                        target.sendMessage(TranslatableChatComponent.read("permissionCommand.removed_permission").replace("%s", permissionToRemove.getName()));
                                                        player.sendMessage(TranslatableChatComponent.read("permissionCommand.removed_permission_other").replace("%%s", target.getName()).replace("%s", permissionToRemove.getName()));
                                                    }
                                                }
                                                return;
                                            }
                                            if (sender instanceof ConsoleCommandSender console) {
                                                Player target = (Player) args[0];
                                                List<String> permissions = (List<String>) args[1];
                                                for (String permissionName : permissions) {
                                                    Permission permissionToRemove = null;
                                                    for (Permission permission : Permission.values()) {
                                                        if (permission.getName().equals(permissionName)) {
                                                            permissionToRemove = permission;
                                                        }
                                                    }
                                                    if (permissionToRemove == null) {
                                                        console.sendMessage(TranslatableChatComponent.read("permissionCommand.permission_not_found").replace("%s", permissionName));
                                                        if (permissions.indexOf(permissionName) != permissions.size() - 1) {
                                                            continue;
                                                        }
                                                        return;
                                                    }
                                                    if (!Permission.hasPermission(target, permissionToRemove)) {
                                                        console.sendMessage(TranslatableChatComponent.read("permissionCommand.permission_not_present").replace("%s", permissionName));
                                                        if (permissions.indexOf(permissionName) != permissions.size() - 1) {
                                                            continue;
                                                        }
                                                        return;
                                                    }
                                                    Permission.removePermission(target, permissionToRemove);
                                                    target.sendMessage(TranslatableChatComponent.read("permissionCommand.removed_permission").replace("%s", permissionToRemove.getName()));
                                                    console.sendMessage(TranslatableChatComponent.read("permissionCommand.removed_permission_other").replace("%%s", target.getName()).replace("%s", permissionToRemove.getName()));
                                                }
                                                return;
                                            }
                                            sender.sendMessage(TranslatableChatComponent.read("command.wrong_executor"));
                                        }))))
                .register();
    }
}
