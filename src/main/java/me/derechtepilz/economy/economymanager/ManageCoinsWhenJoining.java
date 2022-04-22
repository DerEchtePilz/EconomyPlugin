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

package me.derechtepilz.economy.economymanager;

import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.utility.ChatFormatter;
import me.derechtepilz.economy.utility.Config;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;

import java.time.temporal.ChronoUnit;
import java.util.Date;

public class ManageCoinsWhenJoining implements Listener {
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
                player.sendMessage(TranslatableChatComponent.read("joinLeaveEvent.onJoin.grant_interest").replace("%s", ChatFormatter.valueOf(bankManager.getBalance())));
            }
        } else {
            // Give start balance
            bankManager = new BankManager(player, (Double) Config.get("startBalance"));
            player.getPersistentDataContainer().set(Main.getInstance().getLastInterest(), PersistentDataType.LONG, System.currentTimeMillis());
            player.getPersistentDataContainer().set(Main.getInstance().getStartBalance(), PersistentDataType.DOUBLE, (Double) Config.get("startBalance"));

            player.sendMessage(TranslatableChatComponent.read("joinLeaveEvent.onJoin.join_bonus").replace("%s", ChatFormatter.valueOf(bankManager.getBalance())));
        }

        // Check if start balance has been increased and give player missing start balance
        double playerStartBalance = player.getPersistentDataContainer().get(Main.getInstance().getStartBalance(), PersistentDataType.DOUBLE);
        double configStartBalance = (Double) Config.get("startBalance");

        if (configStartBalance > playerStartBalance) {
            double missingStartBalance = configStartBalance - playerStartBalance;
            double currentPlayerBalance = player.getPersistentDataContainer().get(Main.getInstance().getBalance(), PersistentDataType.DOUBLE);
            bankManager.setBalance(currentPlayerBalance + missingStartBalance);

            player.getPersistentDataContainer().set(Main.getInstance().getStartBalance(), PersistentDataType.DOUBLE, configStartBalance);
            player.sendMessage(TranslatableChatComponent.read("joinLeaveEvent.onJoin.awarded_missing_start_balance").replace("%%s", ChatFormatter.valueOf(bankManager.getBalance())).replace("%s", ChatFormatter.valueOf(missingStartBalance)));
        }
    }

    private long betweenDates(Date firstDate, Date secondDate) {
        return ChronoUnit.DAYS.between(firstDate.toInstant(), secondDate.toInstant());
    }
}
