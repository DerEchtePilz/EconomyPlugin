package me.derechtepilz.economy.itemmanager;

import me.derechtepilz.economy.Main;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ItemConverter {

    private final ItemStack item;
    private final ItemMeta meta;

    private final Player creator;

    public ItemConverter(ItemStack item, Player creator, int price) {
        this.item = item;
        this.meta = item.getItemMeta();
        this.creator = creator;

        meta.getPersistentDataContainer().set(Main.getInstance().getCreator(), PersistentDataType.STRING, String.valueOf(creator.getUniqueId()));
        meta.getPersistentDataContainer().set(Main.getInstance().getPrice(), PersistentDataType.INTEGER, price);

        buildOfferedItem();
    }

    public ItemConverter(ItemStack item, Player canceller) {
        this.item = item;
        this.meta = item.getItemMeta();
        this.creator = canceller;

        meta.getPersistentDataContainer().remove(Main.getInstance().getCreator());
        meta.getPersistentDataContainer().remove(Main.getInstance().getPrice());

        buildCancelledItem();
    }

    public ItemConverter(ItemStack item, int price) {
        this.creator = null;
        this.item = item;
        this.meta = item.getItemMeta();

        meta.getPersistentDataContainer().set(Main.getInstance().getPrice(), PersistentDataType.INTEGER, price);
        meta.getPersistentDataContainer().set(Main.getInstance().getCreator(), PersistentDataType.STRING, "console");

        buildConsoleOfferedItem();
    }

    public ItemConverter(ItemStack item) {
        this.item = item;
        this.meta = item.getItemMeta();
        this.creator = null;

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
    }

    private void buildConsoleOfferedItem() {
        item.setItemMeta(meta);

        ItemStack[] offeredItems = new ItemStack[Main.getInstance().getSpecialOffers().get("console").length + 1];

        for (int i = 0; i < Main.getInstance().getSpecialOffers().get("console").length; i++) {
            offeredItems[i] = Main.getInstance().getSpecialOffers().get("console")[i];
        }

        offeredItems[Main.getInstance().getSpecialOffers().get("console").length] = item;

        Main.getInstance().getSpecialOffers().put("console", offeredItems);
    }

    private void buildCancelledItem() {
        meta.getPersistentDataContainer().remove(Main.getInstance().getPrice());
        meta.getPersistentDataContainer().remove(Main.getInstance().getCreator());

        item.setItemMeta(meta);

        Main.getInstance().getOfferedItemsList().remove(item);
        Main.getInstance().getOfferingPlayers().remove(creator.getUniqueId());
    }

    private void buildConsoleCancelledItem() {
        item.setItemMeta(meta);

        Main.getInstance().getSpecialOffers().remove("console");
    }
}
