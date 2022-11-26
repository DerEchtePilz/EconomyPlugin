package io.github.derechtepilz.economy.utility;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Map;

public class ItemBuilder {

	private final ItemStack item;
	private final ItemMeta meta;

	public ItemBuilder(Material material) {
		item = new ItemStack(material, 1);
		meta = (item.getItemMeta() instanceof Damageable) ? (Damageable) item.getItemMeta() : item.getItemMeta();
	}

	public ItemBuilder setName(String name) {
		if (name != null) {
			meta.setDisplayName(name);
		}
		return this;
	}

	public ItemBuilder setAmount(int amount) {
		item.setAmount(amount);
		return this;
	}

	public ItemBuilder setEnchantments(Map<Enchantment, Integer> enchantments) {
		for (Enchantment enchantment : enchantments.keySet()) {
			meta.addEnchant(enchantment, enchantments.get(enchantment), false);
		}
		return this;
	}

	public ItemBuilder setDamage(int damage) {
		if (meta instanceof Damageable) {
			((Damageable) meta).setDamage(damage);
		}
		return this;
	}

	public ItemBuilder setCustomModelData(int customModelData) {
		if (customModelData >= 0) {
			meta.setCustomModelData(customModelData);
		}
		return this;
	}

	public ItemBuilder setDescription(List<String> lore) {
		meta.setLore(lore);
		return this;
	}

	public <T, Z> ItemBuilder setData(NamespacedKey key, PersistentDataType<T, Z> type, Z value) {
		meta.getPersistentDataContainer().set(key, type, value);
		return this;
	}

	public ItemStack build() {
		item.setItemMeta(meta);
		return item;
	}

}
