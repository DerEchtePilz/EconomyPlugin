package io.github.derechtepilz.economy.tests

import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.IntegerArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import io.github.derechtepilz.economy.Main

import dev.jorel.commandapi.arguments.LiteralArgument.of

class TestsCommand(main: Main) {

    private val inventoryTest = main.inventoryTest

    fun register() {
        CommandTree("test")
            .then(of("inventory")
                .executesPlayer(PlayerCommandExecutor { player, _ ->
                    inventoryTest.openInventory(player)
                })
                .then(of("regenerate")
                    .then(IntegerArgument("items", 0)
                        .executesPlayer(PlayerCommandExecutor { _, args ->
                            val items = args[0] as Int
                            inventoryTest.regenerateInventory(items)
                        })
                    )
                )
            )
            .register()
    }

}