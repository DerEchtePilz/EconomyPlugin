package me.derechtepilz.economy.utility;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta meta;

    private final Material material;

    public ItemBuilder(Material material) {
        item = new ItemStack(material);
        this.material = material;
        meta = item.getItemMeta();
    }

    public ItemBuilder setName(String name) {
        meta.setDisplayName(name);
        return this;
    }

    public ItemBuilder setDescription(String... description) {
        List<String> lore = new ArrayList<>();
        for (String content : description) {
            lore.add("§7" + content);
        }
        meta.setLore(lore);
        return this;
    }

    public ItemBuilder setTexture(String playerName) {
        if (material.equals(Material.PLAYER_HEAD)) {
            SkullMeta meta = (SkullMeta) this.meta;
            Player player = Bukkit.getPlayer(playerName);
            if (player == null) return null;
            PlayerProfile profile = player.getPlayerProfile();
            meta.setOwnerProfile(profile);
        }
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }
}
