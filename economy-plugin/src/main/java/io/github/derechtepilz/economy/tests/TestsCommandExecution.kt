package io.github.derechtepilz.economy.tests

import io.github.derechtepilz.economy.tests.inventory.InventoryTest
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class TestsCommandExecution(private val inventoryTest: InventoryTest) {

    fun inventory(commandSender: CommandSender, args: Array<Any>) {
        inventoryTest.openInventory(commandSender as Player)
    }

    fun regenerate(commandSender: CommandSender, args: Array<Any>) {
        val items = args[0] as Int
        inventoryTest.regenerateInventory(items)
    }

}