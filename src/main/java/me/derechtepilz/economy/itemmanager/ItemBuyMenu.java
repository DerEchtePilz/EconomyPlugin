package me.derechtepilz.economy.itemmanager;

import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.utility.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemBuyMenu implements Listener {

    private final List<ItemStack[]> inventories = new ArrayList<>();
    private int currentInventory;

    private int playerOfferPages;
    private int specialOfferPages;

    private Inventory inventory;

    @EventHandler
    public void onClick(InventoryClickEvent event) {

    }

    public void openBuyMenu(Player player, Material type) {
        ItemStack[] allPlayerOffers = Main.getInstance().getOfferingPlayers().get(player.getUniqueId());
        ItemStack[] allSpecialOffers = Main.getInstance().getSpecialOffers().get("console");

        if (allPlayerOffers.length == 0 && allSpecialOffers.length == 0) {
            player.sendMessage("§cNo offers were found!");
            return;
        }

        List<ItemStack> playerOffersTypes = new ArrayList<>();
        List<ItemStack> specialOffersTypes = new ArrayList<>();

        ItemStack[] playerOffers = new ItemStack[0];
        ItemStack[] specialOffers = new ItemStack[0];

        jumpToPlayerOffers = new ItemBuilder(Material.PLAYER_HEAD).setTexture(player.getName()).setName("§aJump to §bPlayer Offers").build();

        for (ItemStack item : allPlayerOffers) {
            if (item.getType().equals(type)) {
                playerOffersTypes.add(item);
            }
        }

        if (playerOffersTypes.size() > 0) {
            playerOffers = resizeInventoryContents((ItemStack[]) playerOffersTypes.toArray());
        }

        for (ItemStack item : allSpecialOffers) {
            if (item.getType().equals(type)) {
                specialOffersTypes.add(item);
            }
        }

        if (specialOffersTypes.size() > 0) {
            specialOffers = resizeInventoryContents((ItemStack[]) specialOffersTypes.toArray());
        }

        playerOfferPages = playerOffers.length / 45;
        specialOfferPages = specialOffers.length / 45;

        if (playerOfferPages >= 1 && specialOfferPages >= 1) {
            prepareInventoryPages(playerOffers, playerOfferPages, true);
            prepareInventoryPages(specialOffers, specialOfferPages, true);
        } else {
            prepareInventoryPages(playerOffers, playerOfferPages, false);
            prepareInventoryPages(specialOffers, specialOfferPages, false);
        }

        inventory = Bukkit.createInventory(null, 54, "Buy items (1)");
        inventory.setContents(inventories.get(0));
        currentInventory = 0;
        player.openInventory(inventory);
    }

    public void openBuyMenu(Player player, boolean query) {
        ItemStack[] allSpecialOffers = Main.getInstance().getSpecialOffers().get("console");
        if (allSpecialOffers.length == 0) {
            player.sendMessage("§cNo special offers were found!");
            return;
        }
        ItemStack[] specialOffers = resizeInventoryContents(allSpecialOffers);
        specialOfferPages = specialOffers.length / 45;

        prepareInventoryPages(specialOffers, specialOfferPages, false);

        inventory = Bukkit.createInventory(null, 54, "Buy items (1)");
        inventory.setContents(inventories.get(0));
        currentInventory = 0;
        player.openInventory(inventory);
    }

    public void openBuyMenu(Player player) {

    }

    private final ItemStack closeItem = new ItemBuilder(Material.BARRIER).setName("§cClose").build();
    private final ItemStack nextPage = new ItemBuilder(Material.ARROW).setName("§aNext page").build();
    private final ItemStack previousPage = new ItemBuilder(Material.ARROW).setName("§aPrevious page").build();
    private final ItemStack menuGlass = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName("§7").build();
    private final ItemStack jumpToSpecialOffers = new ItemBuilder(Material.NETHER_STAR).setName("§aJump to §dSpecial Offers").build();
    private ItemStack jumpToPlayerOffers;

    private ItemStack[] resizeInventoryContents(ItemStack[] offers) {
        if (offers.length == 45) {
            return offers;
        }
        if (offers.length > 45 && offers.length % 45 == 0) {
            return offers;
        }
        ItemStack[] resizedOffers = new ItemStack[Main.getInstance().findNextMultiple(offers.length, 45)];
        for (int i = 0; i < offers.length; i++) {
            resizedOffers[i] = offers[i];
        }
        for (int i = offers.length; i < resizedOffers.length; i++) {
            resizedOffers[i] = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName("§7").build();
        }
        return resizedOffers;
    }

    private void prepareInventoryPages(ItemStack[] offers, int pages, boolean specialAndPlayerOffers) {
        for (int i = 0; i < pages; i++) {
            ItemStack[] inventoryPage = new ItemStack[54];
            for (int j = 0; j < 45; j++) {
                inventoryPage[j] = offers[j];
            }
            if (i == 0) {
                for (int j = 45; j < inventoryPage.length; j++) {
                    inventoryPage[j] = menuGlass;
                    inventoryPage[49] = closeItem;
                    if (pages > 1) {
                        inventoryPage[53] = nextPage;
                    }
                    if (specialAndPlayerOffers) {
                        inventoryPage[52] = jumpToSpecialOffers;
                    }
                }
            } else if (i < pages - 1) {
                for (int j = 45; j < inventoryPage.length; j++) {
                    inventoryPage[j] = menuGlass;
                    inventoryPage[45] = previousPage;
                    inventoryPage[49] = closeItem;
                    inventoryPage[53] = nextPage;
                    if (specialAndPlayerOffers) {
                        inventoryPage[46] = jumpToPlayerOffers;
                        inventoryPage[52] = jumpToSpecialOffers;
                    }
                }
            } else if (i == pages - 1) {
                for (int j = 45; j < inventoryPage.length; j++) {
                    inventoryPage[j] = menuGlass;
                    inventoryPage[45] = previousPage;
                    inventoryPage[49] = closeItem;
                    if (specialAndPlayerOffers) {
                        inventoryPage[46] = jumpToPlayerOffers;
                    }
                }
            }
            inventories.add(inventoryPage);
        }
    }
}
