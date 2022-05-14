package me.derechtepilz.economy.itemmanager;

import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.utility.ItemBuilder;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class ItemBuyMenu implements Listener {

    private final HashMap<UUID, List<ItemStack[]>> inventories = new HashMap<>();
    private final HashMap<UUID, List<List<ItemStack[]>>> prepareInventories = new HashMap<>();
    private int currentInventory;

    private int playerOfferPages;
    private int specialOfferPages;

    private Inventory inventory;

    private final HashMap<ItemStack, List<Player>> buyers = new HashMap<>();

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getView().getTitle().contains(TranslatableChatComponent.read("itemBuyMenu.inventory_title")) && Objects.equals(event.getClickedInventory(), event.getView().getTopInventory())) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) return;
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem.equals(closeItem)) {
                player.closeInventory();
                return;
            }
            if (clickedItem.equals(nextPage)) {
                currentInventory += 1;
                inventory = Bukkit.createInventory(null, 54, TranslatableChatComponent.read("itemBuyMenu.inventory_title") + (currentInventory + 1) + ")");
                inventory.setContents(inventories.get(player.getUniqueId()).get(currentInventory));
                player.openInventory(inventory);
                return;
            }
            if (clickedItem.equals(previousPage)) {
                currentInventory -= 1;
                inventory = Bukkit.createInventory(null, 54, TranslatableChatComponent.read("itemBuyMenu.inventory_title") + (currentInventory + 1) + ")");
                inventory.setContents(inventories.get(player.getUniqueId()).get(currentInventory));
                player.openInventory(inventory);
                return;
            }
            if (clickedItem.getItemMeta().getDisplayName().equals(jumpToPlayerOffers.getItemMeta().getDisplayName())) {
                currentInventory = 0;
                inventory = Bukkit.createInventory(null, 54, TranslatableChatComponent.read("itemBuyMenu.inventory_title") + (currentInventory + 1) + ")");
                inventory.setContents(inventories.get(player.getUniqueId()).get(currentInventory));
                player.openInventory(inventory);
                return;
            }
            if (clickedItem.equals(jumpToSpecialOffers)) {
                currentInventory = playerOfferPages;
                inventory = Bukkit.createInventory(null, 54, TranslatableChatComponent.read("itemBuyMenu.inventory_title") + (currentInventory + 1) + ")");
                inventory.setContents(inventories.get(player.getUniqueId()).get(currentInventory));
                player.openInventory(inventory);
            }
            if (!clickedItem.hasItemMeta() && clickedItem.getItemMeta() == null) return;
            ItemMeta meta = clickedItem.getItemMeta();
            if (meta == null) return;
            if (meta.getPersistentDataContainer().has(Main.getInstance().getUuid(), PersistentDataType.STRING) && meta.getPersistentDataContainer().has(Main.getInstance().getCreator(), PersistentDataType.STRING) && meta.getPersistentDataContainer().has(Main.getInstance().getPrice(), PersistentDataType.INTEGER)) {
                /*
                 * buyers.put(clickedItem, player);
                 * Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(),() -> {
                 * if (buyers.size() == 1) {
                 * // TODO: Check player money
                 * // TODO: Process the item, give the item
                 * } else {
                 * for (Player interestedPlayer : buyers) {
                 * interestedPlayer.sendMessage(TranslatableChatComponent.read("itemBuyMenu.onClick.to_many_customers"));
                 * }
                 * }
                 * }, 20);
                 */
            }
        }
    }

    public void openBuyMenu(Player player) {
        List<ItemStack> playerOffers = new ArrayList<>();
        List<ItemStack> specialOffers = new ArrayList<>();

        // Load player offers
        for (UUID uuid : Main.getInstance().getPlayerOffers().keySet()) {
            playerOffers.addAll(Arrays.asList(Main.getInstance().getPlayerOffers().get(uuid)));
        }

        // Load special offer
        if (Main.getInstance().getSpecialOffers().get("console") != null) {
            specialOffers.addAll(Arrays.asList(Main.getInstance().getSpecialOffers().get("console")));
        }

        ItemStack[] playerOffersArray = playerOffers.toArray(new ItemStack[0]);
        ItemStack[] specialOffersArray = specialOffers.toArray(new ItemStack[0]);

        // Resize arrays to fit inventory size
        ItemStack[] formattedPlayerOffers = new ItemStack[0];
        ItemStack[] formattedSpecialOffers = new ItemStack[0];
        if (playerOffersArray.length >= 1) {
            formattedPlayerOffers = resizeInventoryContents(playerOffersArray);
            jumpToPlayerOffers = new ItemBuilder(Material.PLAYER_HEAD).setTexture(player.getName()).setName(TranslatableChatComponent.read("itemBuyMenu.jump_to_player_offers_name")).build();
        }
        if (specialOffersArray.length >= 1) {
            formattedSpecialOffers = resizeInventoryContents(specialOffersArray);
        }

        List<ItemStack> allOffers = new ArrayList<>();
        allOffers.addAll(List.of(formattedPlayerOffers));
        allOffers.addAll(List.of(formattedSpecialOffers));

        // Prepare inventory pages
        playerOfferPages = formattedPlayerOffers.length != 0 ? formattedPlayerOffers.length / 45 : 0;
        specialOfferPages = formattedSpecialOffers.length != 0 ? formattedSpecialOffers.length / 45 : 0;

        prepareInventoryPages(allOffers, playerOfferPages, specialOfferPages, player);

        // Sort ItermStack[] into the right list
        List<ItemStack[]> invPages = new ArrayList<>();
        for (List<ItemStack[]> pages : prepareInventories.get(player.getUniqueId())) {
            invPages.addAll(pages);
        }
        inventories.put(player.getUniqueId(), invPages);

        // Open inventory
        inventory = Bukkit.createInventory(null, 54, TranslatableChatComponent.read("itemBuyMenu.inventory_title") + "1)");
        inventory.setContents(inventories.get(player.getUniqueId()).get(0));
        currentInventory = 0;
        player.openInventory(inventory);
    }

    public void openBuyMenu(Player player, Material type) {
        List<ItemStack> playerOffers = new ArrayList<>();
        List<ItemStack> specialOffers = new ArrayList<>();

        // Load player offers
        for (UUID uuid : Main.getInstance().getPlayerOffers().keySet()) {
            playerOffers.addAll(Arrays.asList(Main.getInstance().getPlayerOffers().get(uuid)));
        }

        // Load special offer
        if (Main.getInstance().getSpecialOffers().get("console") != null) {
            specialOffers.addAll(Arrays.asList(Main.getInstance().getSpecialOffers().get("console")));
        }

        // Remove all materials that are not requested
        playerOffers.removeIf(material -> (material.getType() != type));
        specialOffers.removeIf(material -> (material.getType() != type));

        for (ItemStack item : playerOffers) {
            Main.getInstance().getLogger().info(item.getType().name());
        }
        for (ItemStack item : specialOffers) {
            Main.getInstance().getLogger().info(item.getType().name());
        }

        ItemStack[] playerOffersArray = playerOffers.toArray(new ItemStack[0]);
        ItemStack[] specialOffersArray = specialOffers.toArray(new ItemStack[0]);

        // Resize arrays to fit inventory size
        ItemStack[] formattedPlayerOffers = new ItemStack[0];
        ItemStack[] formattedSpecialOffers = new ItemStack[0];
        if (playerOffersArray.length >= 1) {
            formattedPlayerOffers = resizeInventoryContents(playerOffersArray);
            jumpToPlayerOffers = new ItemBuilder(Material.PLAYER_HEAD).setTexture(player.getName()).setName(TranslatableChatComponent.read("itemBuyMenu.jump_to_player_offers_name")).build();
        }
        if (specialOffersArray.length >= 1) {
            formattedSpecialOffers = resizeInventoryContents(specialOffersArray);
        }

        // Prepare inventory pages
        playerOfferPages = formattedPlayerOffers.length != 0 ? formattedPlayerOffers.length / 45 : 0;
        specialOfferPages = formattedSpecialOffers.length != 0 ? formattedSpecialOffers.length / 45 : 0;

        if (playerOfferPages >= 1 && specialOfferPages >= 1) {
            // prepareInventoryPages(formattedPlayerOffers, player, playerOfferPages, true);
            // prepareInventoryPages(formattedSpecialOffers, player, specialOfferPages, true);
        } else {
            // prepareInventoryPages(formattedPlayerOffers, player, playerOfferPages, false);
            // prepareInventoryPages(formattedSpecialOffers, player, specialOfferPages, false);
        }

        // Open inventory
        inventory = Bukkit.createInventory(null, 54, TranslatableChatComponent.read("itemBuyMenu.inventory_title") + "1)");
        inventory.setContents(inventories.get(player.getUniqueId()).get(0));
        currentInventory = 0;
        player.openInventory(inventory);
    }

    private final ItemStack closeItem = new ItemBuilder(Material.BARRIER).setName(TranslatableChatComponent.read("items.title.close")).build();
    private final ItemStack nextPage = new ItemBuilder(Material.ARROW).setName(TranslatableChatComponent.read("items.title.next_page")).build();
    private final ItemStack previousPage = new ItemBuilder(Material.ARROW).setName(TranslatableChatComponent.read("items.title.previous_page")).build();
    private final ItemStack menuGlass = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName(TranslatableChatComponent.read("items.title.menu_glass")).build();
    private final ItemStack jumpToSpecialOffers = new ItemBuilder(Material.NETHER_STAR).setName(TranslatableChatComponent.read("itemBuyMenu.jump_to_special_offers_name")).build();
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
            resizedOffers[i] = menuGlass;
        }
        return resizedOffers;
    }

    private void prepareInventoryPages(List<ItemStack> offers, int playerOfferPages, int specialOfferPages, Player customer) {
        List<ItemStack[]> invContents = new ArrayList<>();
        boolean playerOffers = playerOfferPages >= 1;
        boolean specialOffers = specialOfferPages >= 1;
        if (playerOffers) {
            for (int i = 0; i < playerOfferPages; i++) {
                ItemStack[] inventoryPage = new ItemStack[54];
                for (int j = 0; j < 45; j++) {
                    inventoryPage[j] = offers.get(j);
                }
                if (i == 0) {
                    for (int j = 45; j < 54; j++) {
                        inventoryPage[j] = menuGlass;
                        inventoryPage[49] = closeItem;
                        if (specialOffers) {
                            inventoryPage[52] = jumpToSpecialOffers;
                            inventoryPage[53] = nextPage;
                        }
                        if (i < playerOfferPages - 1) {
                            inventoryPage[53] = nextPage;
                        }
                    }
                } else if (i < playerOfferPages - 1) {
                    for (int j = 45; j < 54; j++) {
                        inventoryPage[j] = menuGlass;
                        inventoryPage[45] = previousPage;
                        inventoryPage[49] = closeItem;
                        if (specialOffers) {
                            inventoryPage[52] = jumpToSpecialOffers;
                            inventoryPage[53] = nextPage;
                        }
                        if (i < playerOfferPages - 1) {
                            inventoryPage[53] = nextPage;
                        }
                    }
                } else if (i == playerOfferPages - 1) {
                    for (int j = 45; j < 54; j++) {
                        inventoryPage[j] = menuGlass;
                        inventoryPage[45] = previousPage;
                        inventoryPage[49] = closeItem;
                        if (specialOffers) {
                            inventoryPage[52] = jumpToSpecialOffers;
                            inventoryPage[53] = nextPage;
                        }
                    }
                }
                invContents.add(inventoryPage);
            }
        }
        if (specialOffers) {
            for (int i = 0; i < specialOfferPages; i++) {
                ItemStack[] inventoryPage = new ItemStack[54];
                for (int j = 0; j < 45; j++) {
                    inventoryPage[j] = offers.get(j + playerOfferPages * 45);
                }
                if (i == 0) {
                    for (int j = 45; j < 54; j++) {
                        inventoryPage[j] = menuGlass;
                        if (playerOffers) {
                            inventoryPage[45] = previousPage;
                            inventoryPage[46] = jumpToPlayerOffers;
                        }
                        inventoryPage[49] = closeItem;
                        if (i < specialOfferPages - 1) {
                            inventoryPage[53] = nextPage;
                        }
                    }
                } else if (i < specialOfferPages - 1) {
                    for (int j = 45; j < 54; j++) {
                        inventoryPage[j] = menuGlass;
                        inventoryPage[45] = previousPage;
                        if (playerOffers) {
                            inventoryPage[46] = jumpToPlayerOffers;
                        }
                        inventoryPage[49] = closeItem;
                        inventoryPage[53] = nextPage;
                    }
                } else if (i == specialOfferPages - 1) {
                    for (int j = 45; j < 54; j++) {
                        inventoryPage[j] = menuGlass;
                        inventoryPage[45] = previousPage;
                        if (playerOffers) {
                            inventoryPage[46] = jumpToPlayerOffers;
                        }
                        inventoryPage[49] = closeItem;
                    }
                }
                invContents.add(inventoryPage);
            }
        }
        if (prepareInventories.containsKey(customer.getUniqueId())) {
            prepareInventories.get(customer.getUniqueId()).add(invContents);
        } else {
            List<List<ItemStack[]>> inventories = new ArrayList<>();
            inventories.add(invContents);
            prepareInventories.put(customer.getUniqueId(), inventories);
        }
    }
}
