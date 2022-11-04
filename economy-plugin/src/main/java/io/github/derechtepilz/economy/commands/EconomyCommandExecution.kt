package io.github.derechtepilz.economy.commands

import io.github.derechtepilz.economy.Main
import io.github.derechtepilz.economy.componentapi.ChatComponentAPI
import io.github.derechtepilz.economy.itemmanagement.Item
import io.github.derechtepilz.economy.permissionmanagement.Permission
import io.github.derechtepilz.economy.permissionmanagement.PermissionGroup
import io.github.derechtepilz.economy.utility.DataHandler
import io.github.derechtepilz.economycore.EconomyAPI
import io.github.derechtepilz.economycore.exceptions.BalanceException
import net.md_5.bungee.api.chat.ClickEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta

class EconomyCommandExecution(private val main: Main) {

    fun buyAuction(player: Player, args: Array<Any>) {
        if (!main.inventoryHandler.isTimerRunning) {
            player.sendMessage("§cThe auctions are currently paused. Try again later!")
            return
        }
        DataHandler.setBuyMenuData(player)
        player.sendMessage("§aYou opened the buy menu!")
    }

    fun buyAuctionWithFilter(player: Player, args: Array<Any>) {
        if (!main.inventoryHandler.isTimerRunning) {
            player.sendMessage("§cThe auctions are currently paused. Try again later!")
            return
        }
        val filter = args[0] as ItemStack
        DataHandler.setBuyMenuData(player, filter)
        player.sendMessage("§aYou opened the buy menu!")
    }

    fun createAuction(player: Player, args: Array<Any>) {
        val item = args[0] as ItemStack
        val amount = args[1] as Int
        val price = args[2] as Double
        val duration = args[3] as Int * 60 * 60 + args[4] as Int * 60 + args[5] as Int
        item.amount = amount

        for (i in 0 until player.inventory.size) {
            if (player.inventory.getItem(i) == null) {
                continue
            }
            val currentItem = player.inventory.getItem(i)!!
            val meta: ItemMeta = currentItem.itemMeta!!

            val itemDamage: Int = if (meta is Damageable) {
                meta.damage
            } else {
                0
            }

            val customModelData = if (meta.hasCustomModelData()) {
                meta.customModelData
            } else {
                -1
            }

            if (currentItem.type == item.type) {
                if (currentItem.amount >= amount) {
                    val offer = Item(main, item.type, amount, price, player.uniqueId, duration, meta.displayName, meta.enchants, itemDamage, customModelData)
                    offer.register()
                    currentItem.amount = currentItem.amount - item.amount
                    player.inventory.setItem(i, currentItem)
                    player.sendMessage(
                        "§aYou created a new offer for §6$amount §aitems of type §6minecraft:" + item.type.name.lowercase() + "§a! It will last §6" + duration + " §aseconds!"
                    )
                } else {
                    player.sendMessage("§cYou have to few items of type §6minecraft:" + item.type.name.lowercase() + " §cin your inventory to offer §6" + amount + " §citems!")
                }
                return
            }
        }
        player.sendMessage("§cYou do not have the specified item in your inventory.")
    }

    fun cancelAuction(player: Player, args: Array<Any>) {
        if (!main.inventoryHandler.isTimerRunning) {
            player.sendMessage("§cThe auctions are currently paused. Try again later!")
            return
        }
        val canOpenCancelMenu = main.offeringPlayerUuids.contains(player.uniqueId)
        if (!canOpenCancelMenu) {
            player.sendMessage("§cDidn't open cancel menu because you didn't auction an item!")
            return
        }
        DataHandler.setCancelMenuData(player)
        player.sendMessage("§aYou opened the cancel menu!")
    }

    fun claimAuction(player: Player, args: Array<Any>) {
        if (!main.expiredItems.containsKey(player.uniqueId)) {
            player.sendMessage("§cYou cannot claim any items back because no expired auction could be found that you created!")
            return
        }
        val expiredItems = main.expiredItems[player.uniqueId]!!.size
        val freeSlots = main.expiredOfferMenu.getFreeSlots(player)
        if (freeSlots == 0) {
            player.sendMessage("§cPlease make sure you have at least §6$expiredItems §cslots free!")
            return
        }
        main.expiredOfferMenu.openInventory(player)
    }

    fun pauseAuction(player: Player, args: Array<Any>) {
        if (!main.inventoryHandler.isTimerRunning) {
            player.sendMessage("§cThe auctions are already paused!")
            return
        }
        main.inventoryHandler.isTimerRunning = false
        Bukkit.broadcastMessage("§cThe auctions are now paused!")
    }

    fun resumeAuction(player: Player, args: Array<Any>) {
        if (main.inventoryHandler.isTimerRunning) {
            player.sendMessage("§cThe auctions are already running!")
            return
        }
        main.inventoryHandler.isTimerRunning = true
        Bukkit.broadcastMessage("§aThe auctions are now running!")
    }

