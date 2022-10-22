package me.derechtepilz.economy.itemmanagement;

import com.google.gson.*;
import me.derechtepilz.economy.Main;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class LoadItem {

	private final Main main;

	public LoadItem(Main main) {
		this.main = main;
	}

	@SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions", "unchecked"})
	public void loadItems() {
		try {
			File file = new File("./plugins/Economy");
			if (!file.exists()) {
				file.mkdir();
			}
			File items = new File(file, "items.json");
			BufferedReader reader = new BufferedReader(new FileReader(items));

			String line;
			StringBuilder buildSavedItems = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				buildSavedItems.append(line);
			}

			JsonObject savedItems = JsonParser.parseString(buildSavedItems.toString()).getAsJsonObject();

			boolean auctionRunning = savedItems.get("auctionRunning").getAsBoolean();
			main.getInventoryHandler().setTimerRunning(auctionRunning);

			JsonArray runningAuctions = savedItems.get("runningAuctions").getAsJsonArray();
			JsonArray expiredAuctions = savedItems.get("expiredAuctions").getAsJsonArray();

			for (int i = 0; i < runningAuctions.size(); i++) {
				JsonObject itemObject = runningAuctions.get(i).getAsJsonObject();

				Material material = Material.getMaterial(itemObject.get("material").getAsString());
				int amount = itemObject.get("amount").getAsInt();
				double price = itemObject.get("price").getAsDouble();
				UUID seller = UUID.fromString(itemObject.get("seller").getAsString());
				UUID uuid = UUID.fromString(itemObject.get("uuid").getAsString());
				int duration = itemObject.get("duration").getAsInt();

				String displayName = itemObject.get("displayName").getAsString();
				int damage = itemObject.get("damage").getAsInt();
				int customModelData = itemObject.get("customModelData").getAsInt();

				JsonElement enchantmentsElement = itemObject.get("enchantments");
				Gson gson = new Gson();
				Map<String, Integer> rawEnchantments = gson.fromJson(enchantmentsElement, Map.class);

				Item item = new Item(main, material, amount, price, seller, uuid, duration, displayName, convertEnchantments(rawEnchantments), damage, customModelData);
				item.register();
				main.getLogger().info("Registered auction: " + item);
			}

			for (int i = 0; i < expiredAuctions.size(); i++) {
				JsonObject itemObject = expiredAuctions.get(i).getAsJsonObject();
				Material material = Material.matchMaterial(itemObject.get("material").getAsString());
				int amount = itemObject.get("amount").getAsInt();
				UUID seller = UUID.fromString(itemObject.get("seller").getAsString());

				List<ItemStack> expiredItems = (main.getExpiredItems().containsKey(seller)) ? main.getExpiredItems().get(seller) : new ArrayList<>();
				expiredItems.add(new ItemStack(material, amount));
				main.getExpiredItems().put(seller, expiredItems);
			}
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	private Map<Enchantment, Integer> convertEnchantments(Map<String, Integer> rawEnchantments) {
		Map<Enchantment, Integer> enchantments = new HashMap<>();
		for (String enchantmentName : rawEnchantments.keySet()) {
			Enchantment enchantment = new EnchantmentWrapper(enchantmentName);
			enchantments.put(enchantment, Integer.valueOf(String.valueOf(rawEnchantments.get(enchantmentName)).split("\\.")[0]));
		}
		return enchantments;
	}

}
