package me.derechtepilz.economy.economymanager;

import org.bukkit.entity.Player;

public interface Bank {

    Player getPlayer();

    double getBalance();

    void setBalance(double balance);

    BankManager loadBank(Player player);

}