    fun giveCoins(player: Player, args: Array<Any>) {
        try {
            val target = args[0] as Player
            val amount = args[1] as Double
            val success = EconomyAPI.addCoinsToBalance(target, amount)
            if (success) {
                player.sendMessage("§aYou gave §b" + target.name + " §6" + amount + " §acoins!")
                target.sendMessage("§aYou were given §6$amount §acoins!")
                return
            }
            player.sendMessage("§cSomething went wrong! Maybe tell §b" + target.name + " §cto re-join the server!")
        } catch (exception: BalanceException) {
            player.sendMessage("§c" + exception.message)
        }
    }

    fun takeCoins(player: Player, args: Array<Any>) {
        try {
            val target = args[0] as Player
            val amount = args[1] as Double
            val success = EconomyAPI.removeCoinsFromBalance(target, amount)
            if (success) {
                player.sendMessage("§aYou took §6" + amount + " §acoins from §b" + target.name + "§a!")
                target.sendMessage("§aYou have been taken §6$amount §acoins!")
                return
            }
            player.sendMessage("§cSomething went wrong! Maybe tell §b" + target.name + " §cto re-join the server!")
        } catch (exception: BalanceException) {
            player.sendMessage("§c" + exception.message)
        }
    }

    fun setCoins(player: Player, args: Array<Any>) {
        try {
            val target = args[0] as Player
            val amount = args[1] as Double
            val success = EconomyAPI.setBalance(target, amount)
            if (success) {
                player.sendMessage("§aYou set §b" + target.name + "§a's balance to §6" + amount + " §acoins!")
                target.sendMessage("§aYour balance has been set to §6$amount §acoins!")
                return
            }
            player.sendMessage("§cSomething went wrong! Maybe tell §b" + target.name + " §cto re-join the server!")
        } catch (exception: BalanceException) {
            player.sendMessage("§c" + exception.message)
        }
    }

    fun balTop(player: Player, args: Array<Any>) {
        Bukkit.getScheduler().runTaskAsynchronously(main, Runnable {
            player.sendMessage("§7Loading baltop... Please wait!")
            val playerBalances = main.database.serverBalances
            val balances = main.database.balances
            balances.sortWith(Comparator.naturalOrder())
            player.sendMessage("§6This is the current baltop list:")
            var i = 0
            while (i < balances.size && i <= 9) {
                player.sendMessage("§6" + (i + 1) + ". §7- §a" + Bukkit.getOfflinePlayer(playerBalances[balances[i]]!!).name)
                i++
            }
        })
    }

    fun clearPermissions(player: Player, args: Array<Any>) {
        Permission.clearPermissions(args[0] as Player)
        player.sendMessage("§cYou removed every permission from §b" + (args[0] as Player).name + "§c!")
    }

