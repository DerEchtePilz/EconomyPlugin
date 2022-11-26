package io.github.derechtepilz.economy.offers;

import io.github.derechtepilz.economy.Main;
import io.github.derechtepilz.economy.inventorymanagement.StandardInventoryItems;
import io.github.derechtepilz.economy.utility.ChatFormatter;
import io.github.derechtepilz.economy.utility.DataHandler;
import io.github.derechtepilz.economy.utility.NamespacedKeys;
import io.github.derechtepilz.economycore.EconomyAPI;
import io.github.derechtepilz.economycore.exceptions.BalanceException;
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
			ItemStack item = handleInventoryClicks(event, player);
			if (item == null) return;

			UUID itemUuid = UUID.fromString(item.getItemMeta().getPersistentDataContainer().get(NamespacedKeys.ITEM_UUID, PersistentDataType.STRING));
			OfflinePlayer seller = Bukkit.getOfflinePlayer(UUID.fromString(item.getItemMeta().getPersistentDataContainer().get(NamespacedKeys.ITEM_SELLER, PersistentDataType.STRING)));

			if (seller.getName().equals(player.getName())) {
				player.sendMessage("§cYou cannot buy your own item!");
				return;
			}

			handleMultipleCustomers(player, itemUuid);

			Bukkit.getScheduler().runTaskLater(main, () -> {
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

				giveCoinsToSeller(seller, player, price);

				handleCustomer(player, itemUuid, price);

				unregisterItem(seller, itemUuid);

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

	@SuppressWarnings("ConstantConditions")
	private ItemStack handleInventoryClicks(InventoryClickEvent event, Player player) {
		event.setCancelled(event.getClickedInventory().equals(event.getView().getTopInventory()));

		if (event.getCurrentItem() == null) return null;
		ItemStack item = event.getCurrentItem();
		if (item.equals(StandardInventoryItems.MENU_CLOSE)) {
			player.closeInventory();
			return null;
		}
		if (item.equals(StandardInventoryItems.ARROW_NEXT)) {
			DataHandler.updateMenuPage(player, DataHandler.getCurrentPage(player) + 1);
			return null;
		}
		if (item.equals(StandardInventoryItems.ARROW_PREVIOUS)) {
			DataHandler.updateMenuPage(player, DataHandler.getCurrentPage(player) - 1);
			return null;
		}
		return item;
	}

	private void handleMultipleCustomers(Player player, UUID itemUuid) {
		if (potentialCustomers.containsKey(itemUuid)) {
			List<UUID> customers = potentialCustomers.get(itemUuid);
			if (!customers.contains(player.getUniqueId())) {
				customers.add(player.getUniqueId());
			}
			potentialCustomers.put(itemUuid, customers);
		} else {
			potentialCustomers.put(itemUuid, new ArrayList<>(List.of(player.getUniqueId())));
		}
	}

	@SuppressWarnings("ConstantConditions")
	private void giveCoinsToSeller(OfflinePlayer seller, Player player, double price) {
		if (!seller.isOnline()) {
			double coinsEarned = main.getEarnedCoins().getOrDefault(seller.getUniqueId(), (double) 0);
			coinsEarned += price;
			main.getEarnedCoins().put(seller.getUniqueId(), coinsEarned);
		} else {
			seller.getPlayer().sendMessage("§aYou earned §6" + chatFormatter.valueOf(price) + " coins §afrom selling items!");
			try {
				EconomyAPI.addCoinsToBalance(seller.getPlayer(), price);
			} catch (BalanceException e) {
				player.sendMessage("§cSomething unexpected happened: " + e.getMessage());
			}
		}
	}

	private void handleCustomer(Player player, UUID itemUuid, double price) {
		// Take coins from customer
		try {
			EconomyAPI.removeCoinsFromBalance(player, price);
		} catch (BalanceException e) {
			player.sendMessage("§cSomething unexpected happened: " + e.getMessage());
		}

		// Give item to player
		player.getInventory().addItem(main.getRegisteredItems().get(itemUuid).getBoughtItem());
	}

	private void unregisterItem(OfflinePlayer seller, UUID itemUuid) {
		main.getOfferingPlayerUuids().remove(seller.getUniqueId());
		main.getRegisteredItems().remove(itemUuid);
		main.getRegisteredItemUuids().remove(itemUuid);
	}

}
