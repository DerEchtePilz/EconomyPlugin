package me.derechtepilz.economy.inventory;

import me.derechtepilz.economy.utility.ItemBuilder;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import me.derechtepilz.economy.utility.inventorymanagement.InventoryBase;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TradeMenu implements Listener, InventoryBase {

    private final int[] tradingSlotsPlayerOne = {
            0, 1, 2, 3,
            9, 10, 11, 12,
            18, 19, 20, 21,
            27, 28, 29, 30,
            36, 37, 38, 39
    };

    private final int[] tradingSlotsPlayerTwo = {
            5, 6, 7, 8,
            14, 15, 16, 17,
            23, 24, 25, 26,
            32, 33, 34, 35,
            41, 42, 43, 44
    };

    @SuppressWarnings("SpellCheckingInspection")
    private final Character[] inventoryFillableSlots = {
            ' ', ' ', ' ', ' ', 'G', ' ', ' ', ' ', ' ',
            ' ', ' ', ' ', ' ', 'G', ' ', ' ', ' ', ' ',
            ' ', ' ', ' ', ' ', 'G', ' ', ' ', ' ', ' ',
            ' ', ' ', ' ', ' ', 'G', ' ', ' ', ' ', ' ',
            ' ', ' ', ' ', ' ', 'G', ' ', ' ', ' ', ' ',
            'A', 'P', 'P', 'P', 'C', 'T', 'T', 'T', 'T'
    };

    private final HashMap<UUID, UUID> tradingPlayers = new HashMap<>();
    private final HashMap<UUID, List<ItemStack>> tradedItems = new HashMap<>();
    private final HashMap<UUID, Integer> tradeCancelled = new HashMap<>();

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getView().getTitle().equals(TranslatableChatComponent.read("tradeMenu.menu.title"))) {
            event.setCancelled(true);
            Player target = Bukkit.getPlayer(tradingPlayers.get(player.getUniqueId()));
            ItemStack item = event.getCurrentItem();
            if (item == null) {
                return;
            }
            Inventory playerTradeInventory = event.getView().getTopInventory();
            Inventory targetTradeInventory = target.getOpenInventory().getTopInventory();
            if (event.getClickedInventory().equals(event.getView().getTopInventory())) {
                if (item.equals(cancelTrade)) {
                    tradeCancelled.put(player.getUniqueId(), 1);
                    tradeCancelled.put(target.getUniqueId(), 1);

                    closeInventory(player);
                    closeInventory(target);
                    player.sendMessage(TranslatableChatComponent.read("tradeMenu.trade_cancelled"));
                    target.sendMessage(TranslatableChatComponent.read("tradeMenu.trade_cancelled"));

                    // return player items to player
                    for (ItemStack tradedItem : tradedItems.get(target.getUniqueId())) {
                        player.getInventory().addItem(tradedItem);
                    }

                    // return target items to target
                    for (ItemStack tradedItem : tradedItems.get(player.getUniqueId())) {
                        target.getInventory().addItem(tradedItem);
                    }

                    tradingPlayers.remove(target.getUniqueId());
                    tradingPlayers.remove(player.getUniqueId());

                    tradedItems.remove(player.getUniqueId());
                    tradedItems.remove(target.getUniqueId());

                    tradeCancelled.remove(player.getUniqueId());
                    tradeCancelled.remove(target.getUniqueId());
                    return;
                }
                if (item.equals(acceptTrade)) {
                    if (hasTargetAccepted(playerTradeInventory)) {
                        // trade here

                        // give player items
                        for (ItemStack tradedItem : tradedItems.get(player.getUniqueId())) {
                            player.getInventory().addItem(tradedItem);
                        }

                        // give target items
                        for (ItemStack tradedItem : tradedItems.get(target.getUniqueId())) {
                            target.getInventory().addItem(tradedItem);
                        }

                        // close inventories
                        closeInventory(player);
                        closeInventory(target);

                        tradedItems.remove(player.getUniqueId());
                        tradedItems.remove(target.getUniqueId());

                        player.sendMessage(TranslatableChatComponent.read("tradeMenu.trade_complete"));
                        target.sendMessage(TranslatableChatComponent.read("tradeMenu.trade_complete"));
                        return;
                    }
                    // player accepts the trade so we change the status

                    // change player status for player
                    final int[] playerSlotsToChange = {46, 47, 48};
                    for (int slot : playerSlotsToChange) {
                        playerTradeInventory.setItem(slot, playerAccepted);
                    }
                    playerTradeInventory.setItem(45, tradeAccepted);

                    // change player status for target
                    final int[] targetSlotsToChange = {50, 51, 52, 53};
                    for (int slot : targetSlotsToChange) {
                        targetTradeInventory.setItem(slot, targetAccepted);
                    }
                    return;
                }
                if (item.equals(tradeAccepted)) {
                    setNotAcceptTrade(player, target);
                    return;
                }
                int clickedSlot = event.getSlot();
                for (int playerTradingSlot : tradingSlotsPlayerOne) {
                    if (playerTradingSlot == clickedSlot) {
                        player.getInventory().addItem(item);
                        List<ItemStack> tradeItems = tradedItems.get(target.getUniqueId());
                        tradeItems.remove(item);
                        rearrangeTradingInventory(player, target, tradeItems);

                        setNotAcceptTrade(player, target);
                        setNotAcceptTrade(target, player);
                    }
                }
                return;
            }
            if (event.getClickedInventory().equals(event.getView().getBottomInventory())) {
                addItemToTradingInventory(player, target, item);

                List<ItemStack> items;
                if (tradedItems.containsKey(target.getUniqueId())) {
                    items = tradedItems.get(target.getUniqueId());
                } else {
                    items = new ArrayList<>();
                }

                items.add(item);
                tradedItems.put(target.getUniqueId(), items);

                setNotAcceptTrade(player, target);
                setNotAcceptTrade(target, player);

                player.getInventory().remove(item);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (tradingPlayers.containsKey(player.getUniqueId())) {
            Player target = Bukkit.getPlayer(tradingPlayers.get(player.getUniqueId()));
            if (event.getView().getTitle().equals(TranslatableChatComponent.read("tradeMenu.menu.title"))) {
                if (tradeCancelled.containsKey(player.getUniqueId())) {
                    return;
                }

                player.sendMessage(TranslatableChatComponent.read("tradeMenu.trade_cancelled"));
                target.sendMessage(TranslatableChatComponent.read("tradeMenu.trade_cancelled"));

                // return player items to player
                for (ItemStack tradedItem : tradedItems.get(target.getUniqueId())) {
                    player.getInventory().addItem(tradedItem);
                }

                // return target items to target
                for (ItemStack tradedItem : tradedItems.get(player.getUniqueId())) {
                    target.getInventory().addItem(tradedItem);
                }

                tradingPlayers.remove(target.getUniqueId());
                tradingPlayers.remove(player.getUniqueId());

                tradedItems.remove(player.getUniqueId());
                tradedItems.remove(target.getUniqueId());

                target.closeInventory();
            }
        }
    }

    public void openTradeMenu(Player sender, Player target) {
        tradingPlayers.put(sender.getUniqueId(), target.getUniqueId());
        tradingPlayers.put(target.getUniqueId(), sender.getUniqueId());
        openInventory(sender, createInventory(null, 6 * 9, TranslatableChatComponent.read("tradeMenu.menu.title")));
        openInventory(target, createInventory(null, 6 * 9, TranslatableChatComponent.read("tradeMenu.menu.title")));
    }

    private ItemStack[] manageTradeInventory() {
        ItemStack[] contents = new ItemStack[54];
        for (int i = 0; i < contents.length; i++) {
            switch (inventoryFillableSlots[i]) {
                case ' ' -> contents[i] = new ItemStack(Material.AIR);
                case 'G' -> contents[i] = menuGlass;
                case 'A' -> contents[i] = acceptTrade;
                case 'C' -> contents[i] = cancelTrade;
                case 'P' -> contents[i] = playerNotAccepted;
                case 'T' -> contents[i] = targetNotAccepted;
            }
        }
        return contents;
    }

    private final ItemStack menuGlass = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName("§7").build();
    private final ItemStack playerNotAccepted = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setName(TranslatableChatComponent.read("tradeMenu.item.player_not_accepted")).build();
    private final ItemStack targetNotAccepted = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setName(TranslatableChatComponent.read("tradeMenu.item.target_not_accepted")).build();
    private final ItemStack playerAccepted = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setName(TranslatableChatComponent.read("tradeMenu.item.player_accepted")).build();
    private final ItemStack targetAccepted = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setName(TranslatableChatComponent.read("tradeMenu.item.target_accepted")).build();
    private final ItemStack acceptTrade = new ItemBuilder(Material.YELLOW_DYE).setName(TranslatableChatComponent.read("tradeMenu.item.accept_trade")).build();
    private final ItemStack tradeAccepted = new ItemBuilder(Material.LIME_DYE).setName(TranslatableChatComponent.read("tradeMenu.item.trade_accepted")).build();
    private final ItemStack cancelTrade = new ItemBuilder(Material.BARRIER).setName(TranslatableChatComponent.read("tradeMenu.item.cancel_trade")).build();

    @Override
    public void openInventory(Player player, Inventory inventory) {
        player.openInventory(inventory);
    }

    @Override
    public void closeInventory(Player player) {
        player.closeInventory();
    }

    @Override
    public Inventory createInventory(InventoryHolder holder, int size, String title) {
        Inventory inventory = Bukkit.createInventory(holder, size, title);
        inventory.setContents(manageTradeInventory());
        return inventory;
    }

    private boolean hasTargetAccepted(Inventory tradeInventory) {
        int[] statusSlots = {50, 51, 52, 53};
        boolean hasAccepted = false;
        for (int slot : statusSlots) {
            hasAccepted = tradeInventory.getItem(slot).equals(targetAccepted);
        }
        return hasAccepted;
    }

    private void addItemToTradingInventory(Player player, Player target, ItemStack item) {
        Inventory playerTradeInventory = player.getOpenInventory().getTopInventory();
        Inventory targetTradeInventory = target.getOpenInventory().getTopInventory();
        if (item != null) {
            boolean wasItemAdded = false;
            for (int slot : tradingSlotsPlayerOne) {
                if (isSlotOccupied(slot, tradingSlotsPlayerOne, playerTradeInventory)) {
                    continue;
                }
                if (!wasItemAdded) {
                    playerTradeInventory.setItem(slot, item);
                    wasItemAdded = true;
                }
            }

            wasItemAdded = false;
            for (int slot : tradingSlotsPlayerTwo) {
                if (isSlotOccupied(slot, tradingSlotsPlayerTwo, targetTradeInventory)) {
                    continue;
                }
                if (!wasItemAdded) {
                    targetTradeInventory.setItem(slot, item);
                    wasItemAdded = true;
                }
            }
        } else {
            playerTradeInventory.setItem(0, null);
            targetTradeInventory.setItem(5, null);
        }
    }

    private void rearrangeTradingInventory(Player player, Player target, List<ItemStack> tradedItems) {
        if (tradedItems.size() == 0 || tradedItems == null) {
            addItemToTradingInventory(player, target, null);
            return;
        }
        for (ItemStack item : tradedItems) {
            addItemToTradingInventory(player, target, item);
        }
    }

    private boolean isSlotOccupied(int slot, int[] slots, Inventory tradingInventory) {
        boolean occupied = false;
        for (int tradingSlot : slots) {
            if (slot == tradingSlot) {
                occupied = tradingInventory.getItem(slot) != null;
            }
        }
        return occupied;
    }

    private void setNotAcceptTrade(Player player, Player target) {
        Inventory playerTradeInventory = player.getOpenInventory().getTopInventory();
        Inventory targetTradeInventory = target.getOpenInventory().getTopInventory();

        final int[] playerSlotsToChange = {46, 47, 48};
        final int[] targetSlotsToChange = {50, 51, 52, 53};

        // change player status for player
        for (int slot : playerSlotsToChange) {
            playerTradeInventory.setItem(slot, playerNotAccepted);
        }
        playerTradeInventory.setItem(45, acceptTrade);

        // change player status for target
        for (int slot : targetSlotsToChange) {
            targetTradeInventory.setItem(slot, targetNotAccepted);
        }
    }
}
