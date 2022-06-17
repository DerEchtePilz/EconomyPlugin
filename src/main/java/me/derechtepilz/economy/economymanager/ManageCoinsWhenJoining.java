package me.derechtepilz.economy.economymanager;

import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.utility.ChatFormatter;
import me.derechtepilz.economy.utility.config.Config;
import me.derechtepilz.economy.utility.NamespacedKeys;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;

import java.time.temporal.ChronoUnit;
import java.util.Date;

public class ManageCoinsWhenJoining implements Listener {

    int taskID;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        BankManager bankManager = null;
        if (player.getPersistentDataContainer().has(NamespacedKeys.BALANCE.getKey(), PersistentDataType.DOUBLE)) {
            bankManager = new BankManager().loadBank(player);
        }
        if (player.getPersistentDataContainer().has(NamespacedKeys.LAST_INTEREST.getKey(), PersistentDataType.LONG)) {
            // Calculate interest if at least 24 hours have passed
            long lastPlayerInterest = player.getPersistentDataContainer().get(NamespacedKeys.LAST_INTEREST.getKey(), PersistentDataType.LONG);
            long interestDays = daysBetweenDates(new Date(lastPlayerInterest), new Date(System.currentTimeMillis()));
            if (interestDays >= 1) {
                new CoinManager().calculateInterest(bankManager, interestDays);

                player.getPersistentDataContainer().set(NamespacedKeys.LAST_INTEREST.getKey(), PersistentDataType.LONG, System.currentTimeMillis());
                player.sendMessage(TranslatableChatComponent.read("manageCoinsWhenJoining.onJoin.grant_interest").replace("%s", ChatFormatter.valueOf(bankManager.getBalance())));
            }
        } else {
            // Give start balance
            bankManager = new BankManager(player, Double.parseDouble(Config.get("startBalance")));
            player.getPersistentDataContainer().set(NamespacedKeys.LAST_INTEREST.getKey(), PersistentDataType.LONG, System.currentTimeMillis());
            player.getPersistentDataContainer().set(NamespacedKeys.START_BALANCE.getKey(), PersistentDataType.DOUBLE, Double.parseDouble(Config.get("startBalance")));

            player.sendMessage(TranslatableChatComponent.read("manageCoinsWhenJoining.onJoin.join_bonus").replace("%s", ChatFormatter.valueOf(bankManager.getBalance())));
        }

        // Check if start balance has been increased and give player missing start balance
        double playerStartBalance = player.getPersistentDataContainer().get(NamespacedKeys.START_BALANCE.getKey(), PersistentDataType.DOUBLE);
        double configStartBalance = Double.parseDouble(Config.get("startBalance"));

        if (configStartBalance > playerStartBalance) {
            double missingStartBalance = configStartBalance - playerStartBalance;
            double currentPlayerBalance = player.getPersistentDataContainer().get(NamespacedKeys.BALANCE.getKey(), PersistentDataType.DOUBLE);
            bankManager.setBalance(currentPlayerBalance + missingStartBalance);

            player.getPersistentDataContainer().set(NamespacedKeys.START_BALANCE.getKey(), PersistentDataType.DOUBLE, configStartBalance);
            player.sendMessage(TranslatableChatComponent.read("manageCoinsWhenJoining.onJoin.awarded_missing_start_balance").replace("%%s", ChatFormatter.valueOf(bankManager.getBalance())).replace("%s", ChatFormatter.valueOf(missingStartBalance)));
        }

        giveEarnedCoinsFromSelling(player);
        displayBalance(player);
    }

    private long daysBetweenDates(Date firstDate, Date secondDate) {
        return ChronoUnit.DAYS.between(firstDate.toInstant(), secondDate.toInstant());
    }

    private void giveEarnedCoinsFromSelling(Player player) {
        int earnedCoins = Main.getInstance().getEarnedCoins().getOrDefault(player.getUniqueId(), 0);
        if (earnedCoins > 0) {
            BankManager bankManager = Main.getInstance().getBankAccounts().get(player.getUniqueId());
            bankManager.setBalance(bankManager.getBalance() + earnedCoins);
            player.sendMessage(TranslatableChatComponent.read("manageCoinsWhenJoining.give_earned_coins").replace("%s", ChatFormatter.valueOf(earnedCoins)));
            Main.getInstance().getEarnedCoins().remove(player.getUniqueId());
        }
    }

    private void displayBalance(Player player) {
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), () -> {
            BankManager bankManager = Main.getInstance().getBankAccounts().get(player.getUniqueId());
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(TranslatableChatComponent.read("manageCoinsWhenJoining.display_coins").replace("%s", ChatFormatter.valueOf(bankManager.getBalance()))));
        }, 0, 1);
    }
}
