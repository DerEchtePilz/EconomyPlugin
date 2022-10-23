package io.github.derechtepilz.economy.inventorymanagement;

import io.github.derechtepilz.economy.itemmanagement.Item;
import io.github.derechtepilz.economy.Main;

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
