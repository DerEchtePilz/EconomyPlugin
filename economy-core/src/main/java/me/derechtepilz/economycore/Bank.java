package me.derechtepilz.economycore;

import me.derechtepilz.database.DatabaseQueryBuilder;
import me.derechtepilz.economycore.exceptions.BalanceException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.time.temporal.ChronoUnit;
import java.util.Date;

class Bank {

    public void setBalance(Player player, double amount) throws BalanceException {
        double startBalance = getStartBalance(player);
        if (amount < startBalance) {
            throw new BalanceException("It is not possible to set the coins under the amount of the start balance! (You tried setting the balance to: " + amount + ", at least allowed:" + startBalance + ")");
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
            double playerStartBalance = getStartBalance(player);
            double currentStartBalance = ConfigHandler.getStartBalance();

            if (currentStartBalance > playerStartBalance) {
                System.out.println(this.getClass().getSimpleName() + " - fixStartBalance: currentStartBalance > playerStartBalance");
                System.out.println(this.getClass().getSimpleName() + " - fixStartBalance: " + currentStartBalance);
                System.out.println(this.getClass().getSimpleName() + " - fixStartBalance: " + playerStartBalance);

                double missingStartBalance = currentStartBalance - playerStartBalance;
                double currentPlayerBalance = getBalance(player);

                System.out.println(this.getClass().getSimpleName() + " - fixStartBalance: " + currentPlayerBalance);

                new DatabaseQueryBuilder(EconomyAPI.DATABASE).updateBalance(player.getUniqueId(), currentPlayerBalance + missingStartBalance);
                player.getPersistentDataContainer().set(EconomyAPI.PLAYER_BALANCE, PersistentDataType.DOUBLE, currentPlayerBalance + missingStartBalance);
           }
        });
    }

    public void calculateInterest(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(EconomyAPI.PLUGIN, () -> {
            long lastInterest = getLastInterest(player);
            long currentInterest = System.currentTimeMillis();

            double balance = getBalance(player);
            double interestRate = ConfigHandler.getInterest();

            long passedDays = ChronoUnit.DAYS.between(new Date(lastInterest).toInstant(), new Date(currentInterest).toInstant());

            if (passedDays >= ConfigHandler.getMinimumDaysForInterest()) {
                double balanceBeforeInterest = balance;
                for (long l = 0; l < passedDays; l++) {
                    balance = balance * (1 + interestRate / 100);
                }
                player.sendMessage("§aYou received §6" + (balance - balanceBeforeInterest) + " coins §aas interest!");
                new DatabaseQueryBuilder(EconomyAPI.DATABASE).updateBalance(player.getUniqueId(), balance).updateLastInterest(player.getUniqueId(), currentInterest).commit();
                player.getPersistentDataContainer().set(EconomyAPI.PLAYER_BALANCE, PersistentDataType.DOUBLE, balance);
            }
        });
    }

    private double getStartBalance(Player player) {
        return EconomyAPI.DATABASE.getStartBalance(EconomyAPI.DATABASE.getConnection(), player.getUniqueId());
    }

    private long getLastInterest(Player player) {
        return EconomyAPI.DATABASE.getLastInterest(EconomyAPI.DATABASE.getConnection(), player.getUniqueId());
    }

    private double getBalance(Player player) {
        return EconomyAPI.DATABASE.getBalance(EconomyAPI.DATABASE.getConnection(), player.getUniqueId());
    }

}
