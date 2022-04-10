package me.derechtepilz.economy.itemmanager;

import me.derechtepilz.economy.Main;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ItemConverter {

    private final ItemStack item;
    private final ItemMeta meta;

    private final Player creator;
    private UUID itemUuid;

    public ItemConverter(ItemStack item, Player creator, int price) {
        this.item = item;
        this.meta = item.getItemMeta();
        this.creator = creator;

        do {
            itemUuid = UUID.randomUUID();
        } while (Main.getInstance().getOfferedItems().containsKey(itemUuid));

        meta.getPersistentDataContainer().set(Main.getInstance().getCreator(), PersistentDataType.STRING, String.valueOf(creator.getUniqueId()));
        meta.getPersistentDataContainer().set(Main.getInstance().getUuid(), PersistentDataType.STRING, String.valueOf(itemUuid));
        meta.getPersistentDataContainer().set(Main.getInstance().getPrice(), PersistentDataType.INTEGER, price);

        buildOfferedItem();
    }

    public ItemConverter(ItemStack item, Player canceller) {
        this.item = item;
        this.meta = item.getItemMeta();
        this.creator = canceller;
        this.itemUuid = UUID.fromString(meta.getPersistentDataContainer().get(Main.getInstance().getUuid(), PersistentDataType.STRING));

        meta.getPersistentDataContainer().remove(Main.getInstance().getCreator());
        meta.getPersistentDataContainer().remove(Main.getInstance().getPrice());
        meta.getPersistentDataContainer().remove(Main.getInstance().getUuid());

        buildCancelledItem();
    }

    public ItemConverter(ItemStack item, int price) {
        this.creator = null;
        this.item = item;
        this.meta = item.getItemMeta();

        do {
            itemUuid = UUID.randomUUID();
        } while (Main.getInstance().getOfferedItems().containsKey(itemUuid));

        meta.getPersistentDataContainer().set(Main.getInstance().getPrice(), PersistentDataType.INTEGER, price);
        meta.getPersistentDataContainer().set(Main.getInstance().getUuid(), PersistentDataType.STRING, String.valueOf(itemUuid));
        meta.getPersistentDataContainer().set(Main.getInstance().getCreator(), PersistentDataType.STRING, "console");

        buildConsoleOfferedItem();
    }

    public ItemConverter(ItemStack item) {
        this.item = item;
        this.meta = item.getItemMeta();
        this.creator = null;
        this.itemUuid = UUID.fromString(meta.getPersistentDataContainer().get(Main.getInstance().getUuid(), PersistentDataType.STRING));

        buildConsoleCancelledItem();
    }

    private void buildOfferedItem() {
        item.setItemMeta(meta);

        ItemStack[] offeredItems = new ItemStack[Main.getInstance().getOfferingPlayers().get(creator.getUniqueId()).length + 1];

        for (int i = 0; i < Main.getInstance().getOfferingPlayers().get(creator.getUniqueId()).length; i++) {
            offeredItems[i] = Main.getInstance().getOfferingPlayers().get(creator.getUniqueId())[i];
        }

        offeredItems[Main.getInstance().getOfferingPlayers().get(creator.getUniqueId()).length] = item;

        Main.getInstance().getOfferedItemsList().add(item);
        Main.getInstance().getOfferingPlayers().put(creator.getUniqueId(), offeredItems);
        Main.getInstance().getOfferedItems().put(itemUuid, item);
    }

    private void buildConsoleOfferedItem() {
        item.setItemMeta(meta);

        ItemStack[] offeredItems = new ItemStack[Main.getInstance().getSpecialOffers().get("console").length + 1];

        for (int i = 0; i < Main.getInstance().getSpecialOffers().get("console").length; i++) {
            offeredItems[i] = Main.getInstance().getSpecialOffers().get("console")[i];
        }

        offeredItems[Main.getInstance().getSpecialOffers().get("console").length] = item;

        Main.getInstance().getSpecialOffers().put("console", offeredItems);
        Main.getInstance().getOfferedItems().put(itemUuid, item);
    }

    private void buildCancelledItem() {
        Main.getInstance().getOfferedItemsList().remove(item);
        Main.getInstance().getOfferedItems().remove(itemUuid);

        ItemStack[] offeredPlayerItems = Main.getInstance().getOfferingPlayers().get(creator.getUniqueId());
        List<ItemStack> offeredItems = new ArrayList<>(Arrays.asList(offeredPlayerItems));
        offeredItems.remove(item);
        offeredPlayerItems = (ItemStack[]) offeredItems.toArray();

        item.setItemMeta(meta);

        Main.getInstance().getOfferingPlayers().put(creator.getUniqueId(), offeredPlayerItems);
    }

    private void buildConsoleCancelledItem() {
        Main.getInstance().getOfferedItemsList().remove(item);
        Main.getInstance().getOfferedItems().remove(itemUuid);

        ItemStack[] offeredConsoleItems = Main.getInstance().getSpecialOffers().get("console");
        List<ItemStack> consoleItems = new ArrayList<>(Arrays.asList(offeredConsoleItems));
        consoleItems.remove(item);
        offeredConsoleItems = (ItemStack[]) consoleItems.toArray();

        item.setItemMeta(meta);

        Main.getInstance().getSpecialOffers().put("console", offeredConsoleItems);
    }
}
