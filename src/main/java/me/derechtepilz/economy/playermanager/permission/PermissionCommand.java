package me.derechtepilz.economy.playermanager.permission;

import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.utility.Argument;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class PermissionCommand {
    public void register() {
        new CommandTree("permission")
                .withPermission(CommandPermission.OP)
                .then(new LiteralArgument("set")
                        .then(new LiteralArgument("single")
                                .then(new Argument<Player>(Argument.ArgumentType.PLAYER_SINGLE).getArgument()
                                        .then(new Argument<String>(Argument.ArgumentType.PERMISSION_SINGLE).getArgument()
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
                        .then(new LiteralArgument("group")
                                .then(new Argument<Player>(Argument.ArgumentType.PLAYER_SINGLE).getArgument()
                                        .then(new Argument<String>(Argument.ArgumentType.PERMISSION_GROUP).getArgument()
                                                .executes((sender, args) -> {
                                                    if (sender instanceof Player player) {
                                                        Player target = (Player) args[0];
                                                        List<String> permissionGroups = (List<String>) args[1];
                                                        for (String permissionGroupName : permissionGroups) {
                                                            PermissionGroup permissionGroupToAssign = null;
                                                            for (PermissionGroup permissionGroup : PermissionGroup.values()) {
                                                                if (permissionGroup.getGroupName().equals(permissionGroupName)) {
                                                                    permissionGroupToAssign = permissionGroup;
                                                                }
                                                            }
                                                            if (permissionGroupToAssign == null) {
                                                                if (!Main.getInstance().getCustomPermissionGroup().isPermissionGroup(permissionGroupName)) {
                                                                    player.sendMessage(TranslatableChatComponent.read("permissionCommand.permission_group_not_found").replace("%s", permissionGroupName));
                                                                    if (permissionGroups.indexOf(permissionGroupName) != permissionGroups.size() - 1) {
                                                                        continue;
                                                                    }
                                                                    return;
                                                                }
                                                                if (Main.getInstance().getCustomPermissionGroup().hasPlayerPermissionGroup(permissionGroupName, target)) {
                                                                    player.sendMessage(TranslatableChatComponent.read("permissionCommand.permission_group_already_assigned").replace("%s", permissionGroupName));
                                                                    if (permissionGroups.indexOf(permissionGroupName) != permissionGroups.size() - 1) {
                                                                        continue;
                                                                    }
                                                                    return;
                                                                }
                                                                Main.getInstance().getCustomPermissionGroup().addPermissionGroupToPlayer(permissionGroupName, target);
                                                                if (target.equals(player)) {
                                                                    player.sendMessage(TranslatableChatComponent.read("permissionCommand.assigned_permission_group").replace("%s", permissionGroupName));
                                                                } else {
                                                                    target.sendMessage(TranslatableChatComponent.read("permissionCommand.assigned_permission_group").replace("%s", permissionGroupName));
                                                                    player.sendMessage(TranslatableChatComponent.read("permissionCommand.assigned_permission_group_other").replace("%%s", target.getName()).replace("%s", permissionGroupName));
                                                                }
                                                                if (permissionGroups.indexOf(permissionGroupName) != permissionGroups.size() - 1) {
                                                                    continue;
                                                                }
                                                                return;
                                                            }
                                                            if (PermissionGroup.hasPermissionGroup(target, permissionGroupToAssign)) {
                                                                player.sendMessage(TranslatableChatComponent.read("permissionCommand.permission_group_already_assigned").replace("%s", permissionGroupToAssign.getGroupName()));
                                                                if (permissionGroups.indexOf(permissionGroupName) != permissionGroups.size() - 1) {
                                                                    continue;
                                                                }
                                                                return;
                                                            }
                                                            PermissionGroup.addPermissionGroup(target, permissionGroupToAssign);
                                                            if (target.equals(player)) {
                                                                player.sendMessage(TranslatableChatComponent.read("permissionCommand.assigned_permission_group").replace("%s", permissionGroupToAssign.getGroupName()));
                                                            } else {
                                                                target.sendMessage(TranslatableChatComponent.read("permissionCommand.assigned_permission_group").replace("%s", permissionGroupToAssign.getGroupName()));
                                                                player.sendMessage(TranslatableChatComponent.read("permissionCommand.assigned_permission_group_other").replace("%%s", target.getName()).replace("%s", permissionGroupToAssign.getGroupName()));
                                                            }
                                                        }
                                                        return;
                                                    }
                                                    if (sender instanceof ConsoleCommandSender console) {
                                                        Player target = (Player) args[0];
                                                        List<String> permissionGroups = (List<String>) args[1];
                                                        for (String permissionGroupName : permissionGroups) {
                                                            PermissionGroup permissionGroupToAssign = null;
                                                            for (PermissionGroup permissionGroup : PermissionGroup.values()) {
                                                                if (permissionGroup.getGroupName().equals(permissionGroupName)) {
                                                                    permissionGroupToAssign = permissionGroup;
                                                                }
                                                            }
                                                            if (permissionGroupToAssign == null) {
                                                                if (!Main.getInstance().getCustomPermissionGroup().isPermissionGroup(permissionGroupName)) {
                                                                    console.sendMessage(TranslatableChatComponent.read("permissionCommand.permission_group_not_found").replace("%s", permissionGroupName));
                                                                    if (permissionGroups.indexOf(permissionGroupName) != permissionGroups.size() - 1) {
                                                                        continue;
                                                                    }
                                                                    return;
                                                                }
                                                                if (Main.getInstance().getCustomPermissionGroup().hasPlayerPermissionGroup(permissionGroupName, target)) {
                                                                    console.sendMessage(TranslatableChatComponent.read("permissionCommand.permission_group_already_assigned").replace("%s", permissionGroupName));
                                                                    if (permissionGroups.indexOf(permissionGroupName) != permissionGroups.size() - 1) {
                                                                        continue;
                                                                    }
                                                                    return;
                                                                }
                                                                Main.getInstance().getCustomPermissionGroup().addPermissionGroupToPlayer(permissionGroupName, target);
                                                                target.sendMessage(TranslatableChatComponent.read("permissionCommand.assigned_permission_group").replace("%s", permissionGroupName));
                                                                console.sendMessage(TranslatableChatComponent.read("permissionCommand.assigned_permission_group_other").replace("%%s", target.getName()).replace("%s", permissionGroupName));
                                                                if (permissionGroups.indexOf(permissionGroupName) != permissionGroups.size() - 1) {
                                                                    continue;
                                                                }
                                                                return;
                                                            }
                                                            if (PermissionGroup.hasPermissionGroup(target, permissionGroupToAssign)) {
                                                                console.sendMessage(TranslatableChatComponent.read("permissionCommand.permission_group_already_assigned").replace("%s", permissionGroupToAssign.getGroupName()));
                                                                if (permissionGroups.indexOf(permissionGroupName) != permissionGroups.size() - 1) {
                                                                    continue;
                                                                }
                                                                return;
                                                            }
                                                            PermissionGroup.addPermissionGroup(target, permissionGroupToAssign);
                                                            target.sendMessage(TranslatableChatComponent.read("permissionCommand.assigned_permission_group").replace("%s", permissionGroupToAssign.getGroupName()));
                                                            console.sendMessage(TranslatableChatComponent.read("permissionCommand.assigned_permission_group_other").replace("%%s", target.getName()).replace("%s", permissionGroupToAssign.getGroupName()));
                                                        }
                                                        return;
                                                    }
                                                    sender.sendMessage(TranslatableChatComponent.read("command.wrong_executor"));
                                                })))))
                .then(new LiteralArgument("get")
                        .then(new Argument<Player>(Argument.ArgumentType.PLAYER_SINGLE).getArgument()
                                .executesPlayer((player, args) -> {
                                    Player target = (Player) args[0];
                                    String[] permissions = Permission.getPermissions(target);
                                    player.sendMessage(TranslatableChatComponent.read("permissionCommand.player_permissions").replace("%s", target.getName()));
                                    for (String permission : permissions) {
                                        player.sendMessage("\u00A76- \u00A7a" + permission);
                                    }
                                })))
                .then(new LiteralArgument("remove")
                        .then(new LiteralArgument("single")
                                .then(new Argument<Player>(Argument.ArgumentType.PLAYER_SINGLE).getArgument()
                                        .then(new Argument<String>(Argument.ArgumentType.PERMISSION_SINGLE).getArgument()
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
                        .then(new LiteralArgument("group")
                                .then(new Argument<Player>(Argument.ArgumentType.PLAYER_SINGLE).getArgument()
                                        .then(new Argument<String>(Argument.ArgumentType.PERMISSION_GROUP).getArgument()
                                                .executes((sender, args) -> {
                                                    if (sender instanceof Player player) {
                                                        Player target = (Player) args[0];
                                                        List<String> permissionGroups = (List<String>) args[1];
                                                        for (String permissionGroupName : permissionGroups) {
                                                            PermissionGroup permissionGroupToRemove = null;
                                                            for (PermissionGroup permissionGroup : PermissionGroup.values()) {
                                                                if (permissionGroup.getGroupName().equals(permissionGroupName)) {
                                                                    permissionGroupToRemove = permissionGroup;
                                                                }
                                                            }
                                                            if (permissionGroupToRemove == null) {
                                                                if (!Main.getInstance().getCustomPermissionGroup().isPermissionGroup(permissionGroupName)) {
                                                                    player.sendMessage(TranslatableChatComponent.read("permissionCommand.permission_group_not_found").replace("%s", permissionGroupName));
                                                                    if (permissionGroups.indexOf(permissionGroupName) != permissionGroups.size() - 1) {
                                                                        continue;
                                                                    }
                                                                    return;
                                                                }
                                                                if (!Main.getInstance().getCustomPermissionGroup().hasPlayerPermissionGroup(permissionGroupName, target)) {
                                                                    player.sendMessage(TranslatableChatComponent.read("permissionCommand.permission_group_not_present").replace("%s", permissionGroupName));
                                                                    if (permissionGroups.indexOf(permissionGroupName) != permissionGroups.size() - 1) {
                                                                        continue;
                                                                    }
                                                                    return;
                                                                }
                                                                Main.getInstance().getCustomPermissionGroup().removePermissionGroupFromPlayer(permissionGroupName, target);
                                                                if (target.equals(player)) {
                                                                    player.sendMessage(TranslatableChatComponent.read("permissionCommand.removed_permission_group").replace("%s", permissionGroupName));
                                                                } else {
                                                                    target.sendMessage(TranslatableChatComponent.read("permissionCommand.removed_permission_group").replace("%s", permissionGroupName));
                                                                    player.sendMessage(TranslatableChatComponent.read("permissionCommand.removed_permission_group_other").replace("%%s", target.getName()).replace("%s", permissionGroupName));
                                                                }
                                                                if (permissionGroups.indexOf(permissionGroupName) != permissionGroups.size() - 1) {
                                                                    continue;
                                                                }
                                                                return;
                                                            }
                                                            if (!PermissionGroup.hasPermissionGroup(target, permissionGroupToRemove)) {
                                                                player.sendMessage(TranslatableChatComponent.read("permissionCommand.permission_group_not_present").replace("%s", permissionGroupToRemove.getGroupName()));
                                                                if (permissionGroups.indexOf(permissionGroupName) != permissionGroups.size() - 1) {
                                                                    continue;
                                                                }
                                                                return;
                                                            }
                                                            PermissionGroup.removePermissionGroup(target, permissionGroupToRemove);
                                                            if (target.equals(player)) {
                                                                player.sendMessage(TranslatableChatComponent.read("permissionCommand.removed_permission_group").replace("%s", permissionGroupToRemove.getGroupName()));
                                                            } else {
                                                                target.sendMessage(TranslatableChatComponent.read("permissionCommand.removed_permission_group").replace("%s", permissionGroupToRemove.getGroupName()));
                                                                player.sendMessage(TranslatableChatComponent.read("permissionCommand.removed_permission_group_other").replace("%%s", target.getName()).replace("%s", permissionGroupToRemove.getGroupName()));
                                                            }
                                                        }
                                                        return;
                                                    }
                                                    if (sender instanceof ConsoleCommandSender console) {
                                                        Player target = (Player) args[0];
                                                        List<String> permissionGroups = (List<String>) args[1];
                                                        for (String permissionGroupName : permissionGroups) {
                                                            PermissionGroup permissionGroupToRemove = null;
                                                            for (PermissionGroup permissionGroup : PermissionGroup.values()) {
                                                                if (permissionGroup.getGroupName().equals(permissionGroupName)) {
                                                                    permissionGroupToRemove = permissionGroup;
                                                                }
                                                            }
                                                            if (permissionGroupToRemove == null) {
                                                                if (!Main.getInstance().getCustomPermissionGroup().isPermissionGroup(permissionGroupName)) {
                                                                    console.sendMessage(TranslatableChatComponent.read("permissionCommand.permission_group_not_found").replace("%s", permissionGroupName));
                                                                    if (permissionGroups.indexOf(permissionGroupName) != permissionGroups.size() - 1) {
                                                                        continue;
                                                                    }
                                                                    return;
                                                                }
                                                                if (!Main.getInstance().getCustomPermissionGroup().hasPlayerPermissionGroup(permissionGroupName, target)) {
                                                                    console.sendMessage(TranslatableChatComponent.read("permissionCommand.permission_group_not_present").replace("%s", permissionGroupName));
                                                                    if (permissionGroups.indexOf(permissionGroupName) != permissionGroups.size() - 1) {
                                                                        continue;
                                                                    }
                                                                    return;
                                                                }
                                                                Main.getInstance().getCustomPermissionGroup().removePermissionGroupFromPlayer(permissionGroupName, target);
                                                                target.sendMessage(TranslatableChatComponent.read("permissionCommand.removed_permission_group").replace("%s", permissionGroupName));
                                                                console.sendMessage(TranslatableChatComponent.read("permissionCommand.removed_permission_group_other").replace("%%s", target.getName()).replace("%s", permissionGroupName));
                                                                if (permissionGroups.indexOf(permissionGroupName) != permissionGroups.size() - 1) {
                                                                    continue;
                                                                }
                                                                return;
                                                            }
                                                            if (!PermissionGroup.hasPermissionGroup(target, permissionGroupToRemove)) {
                                                                console.sendMessage(TranslatableChatComponent.read("permissionCommand.permission_group_not_present").replace("%s", permissionGroupToRemove.getGroupName()));
                                                                if (permissionGroups.indexOf(permissionGroupName) != permissionGroups.size() - 1) {
                                                                    continue;
                                                                }
                                                                return;
                                                            }
                                                            PermissionGroup.removePermissionGroup(target, permissionGroupToRemove);
                                                            target.sendMessage(TranslatableChatComponent.read("permissionCommand.removed_permission_group").replace("%s", permissionGroupToRemove.getGroupName()));
                                                            console.sendMessage(TranslatableChatComponent.read("permissionCommand.removed_permission_group_other").replace("%%s", target.getName()).replace("%s", permissionGroupToRemove.getGroupName()));
                                                        }
                                                        return;
                                                    }
                                                    sender.sendMessage(TranslatableChatComponent.read("command.wrong_executor"));
                                                })))))
                .then(new LiteralArgument("group")
                        .then(new LiteralArgument("create")
                                .then(new StringArgument("groupName")
                                        .then(new Argument<String>(Argument.ArgumentType.PERMISSION_SINGLE).getArgument()
                                                .executes((sender, args) -> {
                                                    if (sender instanceof Player player) {
                                                        String permissionGroupName = (String) args[0];
                                                        List<String> permissionGroupPermissions = (List<String>) args[1];
                                                        List<Double> permissionGroupPermissionIds = new ArrayList<>();
                                                        for (String permissionName : permissionGroupPermissions) {
                                                            permissionGroupPermissionIds.add((double) Permission.getPermissionIdFromName(permissionName));
                                                        }

                                                        try {
                                                            Main.getInstance().getCustomPermissionGroup().registerPermissionGroup(permissionGroupName.toLowerCase(), permissionGroupPermissionIds);

                                                            player.sendMessage(TranslatableChatComponent.read("permissionCommand.custom_group.created").replace("%s", permissionGroupName.toLowerCase()));
                                                        } catch (IOException e) {
                                                            player.sendMessage("§c" + e.getMessage());
                                                        }
                                                        return;
                                                    }
                                                    if (sender instanceof ConsoleCommandSender console) {
                                                        String permissionGroupName = (String) args[0];
                                                        List<String> permissionGroupPermissions = (List<String>) args[1];
                                                        List<Double> permissionGroupPermissionIds = new ArrayList<>();
                                                        for (String permissionName : permissionGroupPermissions) {
                                                            permissionGroupPermissionIds.add((double) Permission.getPermissionIdFromName(permissionName));
                                                        }

                                                        try {
                                                            Main.getInstance().getCustomPermissionGroup().registerPermissionGroup(permissionGroupName.toLowerCase(), permissionGroupPermissionIds);

                                                            console.sendMessage(TranslatableChatComponent.read("permissionCommand.custom_group.created").replace("%s", permissionGroupName.toLowerCase()));
                                                        } catch (IOException e) {
                                                            console.sendMessage("§c" + e.getMessage());
                                                        }
                                                        return;
                                                    }
                                                    sender.sendMessage(TranslatableChatComponent.read("command.wrong_executor"));
                                                }))))
                        .then(new LiteralArgument("delete")
                                .then(new StringArgument("groupName")
                                        .executes((sender, args) -> {
                                            if (sender instanceof Player player) {
                                                String permissionGroupName = (String) args[0];
                                                try {
                                                    Main.getInstance().getCustomPermissionGroup().deletePermissionGroup(permissionGroupName.toLowerCase());

                                                    player.sendMessage(TranslatableChatComponent.read("permissionCommand.custom_group.deleted").replace("%s", permissionGroupName.toLowerCase()));
                                                } catch (IOException e) {
                                                    player.sendMessage("§c" + e.getMessage());
                                                }
                                                return;
                                            }
                                            if (sender instanceof ConsoleCommandSender console) {
                                                String permissionGroupName = (String) args[0];
                                                try {
                                                    Main.getInstance().getCustomPermissionGroup().deletePermissionGroup(permissionGroupName.toLowerCase());

                                                    console.sendMessage(TranslatableChatComponent.read("permissionCommand.custom_group.deleted").replace("%s", permissionGroupName.toLowerCase()));
                                                } catch (IOException e) {
                                                    console.sendMessage("§c" + e.getMessage());
                                                }
                                                return;
                                            }
                                            sender.sendMessage(TranslatableChatComponent.read("command.wrong_executor"));
                                        }))))
                .register();
    }
}
