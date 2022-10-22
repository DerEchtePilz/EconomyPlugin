package me.derechtepilz.economy.itemmanagement;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.derechtepilz.economy.Main;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

public class SaveItem {

	private final Main main;

	public SaveItem(Main main) {
		this.main = main;
	}

	@SuppressWarnings({"ResultOfMethodCallIgnored"})
	public void saveItems() {
		try {
			File file = new File("./plugins/Economy");
			if (!file.exists()) {
				file.mkdir();
			}
			File items = new File(file, "items.json");
			FileWriter writer = new FileWriter(items);

			JsonObject auctionsObject = new JsonObject();
			auctionsObject.addProperty("auctionRunning", main.getInventoryHandler().isTimerRunning());

			JsonArray runningAuctionsArray = new JsonArray();
			for (UUID uuid : main.getRegisteredItems().keySet()) {
				runningAuctionsArray.add(main.getRegisteredItems().get(uuid).saveItem());
			}
			auctionsObject.add("runningAuctions", runningAuctionsArray);

			JsonArray expiredAuctionsArray = new JsonArray();
			for (UUID uuid : main.getExpiredItems().keySet()) {
				for (ItemStack itemStack : main.getExpiredItems().get(uuid)) {
					JsonObject expiredItem = new JsonObject();
					expiredItem.addProperty("material", itemStack.getType().name());
					expiredItem.addProperty("amount", itemStack.getAmount());
					expiredItem.addProperty("seller", String.valueOf(uuid));
					expiredAuctionsArray.add(expiredItem);
				}
			}
			auctionsObject.add("expiredAuctions", expiredAuctionsArray);

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String auctionsObjectJsonString = gson.toJson(auctionsObject);
			writer.write(auctionsObjectJsonString);
			writer.close();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

}