    @Suppress("UNCHECKED_CAST")
    fun setSinglePermission(player: Player, args: Array<Any>) {
        val target = args[0] as Player
        val permissions = args[1] as List<String>
        for (permissionName in permissions) {
            var permissionToAssign: Permission? = null
            for (permission in Permission.values()) {
                if (permission.getPermission() == permissionName) {
                    permissionToAssign = permission
                }
            }
            if (permissionToAssign == null) {
                player.sendMessage("§cThe permission §6$permissionName §cwas not found!")
                if (permissions.indexOf(permissionName) != permissions.size - 1) {
                    continue
                }
                return
            }
            if (Permission.hasPermission(target, permissionToAssign)) {
                player.sendMessage("§cThe player §b" + target.name + " §calready has the permission §6" + permissionName + "§c!")
                if (permissions.indexOf(permissionName) != permissions.size - 1) {
                    continue
                }
                return
            }
            Permission.addPermission(target, permissionToAssign)
            if (target == player) {
                player.sendMessage("§aYou have got the permission §6" + permissionToAssign.getPermission() + "§a!")
            } else {
                target.sendMessage("§aYou have got the permission §6" + permissionToAssign.getPermission() + "§a!")
                player.sendMessage("§aSUCCESS! The permission §6" + permissionToAssign.getPermission() + " &awas given to §b" + target.name + "§a!")
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun removeSinglePermission(player: Player, args: Array<Any>) {
        val target = args[0] as Player
        val permissions = args[1] as List<String>
        for (permissionName in permissions) {
            var permissionToRemove: Permission? = null
            for (permission in Permission.values()) {
                if (permission.getPermission() == permissionName) {
                    permissionToRemove = permission
                }
            }
            if (permissionToRemove == null) {
                player.sendMessage("§cThe permission §6$permissionName §cwas not found!")
                if (permissions.indexOf(permissionName) != permissions.size - 1) {
                    continue
                }
                return
            }
            if (!Permission.hasPermission(target, permissionToRemove)) {
                player.sendMessage("§cThe player §b" + target.name + " §cdoes not have the permission §6" + permissionName + "§c!")
                if (permissions.indexOf(permissionName) != permissions.size - 1) {
                    continue
                }
                return
            }
            Permission.removePermission(player, permissionToRemove)
            if (target == player) {
                player.sendMessage("§cYou have been taken the permission §6" + permissionToRemove.getPermission() + "§c!")
            } else {
                target.sendMessage("§cYou have been taken the permission §6" + permissionToRemove.getPermission() + "§c!")
                player.sendMessage("§cThe permission §6" + permissionToRemove.getPermission() + " §cwas taken from §b" + target.name + "§c!")
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun setPermissionGroup(player: Player, args: Array<Any>) {
        val target = args[0] as Player
        val permissionGroups = args[1] as List<String>
        for (group in permissionGroups) {
            if (!PermissionGroup.exists(group)) {
                player.sendMessage("§cThe permission group §6$group §cwas not found!")
                return
            }
            if (PermissionGroup.hasPermissionGroup(target, group)) {
                player.sendMessage("§cThe player §b${target.name} §calready has the permission group §6$group§c!")
                return
            }
            PermissionGroup.setPermissionGroup(target, group)
            if (target == player) {
                player.sendMessage("§cYou have been given the permission §6$group§c!")
            } else {
                target.sendMessage("§cYou have been given the permission §6$group§c!")
                player.sendMessage("§cThe permission §6$group §cwas given to §b${target.name}§c!")
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun removePermissionGroup(player: Player, args: Array<Any>) {
        val target = args[0] as Player
        val permissionGroups = args[1] as List<String>
        for (group in permissionGroups) {
            if (!PermissionGroup.exists(group)) {
                player.sendMessage("§cThe permission group §6$group §cwas not found!")
                return
            }
            if (!PermissionGroup.hasPermissionGroup(target, group)) {
                player.sendMessage("§cThe player §b${target.name} §cdoes not have the permission group §6$group§c!")
                return
            }
            PermissionGroup.removePermissionGroup(target, group)
            if (target == player) {
                player.sendMessage("§cYou have been taken the permission §6$group§c!")
            } else {
                target.sendMessage("§cYou have been taken the permission §6$group§c!")
                player.sendMessage("§cThe permission §6$group §cwas taken from §b${target.name}§c!")
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun registerPermissionGroup(player: Player, args: Array<Any>) {
        val groupName = args[0] as String
        val permissions = args[1] as MutableList<String>
        if (PermissionGroup.exists(groupName)) {
            player.sendMessage("§cThe permission group §6$groupName §calready exists! Please delete it if you want to re-create it!")
            return
        }
        PermissionGroup.registerPermissionGroup(groupName, permissions)
        player.sendMessage("§aThe permission group §6$groupName §awas created successfully!")
    }

    fun deletePermissionGroup(player: Player, args: Array<Any>) {
        val groupName = args[0] as String
        if (!PermissionGroup.exists(groupName)) {
            player.sendMessage("§cThe permission group §6$groupName §cdoes not exist! Please create it first if you want to delete it!")
            return
        }
        PermissionGroup.deletePermissionGroup(groupName)
        player.sendMessage("§cThe permission group §6$groupName §cwas deleted successfully!")
    }

    fun getSinglePermission(player: Player, args: Array<Any>) {
        val target = args[0] as Player
        val permissions = Permission.getPermissions(target)
        player.sendMessage("§b" + target.name + " §ahas the following permissions:")
        for (permission in permissions) {
            player.sendMessage("§6- §a$permission")
        }
    }

    fun addFriend(player: Player, args: Array<Any>) {
        val target = args[0] as Player
        if (target.uniqueId == player.uniqueId) {
            player.sendMessage("§cYou cannot use this command on yourself!")
            ChatComponentAPI().appendText("§7[Click here to try someone else]")
                .withClickEvent("/economy friend add ", ClickEvent.Action.SUGGEST_COMMAND)
                .sendToPlayer(player)
            return
        }
        if (main.friend.isFriend(player.uniqueId, target.uniqueId)) {
            player.sendMessage("§7You are already friends with §6" + target.name + "§7!")
            return
        }
        // Here the cooldown status should be checked
        main.friendRequest.addFriendRequest(player.uniqueId, target.uniqueId)
        ChatComponentAPI().appendText("§7Accept friend request from §6" + player.name + "§7?")
            .appendTextComponent(ChatComponentAPI().appendText("§a[YES]").withClickEvent("/friend accept " + player.name, ClickEvent.Action.RUN_COMMAND).showTextOnHover("§7By clicking this you are going to §aaccept §7this friend request"))
            .appendTextComponent(ChatComponentAPI().appendText("§c[NO]").withClickEvent("/friend deny " + player.name, ClickEvent.Action.RUN_COMMAND).showTextOnHover("§7By clicking this you are going to §cdeny §7this friend request"))
            .sendToPlayer(target)
    }

    fun removeFriend(player: Player, args: Array<Any>) {
        val target = args[0] as Player
        if (target.uniqueId == player.uniqueId) {
            player.sendMessage("§cYou cannot use this command on yourself!")
            ChatComponentAPI().appendText("§7[Click here to try someone else]")
                .withClickEvent("/economy friend remove ", ClickEvent.Action.SUGGEST_COMMAND)
                .sendToPlayer(player)
            return
        }
        if (!main.friend.isFriend(player.uniqueId, target.uniqueId)) {
            player.sendMessage("§7You are not friends with §6" + target.name + "§7!")
            return
        }
        main.friend.removeFriend(player.uniqueId, target.uniqueId, false)

        player.sendMessage("§7You removed §6" + target.name + "§7from your friends list!")
        target.sendMessage("§6" + player.name + " §7has removed you from their friends list!")
    }

    fun acceptFriend(player: Player, args: Array<Any>) {
        val target = args[0] as Player // This is the player who has sent the friend request

        if (player.uniqueId == target.uniqueId) {
            player.sendMessage("§cYou cannot use this command on yourself!")
            ChatComponentAPI().appendText("§7[Click here to try someone else!]")
                .withClickEvent("/economy friend accept ", ClickEvent.Action.SUGGEST_COMMAND)
                .sendToPlayer(player)
            return
        }
        if (!main.friendRequest.isFriendRequest(target.uniqueId, player.uniqueId)) {
            player.sendMessage("§7You don't have an incoming friend request from §6" + target.name + "§7!")
            return
        }
        main.friendRequest.removeFriendRequest(target.uniqueId, player.uniqueId)
        main.friend.addFriend(target.uniqueId, player.uniqueId, false)

        player.sendMessage("§7You are now friends with §6" + target.name + "§7!")
        target.sendMessage("§7You are now friends with §6" + player.name + "§7!")
    }

    fun denyFriend(player: Player, args: Array<Any>) {
        val target = args[0] as Player // This is the player who has sent the friend request

        if (player.uniqueId == target.uniqueId) {
            player.sendMessage("§cYou cannot use this command on yourself!")
            ChatComponentAPI().appendText("§7[Click here to try someone else!]")
                .withClickEvent("/economy friend deny ", ClickEvent.Action.SUGGEST_COMMAND)
                .sendToPlayer(player)
            return
        }
        if (!main.friendRequest.isFriendRequest(target.uniqueId, player.uniqueId)) {
            player.sendMessage("§7You don't have an incoming friend request from §6" + target.name + "§7!")
            return
        }
        main.friendRequest.removeFriendRequest(target.uniqueId, player.uniqueId)

        player.sendMessage("§7You denied the incoming friend request from §6" + target.name + "§7!")
        target.sendMessage("§7You friend request to §6" + player.name + " §7was denied!")
    }

    fun allowDirectDownloads(player: Player, args: Array<Any>) {
        val allowDirectDownloads = args[0] as Boolean
        main.pluginConfig.set("allowDirectDownloads", allowDirectDownloads.toString())

        player.sendMessage("§7You just set §6allowDirectDownloads §7to §6$allowDirectDownloads§7!")
        if (allowDirectDownloads) {
            player.sendMessage("§7If a new update is available, the plugin will be automatically updated!")
            return
        }
        player.sendMessage("§7If a new update is available, you will have to download it yourself!")
    }

    fun startBalance(player: Player, args: Array<Any>) {
        val startBalance = args[0] as Double
        EconomyAPI.setStartBalance(startBalance)
        player.sendMessage("§7You set the start balance to §6$startBalance§7!")
    }

    fun interestRate(player: Player, args: Array<Any>) {
        val interestRate = args[0] as Double
        EconomyAPI.setInterestRate(interestRate)
        player.sendMessage("§7You set the interest rate to §6$interestRate§7!")
    }

    fun minimumDaysForInterest(player: Player, args: Array<Any>) {
        val minimumDaysForInterest = args[0] as Int
        EconomyAPI.setMinimumDaysForInterest(minimumDaysForInterest)
        player.sendMessage("§7You set the minimum days to get interest to §6$minimumDaysForInterest§7!")
    }

    fun resetConfig(player: Player, args: Array<Any>) {
        main.pluginConfig.resetConfig()
        EconomyAPI.resetConfigValues()
        player.sendMessage("§7The config has been reset!")
    }

    fun reloadConfig(player: Player, args: Array<Any>) {
        main.pluginConfig.reloadConfig()
        player.sendMessage("§7The config has been reloaded!")
    }

}