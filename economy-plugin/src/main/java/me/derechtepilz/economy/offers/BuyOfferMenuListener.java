package me.derechtepilz.economy.offers;

import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.inventorymanagement.StandardInventoryItems;
import me.derechtepilz.economy.utility.ChatFormatter;
import me.derechtepilz.economy.utility.DataHandler;
import me.derechtepilz.economy.utility.ItemBuilder;
import me.derechtepilz.economy.utility.NamespacedKeys;
import me.derechtepilz.economycore.EconomyAPI;
import me.derechtepilz.economycore.exceptions.BalanceException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BuyOfferMenuListener implements Listener {

    private final HashMap<UUID, List<UUID>> potentialCustomers = new HashMap<>();
    private final ChatFormatter chatFormatter = new ChatFormatter();

    private final Main main;
    public BuyOfferMenuListener(Main main) {
        this.main = main;
    }

    @SuppressWarnings("ConstantConditions")
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getView().getTitle().contains("Buy Menu (")) {
            if (event.getClickedInventory() == null) return;
            event.setCancelled(event.getClickedInventory().equals(event.getView().getTopInventory()));

            if (event.getCurrentItem() == null) return;
            ItemStack item = event.getCurrentItem();
            if (item.equals(StandardInventoryItems.MENU_CLOSE)) {
                player.closeInventory();
                return;
            }
            if (item.equals(StandardInventoryItems.ARROW_NEXT)) {
                DataHandler.updateMenuPage(player, DataHandler.getCurrentPage(player) + 1);
                return;
            }
            if (item.equals(StandardInventoryItems.ARROW_PREVIOUS)) {
                DataHandler.updateMenuPage(player, DataHandler.getCurrentPage(player) - 1);
                return;
            }
            if (!item.getItemMeta().getPersistentDataContainer().has(NamespacedKeys.ITEM_UUID, PersistentDataType.STRING)) {
                return;
            }
            if (item.getItemMeta().getPersistentDataContainer().has(NamespacedKeys.ITEM_UUID, PersistentDataType.STRING)) return;
            UUID itemUuid = UUID.fromString(item.getItemMeta().getPersistentDataContainer().get(NamespacedKeys.ITEM_UUID, PersistentDataType.STRING));
            OfflinePlayer seller = Bukkit.getOfflinePlayer(UUID.fromString(item.getItemMeta().getPersistentDataContainer().get(NamespacedKeys.ITEM_SELLER, PersistentDataType.STRING)));

            if (seller.getName().equals(player.getName())) {
                player.sendMessage("§cYou cannot buy your own item!");
                return;
            }
            if (potentialCustomers.containsKey(itemUuid)) {
                List<UUID> customers = potentialCustomers.get(itemUuid);
                if (!customers.contains(player.getUniqueId())) {
                    customers.add(player.getUniqueId());
                }
                potentialCustomers.put(itemUuid, customers);
            } else {
                potentialCustomers.put(itemUuid, new ArrayList<>(List.of(player.getUniqueId())));
            }

            Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), () -> {
                if (potentialCustomers.get(itemUuid).size() > 1) {
                    potentialCustomers.get(itemUuid).forEach(customer -> Bukkit.getPlayer(customer).sendMessage("§cCould not process purchase because there were too many interested customer!"));
                    potentialCustomers.remove(itemUuid);
                    return;
                }
                double balance = EconomyAPI.getBalance(player);
                double price = item.getItemMeta().getPersistentDataContainer().get(NamespacedKeys.ITEM_PRICE, PersistentDataType.DOUBLE);
                if (price > balance) {
                    player.sendMessage("§cYou do not have enough coins to buy this item!");
                    return;
                }

                // Give coins to seller
                    if (!seller.isOnline()) {
                        double coinsEarned = main.getEarnedCoins().getOrDefault(seller.getUniqueId(), (double) 0);
                        coinsEarned += price;
                        main.getEarnedCoins().put(seller.getUniqueId(), coinsEarned);
                    } else {
                        seller.getPlayer().sendMessage("§aYou earned §6" + chatFormatter.valueOf(price) + " coins §afrom selling items!");
                        try {
                            EconomyAPI.addCoinsToBalance(player, price);
                        } catch (BalanceException e) {
                            player.sendMessage("§cSomething unexpected happened: " + e.getMessage());
                        }
                    }

                // Take coins from customer
                try {
                    EconomyAPI.removeCoinsFromBalance(player, price);
                } catch (BalanceException e) {
                    player.sendMessage("§cSomething unexpected happened: " + e.getMessage());
                }

                // Give item to customer
                player.getInventory().addItem(new ItemStack(item.getType(), item.getAmount()));

                potentialCustomers.remove(itemUuid);
            }, 5);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (event.getView().getTitle().contains("Buy Menu (")) {
            DataHandler.removeMenuData(player);
        }
    }
}
