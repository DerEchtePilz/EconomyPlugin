/**
 * MIT License
 *
 * Copyright (c) 2022 DerEchtePilz
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.derechtepilz.economy.itemmanager;

import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.utility.ItemBuilder;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ItemBuyMenu implements Listener {

    private final List<ItemStack[]> inventories = new ArrayList<>();
    private int currentInventory;

    private int playerOfferPages;
    private int specialOfferPages;

    private Inventory inventory;

    private final List<Player> buyers = new ArrayList<>();

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
                inventory.setContents(inventories.get(currentInventory));
                player.openInventory(inventory);
                return;
            }
            if (clickedItem.equals(previousPage)) {
                currentInventory -= 1;
                inventory = Bukkit.createInventory(null, 54, TranslatableChatComponent.read("itemBuyMenu.inventory_title") + (currentInventory + 1) + ")");
                inventory.setContents(inventories.get(currentInventory));
                player.openInventory(inventory);
                return;
            }
            if (clickedItem.equals(jumpToPlayerOffers)) {
                currentInventory = 0;
                inventory = Bukkit.createInventory(null, 54, TranslatableChatComponent.read("itemBuyMenu.inventory_title") + (currentInventory + 1) + ")");
                inventory.setContents(inventories.get(currentInventory));
                player.openInventory(inventory);
                return;
            }
            if (clickedItem.equals(jumpToSpecialOffers)) {
                currentInventory = playerOfferPages;
                inventory = Bukkit.createInventory(null, 54, TranslatableChatComponent.read("itemBuyMenu.inventory_title") + (currentInventory + 1) + ")");
                inventory.setContents(inventories.get(currentInventory));
                player.openInventory(inventory);
            }
            if (!clickedItem.hasItemMeta() && clickedItem.getItemMeta() == null) return;
            ItemMeta meta = clickedItem.getItemMeta();
            if (meta == null) return;
            if (meta.getPersistentDataContainer().has(Main.getInstance().getUuid(), PersistentDataType.STRING) && meta.getPersistentDataContainer().has(Main.getInstance().getCreator(), PersistentDataType.STRING) && meta.getPersistentDataContainer().has(Main.getInstance().getPrice(), PersistentDataType.INTEGER)) {
                buyers.add(player);
                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(),() -> {
                    if (buyers.size() == 1) {
                        // TODO: Check player money
                        // TODO: Process the item, give the item
                    } else {
                        for (Player interestedPlayer : buyers) {
                            interestedPlayer.sendMessage(TranslatableChatComponent.read("itemBuyMenu.onClick.to_many_customers"));
                        }
                    }
                }, 20);
            }
        }
    }

    public void openBuyMenu(Player player, Material type) {
        ItemStack[] allPlayerOffers = Main.getInstance().getOfferingPlayers().get(player.getUniqueId());
        ItemStack[] allSpecialOffers = Main.getInstance().getSpecialOffers().get("console");

        if (allPlayerOffers.length == 0 && allSpecialOffers.length == 0) {
            player.sendMessage(TranslatableChatComponent.read("itemBuyMenu.no_offers"));
            return;
        }

        List<ItemStack> playerOffersTypes = new ArrayList<>();
        List<ItemStack> specialOffersTypes = new ArrayList<>();

        ItemStack[] playerOffers = new ItemStack[0];
        ItemStack[] specialOffers = new ItemStack[0];

        jumpToPlayerOffers = new ItemBuilder(Material.PLAYER_HEAD).setTexture(player.getName()).setName(TranslatableChatComponent.read("itemBuyMenu.jump_to_player_offers_name")).build();

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

        inventory = Bukkit.createInventory(null, 54, TranslatableChatComponent.read("itemBuyMenu.inventory_title") + "1)");
        inventory.setContents(inventories.get(0));
        currentInventory = 0;
        player.openInventory(inventory);
    }

    public void openBuyMenu(Player player, boolean query) {
        ItemStack[] allSpecialOffers = Main.getInstance().getSpecialOffers().get("console");
        if (allSpecialOffers == null) {
            player.sendMessage(TranslatableChatComponent.read("itemBuyMenu.no_special_offer"));
            return;
        }

        if (allSpecialOffers.length == 0) {
            player.sendMessage(TranslatableChatComponent.read("itemBuyMenu.no_special_offer"));
            return;
        }
        ItemStack[] specialOffers = resizeInventoryContents(allSpecialOffers);
        specialOfferPages = specialOffers.length / 45;

        prepareInventoryPages(specialOffers, specialOfferPages, false);

        inventory = Bukkit.createInventory(null, 54, TranslatableChatComponent.read("itemBuyMenu.inventory_title") + "1)");
        inventory.setContents(inventories.get(0));
        currentInventory = 0;
        player.openInventory(inventory);
    }


    // TODO: Handle edge cases for null arrays
    public void openBuyMenu(Player player) {
        ItemStack[] playerOffers = Main.getInstance().getOfferingPlayers().get(player.getUniqueId());
        ItemStack[] specialOffers = Main.getInstance().getSpecialOffers().get("console");

        if (playerOffers == null && specialOffers == null) {
            player.sendMessage(TranslatableChatComponent.read("itemBuyMenu.no_offers"));
            return;
        }

        if (playerOffers.length == 0 && specialOffers.length == 0) {
            player.sendMessage(TranslatableChatComponent.read("itemBuyMenu.no_offers"));
            return;
        }

        jumpToPlayerOffers = new ItemBuilder(Material.PLAYER_HEAD).setTexture(player.getName()).setName(TranslatableChatComponent.read("itemBuyMenu.jump_to_player_offers_name")).build();

        if (playerOffers.length > 0) {
            playerOffers = resizeInventoryContents(playerOffers);
        }

        if (specialOffers.length > 0) {
            specialOffers = resizeInventoryContents(specialOffers);
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

        inventory = Bukkit.createInventory(null, 54, TranslatableChatComponent.read("itemBuyMenu.inventory_title") + "1)");
        inventory.setContents(inventories.get(0));
        currentInventory = 0;
        player.openInventory(inventory);
    }

    private final ItemStack closeItem = new ItemBuilder(Material.BARRIER).setName(TranslatableChatComponent.read("itemBuyMenu.close_item_name")).build();
    private final ItemStack nextPage = new ItemBuilder(Material.ARROW).setName(TranslatableChatComponent.read("itemBuyMenu.next_page_name")).build();
    private final ItemStack previousPage = new ItemBuilder(Material.ARROW).setName(TranslatableChatComponent.read("itemBuyMenu.previous_page_name")).build();
    private final ItemStack menuGlass = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName(TranslatableChatComponent.read("itemBuyMenu.menu_glass_name")).build();
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
