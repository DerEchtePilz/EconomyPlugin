package me.derechtepilz.economy.economymanager;

import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.utility.Config;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;

import java.time.temporal.ChronoUnit;
import java.util.Date;

public class JoinLeaveEvent implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.getPersistentDataContainer().has(Main.getInstance().getLastInterest(), PersistentDataType.LONG)) {
            // Calculate interest if at least 24 hours have passed
            long lastPlayerInterest = player.getPersistentDataContainer().get(Main.getInstance().getLastInterest(), PersistentDataType.LONG);
            long interestDays = betweenDates(new Date(lastPlayerInterest), new Date(System.currentTimeMillis()));
            if (interestDays >= 1) {
                BankManager bankManager = new BankManager(player, player.getPersistentDataContainer().get(Main.getInstance().getBalance(), PersistentDataType.DOUBLE));
                new CoinManager().calculateInterest(bankManager, interestDays);

                player.getPersistentDataContainer().set(Main.getInstance().getLastInterest(), PersistentDataType.LONG, System.currentTimeMillis());
                player.sendMessage(TranslatableChatComponent.read("joinLeaveEvent.onJoin.grant_interest"));
            }
        } else {
            // Give start balance
            BankManager bankManager = new BankManager(player, (Double) Config.get("startBalance"));
            player.getPersistentDataContainer().set(Main.getInstance().getLastInterest(), PersistentDataType.LONG, System.currentTimeMillis());
            player.getPersistentDataContainer().set(Main.getInstance().getStartBalance(), PersistentDataType.DOUBLE, (Double) Config.get("startBalance"));

            player.sendMessage(TranslatableChatComponent.read("joinLeaveEvent.onJoin.join_bonus"));
        }

        // Check if start balance has been increased and give player missing start balance
        double playerStartBalance = player.getPersistentDataContainer().get(Main.getInstance().getStartBalance(), PersistentDataType.DOUBLE);
        double configStartBalance = (Double) Config.get("startBalance");

        if (configStartBalance > playerStartBalance) {
            double missingStartBalance = configStartBalance - playerStartBalance;
            double currentPlayerBalance = player.getPersistentDataContainer().get(Main.getInstance().getBalance(), PersistentDataType.DOUBLE);
            BankManager bankManager = new BankManager(player, currentPlayerBalance + missingStartBalance);

            player.getPersistentDataContainer().set(Main.getInstance().getStartBalance(), PersistentDataType.DOUBLE, configStartBalance);
            player.sendMessage(TranslatableChatComponent.read("joinLeaveEvent.onJoin.awarded_missing_start_balance"));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

    }

    private long betweenDates(Date firstDate, Date secondDate) {
        return ChronoUnit.DAYS.between(firstDate.toInstant(), secondDate.toInstant());
    }
}
