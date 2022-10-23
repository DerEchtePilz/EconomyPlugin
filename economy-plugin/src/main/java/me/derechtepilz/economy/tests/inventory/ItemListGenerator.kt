package me.derechtepilz.economy.tests.inventory

import me.derechtepilz.economy.utility.ItemBuilder
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

object ItemListGenerator {

    private val characters: CharArray = charArrayOf(
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    )

    @JvmStatic
    fun generateItemList(size: Int): Array<ItemStack> {
        val itemList: MutableList<ItemStack> = mutableListOf()
        for (i in 0 until size) {
            val stringBuilder: StringBuilder = StringBuilder()
            for (j in 0..9) {
                stringBuilder.append(characters[Random.nextInt(0, characters.size - 1)])
            }
            itemList.add(ItemBuilder(generateMaterial()).setName("§f$stringBuilder").build())
        }
        return itemList.toTypedArray()
    }

    @JvmStatic
    private fun generateMaterial(): Material {
        val random: Random = Random
        var itemMaterial: Material = Material.values()[random.nextInt(0, Material.values().size - 1)]
        while (!itemMaterial.isItem) {
            itemMaterial = Material.values()[random.nextInt(0, Material.values().size - 1)]
        }
        return itemMaterial
    }

}