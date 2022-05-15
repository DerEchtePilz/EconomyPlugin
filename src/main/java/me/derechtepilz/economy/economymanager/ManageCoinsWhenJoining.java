package me.derechtepilz.economy.economymanager;

import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.utility.ChatFormatter;
import me.derechtepilz.economy.utility.Config;
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
        if (player.getPersistentDataContainer().has(Main.getInstance().getBalance(), PersistentDataType.DOUBLE)) {
            bankManager = new BankManager().loadBank(player);
        }
        if (player.getPersistentDataContainer().has(Main.getInstance().getLastInterest(), PersistentDataType.LONG)) {
            // Calculate interest if at least 24 hours have passed
            long lastPlayerInterest = player.getPersistentDataContainer().get(Main.getInstance().getLastInterest(), PersistentDataType.LONG);
            long interestDays = betweenDates(new Date(lastPlayerInterest), new Date(System.currentTimeMillis()));
            if (interestDays >= 1) {
                new CoinManager().calculateInterest(bankManager, interestDays);

                player.getPersistentDataContainer().set(Main.getInstance().getLastInterest(), PersistentDataType.LONG, System.currentTimeMillis());
                player.sendMessage(TranslatableChatComponent.read("manageCoinsWhenJoining.onJoin.grant_interest").replace("%s", ChatFormatter.valueOf(bankManager.getBalance())));
            }
        } else {
            // Give start balance
            bankManager = new BankManager(player, (Double) Config.get("startBalance"));
            player.getPersistentDataContainer().set(Main.getInstance().getLastInterest(), PersistentDataType.LONG, System.currentTimeMillis());
            player.getPersistentDataContainer().set(Main.getInstance().getStartBalance(), PersistentDataType.DOUBLE, (Double) Config.get("startBalance"));

            player.sendMessage(TranslatableChatComponent.read("manageCoinsWhenJoining.onJoin.join_bonus").replace("%s", ChatFormatter.valueOf(bankManager.getBalance())));
        }

        // Check if start balance has been increased and give player missing start balance
        double playerStartBalance = player.getPersistentDataContainer().get(Main.getInstance().getStartBalance(), PersistentDataType.DOUBLE);
        double configStartBalance = (Double) Config.get("startBalance");

        if (configStartBalance > playerStartBalance) {
            double missingStartBalance = configStartBalance - playerStartBalance;
            double currentPlayerBalance = player.getPersistentDataContainer().get(Main.getInstance().getBalance(), PersistentDataType.DOUBLE);
            bankManager.setBalance(currentPlayerBalance + missingStartBalance);

            player.getPersistentDataContainer().set(Main.getInstance().getStartBalance(), PersistentDataType.DOUBLE, configStartBalance);
            player.sendMessage(TranslatableChatComponent.read("manageCoinsWhenJoining.onJoin.awarded_missing_start_balance").replace("%%s", ChatFormatter.valueOf(bankManager.getBalance())).replace("%s", ChatFormatter.valueOf(missingStartBalance)));
        }

        displayCoins(player);
    }

    private long betweenDates(Date firstDate, Date secondDate) {
        return ChronoUnit.DAYS.between(firstDate.toInstant(), secondDate.toInstant());
    }

    private void displayCoins(Player player) {
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), () -> {
            BankManager bankManager = Main.getInstance().getBankAccounts().get(player.getUniqueId());
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(TranslatableChatComponent.read("manageCoinsWhenJoining.display_coins").replace("%s", ChatFormatter.valueOf(bankManager.getBalance()))));
        }, 0, 1);
    }
}
