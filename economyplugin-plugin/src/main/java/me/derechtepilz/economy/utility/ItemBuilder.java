package me.derechtepilz.economy.utility;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta meta;

    private final Material material;
    private String playerHeadTexture;
    private String playerHeadSignature;

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.material = material;
        this.meta = item.getItemMeta();
    }

    public ItemBuilder(Material material, int amount) {
        this.item = new ItemStack(material, amount);
        this.material = material;
        this.meta = item.getItemMeta();
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
        try {
            if (material.equals(Material.PLAYER_HEAD)) {
                SkullMeta skullMeta = (SkullMeta) this.meta;
                Player player = Bukkit.getPlayer(playerName);
                GameProfile profile = new GameProfile(player.getUniqueId(), playerName);
                getProfile(String.valueOf(player.getUniqueId()));
                profile.getProperties().put("textures", new Property("textures", playerHeadTexture, playerHeadSignature));

                Field profileField = skullMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(skullMeta, profile);
                return this;
            }
        } catch (IOException | NoSuchFieldException | IllegalAccessException ignored) {}
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }

    private void getProfile(String uuid) throws IOException {
        APIRequest profileRequest = new APIRequest();
        JsonElement profileElement = JsonParser.parseString(profileRequest.request("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false"));
        JsonObject profileObject = profileElement.getAsJsonObject();
        JsonArray properties = profileObject.getAsJsonArray("properties");
        playerHeadTexture = properties.get(0).getAsJsonObject().getAsJsonPrimitive("value").getAsString();
        playerHeadSignature = properties.get(0).getAsJsonObject().getAsJsonPrimitive("signature").getAsString();
    }
}
