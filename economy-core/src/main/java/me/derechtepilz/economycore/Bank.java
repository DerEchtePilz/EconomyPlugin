package me.derechtepilz.economycore;

import me.derechtepilz.economycore.exceptions.BalanceException;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.time.temporal.ChronoUnit;
import java.util.Date;

class Bank {

    private final Player player;
    private double balance;
    private final double startBalance;
    private long lastInterest;

    public Bank(Player player) {
        this.player = player;
        this.startBalance = ConfigHandler.getStartBalance();
        this.lastInterest = System.currentTimeMillis();
        this.player.getPersistentDataContainer().set(EconomyAPI.lastInterestKey, PersistentDataType.LONG, lastInterest);
        this.player.getPersistentDataContainer().set(EconomyAPI.playerBalance, PersistentDataType.DOUBLE, balance);
        this.player.getPersistentDataContainer().set(EconomyAPI.playerStartBalance, PersistentDataType.DOUBLE, startBalance);
    }

    public Bank(Player player, double balance, double startBalance, long lastInterest) {
        this.player = player;
        this.balance = balance;
        this.startBalance = startBalance;
        this.lastInterest = lastInterest;
    }

    public void setBalance(double amount) throws BalanceException {
        if (amount < startBalance) {
            throw new BalanceException("It is not possible to set the coins under the amount of the start balance! (You tried to remove: " + amount + ", allowed:" + (this.balance - startBalance) + ")");
        }
        this.balance = amount;
        updateAccount();
    }

    public void addBalance(double amount) throws BalanceException {
        if (amount < 0) {
            throw new BalanceException("It is not possible to add a negative amount of coins!");
        }
        this.balance = this.balance + amount;
        updateAccount();
    }

    public void removeBalance(double amount) throws BalanceException {
        if (this.balance - amount < startBalance) {
            throw new BalanceException("It is not possible to remove so many coins that the player does not have the start balance anymore! (You tried to remove: " + amount + ", allowed:" + (this.balance - startBalance) + ")");
        }
        this.balance = this.balance - amount;
        updateAccount();
    }

    @SuppressWarnings("ConstantConditions")
    public void calculateInterest() {
        double interest = ConfigHandler.getInterest();
        long lastPlayerInterest = player.getPersistentDataContainer().get(EconomyAPI.lastInterestKey, PersistentDataType.LONG);
        long currentInterest = System.currentTimeMillis();
        long days = ChronoUnit.DAYS.between(new Date(lastPlayerInterest).toInstant(), new Date(currentInterest).toInstant());
        boolean isInterestDue = days >= ConfigHandler.getMinimumDaysForInterest();
        if (isInterestDue) {
            for (long l = 0; l <= days; l++) {
                balance = balance * (1 + interest / 100);
            }
        }
        lastInterest = currentInterest;
        updateAccount();
    }

    public void updateAccount() {
        player.getPersistentDataContainer().set(EconomyAPI.lastInterestKey, PersistentDataType.LONG, lastInterest);
        player.getPersistentDataContainer().set(EconomyAPI.playerBalance, PersistentDataType.DOUBLE, balance);
    }

    @Override
    public String toString() {
        return "Bank=[player=" + player.getName() + ", balance=" + balance + ", startBalance=" + startBalance + ", lastInterest=" + lastInterest + "]";
    }

    double getBalance() {
        return balance;
    }

}
