package me.derechtepilz.economy.bukkitcommands.commands.commandapicommands;

import me.derechtepilz.economy.bukkitcommands.arguments.PlayerArgument;
import me.derechtepilz.economy.bukkitcommands.commands.CommandBase;
import me.derechtepilz.economy.playermanager.Permission;
import me.derechtepilz.economy.utility.ChatFormatter;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class PermissionCommandExecutor extends CommandBase {
    public PermissionCommandExecutor(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 1, 2 -> sender.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_few_arguments").replace("%%s", ChatFormatter.valueOf(3)).replace("%s", ChatFormatter.valueOf(args.length)));
            case 3 -> {
                switch (args[1]) {
                    case "set", "remove" -> sender.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_few_arguments").replace("%%s", ChatFormatter.valueOf(4)).replace("%s", ChatFormatter.valueOf(args.length)));
                    case "get" -> {
                        if (!(sender instanceof Player player)) {
                            sender.sendMessage(TranslatableChatComponent.read("command.wrong_executor"));
                            return;
                        }
                        Player target = new PlayerArgument().parse(args[2]);
                        if (target == null) {
                            player.sendMessage(TranslatableChatComponent.read("fallbackCommand.player_not_recognized").replace("%s", args[2]));
                            return;
                        }
                        String[] permissions = Permission.getPermissions(target);
                        player.sendMessage(TranslatableChatComponent.read("permissionCommand.player_permissions").replace("%s", target.getName()));
                        for (String permission : permissions) {
                            player.sendMessage("\u00A76- \u00A7a" + permission);
                        }
                    }
                }
            }
            case 4 -> {
                switch (args[1]) {
                    case "get" -> sender.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_few_arguments").replace("%%s", ChatFormatter.valueOf(3)).replace("%s", ChatFormatter.valueOf(args.length)));
                    case "set" -> {
                        if (sender instanceof Player player) {
                            Player target = new PlayerArgument().parse(args[2]);
                            if (target == null) {
                                player.sendMessage(TranslatableChatComponent.read("fallbackCommand.player_not_recognized").replace("%s", args[2]));
                                return;
                            }
                            String permissionName = args[3];
                            Permission permissionToAssign = null;
                            for (Permission permission : Permission.values()) {
                                if (permission.getName().equals(permissionName)) {
                                    permissionToAssign = permission;
                                }
                            }
                            if (permissionToAssign == null) {
                                player.sendMessage(TranslatableChatComponent.read("permissionCommand.permission_not_found").replace("%s", args[3]));
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
                            Player target = new PlayerArgument().parse(args[2]);
                            if (target == null) {
                                console.sendMessage(TranslatableChatComponent.read("fallbackCommand.player_not_recognized").replace("%s", args[2]));
                                return;
                            }
                            String permissionName = args[3];
                            Permission permissionToAssign = null;
                            for (Permission permission : Permission.values()) {
                                if (permission.getName().equals(permissionName)) {
                                    permissionToAssign = permission;
                                }
                            }
                            if (permissionToAssign == null) {
                                console.sendMessage(TranslatableChatComponent.read("permissionCommand.permission_not_found").replace("%s", args[3]));
                                return;
                            }
                            if (Permission.hasPermission(target, permissionToAssign)) {
                                console.sendMessage(TranslatableChatComponent.read("permissionCommand.permission_already_assigned"));
                                return;
                            }
                            Permission.addPermission(target, permissionToAssign);

                            target.sendMessage(TranslatableChatComponent.read("permissionCommand.assigned_permission").replace("%s", permissionToAssign.getName()));
                            console.sendMessage(TranslatableChatComponent.read("permissionCommand.assigned_permission_other").replace("%%s", permissionToAssign.getName()).replace("%s", target.getName()));
                        }
                    }
                    case "remove" -> {
                        if (sender instanceof Player player) {
                            Player target = new PlayerArgument().parse(args[2]);
                            if (target == null) {
                                player.sendMessage(TranslatableChatComponent.read("fallbackCommand.player_not_recognized").replace("%s", args[2]));
                                return;
                            }
                            String permissionName = args[3];
                            Permission permissionToRemove = null;
                            for (Permission permission : Permission.values()) {
                                if (permission.getName().equals(permissionName)) {
                                    permissionToRemove = permission;
                                }
                            }
                            if (permissionToRemove == null) {
                                player.sendMessage(TranslatableChatComponent.read("permissionCommand.permission_not_found").replace("%s", args[3]));
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
                            Player target = new PlayerArgument().parse(args[2]);
                            if (target == null) {
                                console.sendMessage(TranslatableChatComponent.read("fallbackCommand.player_not_recognized").replace("%s", args[2]));
                                return;
                            }
                            String permissionName = args[3];
                            Permission permissionToRemove = null;
                            for (Permission permission : Permission.values()) {
                                if (permission.getName().equals(permissionName)) {
                                    permissionToRemove = permission;
                                }
                            }
                            if (permissionToRemove == null) {
                                console.sendMessage(TranslatableChatComponent.read("permissionCommand.permission_not_found").replace("%s", args[3]));
                                return;
                            }
                            if (!Permission.hasPermission(target, permissionToRemove)) {
                                console.sendMessage(TranslatableChatComponent.read("permissionCommand.permission_not_present"));
                                return;
                            }
                            Permission.removePermission(target, permissionToRemove);

                            target.sendMessage(TranslatableChatComponent.read("permissionCommand.removed_permission").replace("%s", permissionToRemove.getName()));
                            console.sendMessage(TranslatableChatComponent.read("permissionCommand.removed_permission_other").replace("%%s", permissionToRemove.getName()).replace("%s", target.getName()));
                        }
                    }
                }
            }
            default -> sender.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_many_arguments").replace("%%s", ChatFormatter.valueOf(4)).replace("%s", ChatFormatter.valueOf(args.length)));
        }
    }
}
