package me.derechtepilz.economy.economymanager;

import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.utility.ChatFormatter;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class BankManager implements Bank {

    private Player player;
    private double balance;

    /**
     *
     * @param player The player who is the holder of the account
     * @param newBalance The new balance that should be set for the player
     */
    public BankManager(Player player, double newBalance) {
        this.player = player;
        this.balance = newBalance;

        updateBank();
    }

    public BankManager() {

    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public double getBalance() {
        return balance;
    }

    @Override
    public void setBalance(double balance) {
        this.balance = balance;
        updateBank();
    }

    @Override
    public BankManager loadBank(Player player) {
        return new BankManager(player, player.getPersistentDataContainer().get(Main.getInstance().getBalance(), PersistentDataType.DOUBLE));
    }

    private void updateBank() {
        player.getPersistentDataContainer().set(Main.getInstance().getBalance(), PersistentDataType.DOUBLE, balance);
        Main.getInstance().getBankAccounts().put(player.getUniqueId(), this);
    }
}
