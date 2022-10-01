package me.derechtepilz.economycore;

import me.derechtepilz.database.DatabaseQueryBuilder;
import me.derechtepilz.economycore.exceptions.BalanceException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

class Bank {

    public void setBalance(Player player, double amount) throws BalanceException {
        double balance = getBalance(player);
        double startBalance = getStartBalance(player);
        if (amount < startBalance) {
            throw new BalanceException("It is not possible to set the coins under the amount of the start balance! (You tried setting the balance to: " + amount + ", allowed:" + (balance - startBalance) + ")");
        }
        Bukkit.getScheduler().runTaskAsynchronously(EconomyAPI.PLUGIN, () -> new DatabaseQueryBuilder(EconomyAPI.DATABASE).updateBalance(player.getUniqueId(), amount).commit());
        player.getPersistentDataContainer().set(EconomyAPI.PLAYER_BALANCE, PersistentDataType.DOUBLE, amount);
    }

    public void addBalance(Player player, double amount) throws BalanceException {
        if (amount < 0) {
            throw new BalanceException("It is not possible to add a negative amount of coins!");
        }
        double balance = getBalance(player);
        balance += amount;
        double finalBalance = balance;
        Bukkit.getScheduler().runTaskAsynchronously(EconomyAPI.PLUGIN, () -> new DatabaseQueryBuilder(EconomyAPI.DATABASE).updateBalance(player.getUniqueId(), finalBalance).commit());
        player.getPersistentDataContainer().set(EconomyAPI.PLAYER_BALANCE, PersistentDataType.DOUBLE, finalBalance);
    }

    public void removeBalance(Player player, double amount) throws BalanceException {
        double balance = getBalance(player);
        double startBalance = getStartBalance(player);
        if (balance - amount < startBalance) {
            throw new BalanceException("It is not possible to remove so many coins that the player does not have the start balance anymore! (You tried to remove: " + amount + ", allowed: " + (balance - startBalance) + ")");
        }
        balance = balance - amount;
        double finalBalance = balance;
        Bukkit.getScheduler().runTaskAsynchronously(EconomyAPI.PLUGIN, () -> new DatabaseQueryBuilder(EconomyAPI.DATABASE).updateBalance(player.getUniqueId(), finalBalance).commit());
        player.getPersistentDataContainer().set(EconomyAPI.PLAYER_BALANCE, PersistentDataType.DOUBLE, finalBalance);
    }

    public void fixStartBalance(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(EconomyAPI.PLUGIN, () -> {
            double playerBalance = getBalance(player);
            double playerStartBalance = getStartBalance(player);
            double currentStartBalance = ConfigHandler.getStartBalance();
            if (currentStartBalance > playerStartBalance) {
                double startBalanceDifference = currentStartBalance - playerStartBalance;
                playerBalance = playerBalance + startBalanceDifference;
                new DatabaseQueryBuilder(EconomyAPI.DATABASE).updateBalance(player.getUniqueId(), playerBalance).commit();
                player.getPersistentDataContainer().set(EconomyAPI.PLAYER_BALANCE, PersistentDataType.DOUBLE, playerBalance);
            }
        });
    }

    public void calculateInterest(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(EconomyAPI.PLUGIN, () -> {
            long lastInterest = getLastInterest(player);
            double balance = getBalance(player);
            double interestRate = ConfigHandler.getInterest();
            long currentInterest = System.currentTimeMillis();
            long passedDays = ChronoUnit.DAYS.between(new Date(lastInterest).toInstant(), new Date(currentInterest).toInstant());
            if (passedDays >= ConfigHandler.getMinimumDaysForInterest()) {
                double balanceBeforeInterest = balance;
                for (long l = 0; l < passedDays; l++) {
                    balance = balance * (1 + interestRate / 100);
                }
                player.sendMessage("§aYou received §6" + (balance - balanceBeforeInterest) + " coins §aas interest");
            }
            lastInterest = currentInterest;
            new DatabaseQueryBuilder(EconomyAPI.DATABASE).updateBalance(player.getUniqueId(), balance).updateLastInterest(player.getUniqueId(), lastInterest).commit();
            player.getPersistentDataContainer().set(EconomyAPI.PLAYER_BALANCE, PersistentDataType.DOUBLE, balance);
        });
    }

    private double getStartBalance(Player player) {
        AtomicReference<Double> startBalance = new AtomicReference<>((double) 0);
        Bukkit.getScheduler().runTaskAsynchronously(EconomyAPI.PLUGIN, () -> startBalance.set(EconomyAPI.DATABASE.getStartBalance(EconomyAPI.DATABASE.getConnection(), player.getUniqueId())));
        return startBalance.get();
    }

    private long getLastInterest(Player player) {
        AtomicReference<Long> lastInterest = new AtomicReference<>((long) 0);
        Bukkit.getScheduler().runTaskAsynchronously(EconomyAPI.PLUGIN, () -> lastInterest.set(EconomyAPI.DATABASE.getLastInterest(EconomyAPI.DATABASE.getConnection(), player.getUniqueId())));
        return lastInterest.get();
    }

    private double getBalance(Player player) {
        AtomicReference<Double> balance = new AtomicReference<>((double) 0);
        Bukkit.getScheduler().runTaskAsynchronously(EconomyAPI.PLUGIN, () -> balance.set(EconomyAPI.DATABASE.getBalance(EconomyAPI.DATABASE.getConnection(), player.getUniqueId())));
        return balance.get();
    }

}
