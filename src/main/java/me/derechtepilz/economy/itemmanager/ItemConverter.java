package me.derechtepilz.economy.itemmanager;

import me.derechtepilz.economy.Main;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class ItemConverter {

    private final ItemStack item;
    private final ItemMeta meta;

    private UUID itemUuid;
    private final Player creator;

    public ItemConverter(ItemStack item, Player creator, int price) {
        this.item = item;
        this.meta = item.getItemMeta();
        this.creator = creator;

        do {
            this.itemUuid = UUID.randomUUID();
        } while (Main.getInstance().getOfferedItems().containsKey(this.itemUuid));

        meta.getPersistentDataContainer().set(Main.getInstance().getCreator(), PersistentDataType.STRING, String.valueOf(creator.getUniqueId()));
        meta.getPersistentDataContainer().set(Main.getInstance().getUuid(), PersistentDataType.STRING, String.valueOf(itemUuid));
        meta.getPersistentDataContainer().set(Main.getInstance().getPrice(), PersistentDataType.INTEGER, price);

        buildOfferedItem();
    }

    public ItemConverter(ItemStack item, UUID itemUuid, Player canceller) {
        this.item = item;
        this.meta = item.getItemMeta();
        this.creator = canceller;
        this.itemUuid = itemUuid;

        meta.getPersistentDataContainer().remove(Main.getInstance().getCreator());
        meta.getPersistentDataContainer().remove(Main.getInstance().getUuid());
        meta.getPersistentDataContainer().remove(Main.getInstance().getPrice());

        buildCancelledItem();
    }

    public ItemConverter(ItemStack item, int price) {
        this.creator = null;
        this.item = item;
        this.meta = item.getItemMeta();

        do {
            this.itemUuid = UUID.randomUUID();
        } while (Main.getInstance().getOfferedItems().containsKey(itemUuid));

        meta.getPersistentDataContainer().set(Main.getInstance().getUuid(), PersistentDataType.STRING, String.valueOf(itemUuid));
        meta.getPersistentDataContainer().set(Main.getInstance().getPrice(), PersistentDataType.INTEGER, price);
        meta.getPersistentDataContainer().set(Main.getInstance().getCreator(), PersistentDataType.STRING, "console");

        buildConsoleOfferedItem();
    }

    public ItemConverter(ItemStack item, UUID itemUuid) {
        this.item = item;
        this.meta = item.getItemMeta();
        this.creator = null;
        this.itemUuid = itemUuid;

        buildConsoleCancelledItem();
    }

    private void buildOfferedItem() {
        item.setItemMeta(meta);

        ItemStack[] offeredItems = new ItemStack[Main.getInstance().getOfferingPlayers().get(creator.getUniqueId()).length + 1];

        for (int i = 0; i < Main.getInstance().getOfferingPlayers().get(creator.getUniqueId()).length; i++) {
            offeredItems[i] = Main.getInstance().getOfferingPlayers().get(creator.getUniqueId())[i];
        }

        offeredItems[Main.getInstance().getOfferingPlayers().get(creator.getUniqueId()).length] = item;

        Main.getInstance().getOfferedItems().put(itemUuid, item);
        Main.getInstance().getOfferingPlayers().put(creator.getUniqueId(), offeredItems);
    }

    private void buildConsoleOfferedItem() {
        item.setItemMeta(meta);

        ItemStack[] offeredItems = new ItemStack[Main.getInstance().getSpecialOffers().get("console").length + 1];

        for (int i = 0; i < Main.getInstance().getSpecialOffers().get("console").length; i++) {
            offeredItems[i] = Main.getInstance().getSpecialOffers().get("console")[i];
        }

        offeredItems[Main.getInstance().getSpecialOffers().get("console").length] = item;

        Main.getInstance().getOfferedItems().put(itemUuid, item);
        Main.getInstance().getSpecialOffers().put("console", offeredItems);
    }

    private void buildCancelledItem() {
        if (item.getItemMeta() != null && item.hasItemMeta()) {
            UUID itemUuid = UUID.fromString(item.getItemMeta().getPersistentDataContainer().get(Main.getInstance().getUuid(), PersistentDataType.STRING));
        }

        meta.getPersistentDataContainer().remove(Main.getInstance().getUuid());
        meta.getPersistentDataContainer().remove(Main.getInstance().getPrice());
        meta.getPersistentDataContainer().remove(Main.getInstance().getCreator());

        item.setItemMeta(meta);

        Main.getInstance().getOfferedItems().remove(itemUuid);
        Main.getInstance().getOfferingPlayers().remove(creator.getUniqueId());
    }

    private void buildConsoleCancelledItem() {
        item.setItemMeta(meta);

        Main.getInstance().getOfferedItems().remove(itemUuid);
        Main.getInstance().getSpecialOffers().remove("console");
    }
}
