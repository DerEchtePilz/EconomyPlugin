package me.derechtepilz.economy.inventorymanagement;

import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.itemmanagement.Item;

import java.util.UUID;

public class ItemUpdater {

	private final Main main;

	public ItemUpdater(Main main) {
		this.main = main;
	}

	public void getUpdatedItems() {
		for (UUID uuid : main.getRegisteredItems().keySet()) {
			Item item = main.getRegisteredItems().get(uuid);
			item.decreaseDurationAndUpdate();
		}
	}

}
