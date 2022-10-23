package io.github.derechtepilz.economycore;

import io.github.derechtepilz.database.DatabaseQueryBuilder;
import io.github.derechtepilz.economycore.exceptions.BalanceException;
import org.bukkit.entity.Player;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

class Bank {

    public void setBalance(Player player, double amount) throws BalanceException {
        double startBalance = getStartBalance(player);
        UUID uuid = player.getUniqueId();
        if (amount < startBalance) {
            throw new BalanceException("It is not possible to set the coins under the amount of the start balance! (You tried setting the balance to: " + amount + ", at least allowed:" + startBalance + ")");
        }
        EconomyAPI.DATABASE.updateBalance(uuid, amount);
    }

    public void addBalance(Player player, double amount) throws BalanceException {
        if (amount < 0) {
            throw new BalanceException("It is not possible to add a negative amount of coins!");
        }
        double balance = getBalance(player);
        balance += amount;
        EconomyAPI.DATABASE.updateBalance(player.getUniqueId(), balance);
    }

    public void removeBalance(Player player, double amount) throws BalanceException {
        double playerBalance = getBalance(player);
        double playerStartBalance = getStartBalance(player);
        UUID uuid = player.getUniqueId();
        if (playerBalance - amount < playerStartBalance) {
            throw new BalanceException("It is not possible to remove so many coins that the player does not have the start balance anymore! (You tried to remove: " + amount + ", allowed: " + (playerBalance - playerStartBalance) + ")");
        }
        playerBalance = playerBalance - amount;
        EconomyAPI.DATABASE.updateBalance(uuid, playerBalance);
    }

    public void fixStartBalance(Player player) {
        double playerStartBalance = getStartBalance(player);
        double currentStartBalance = ConfigHandler.getStartBalance();

        if (currentStartBalance > playerStartBalance) {
            double missingStartBalance = currentStartBalance - playerStartBalance;
            double playerBalance = getBalance(player);
            EconomyAPI.DATABASE.updateBalance(player.getUniqueId(), playerBalance + missingStartBalance);
        }
    }

    public void calculateInterest(Player player) {
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
            new DatabaseQueryBuilder(EconomyAPI.DATABASE).updateBalance(player.getUniqueId(), balance).updateLastInterest(player.getUniqueId(), currentInterest);
        }
    }

    private double getStartBalance(Player player) {
        return EconomyAPI.DATABASE.getStartBalance(player.getUniqueId());
    }

    private long getLastInterest(Player player) {
        return EconomyAPI.DATABASE.getLastInterest(player.getUniqueId());
    }

    private double getBalance(Player player) {
        return EconomyAPI.DATABASE.getBalance(player.getUniqueId());
    }

}
