package me.derechtepilz.economy.economymanager;

import me.derechtepilz.economy.utility.Config;

public class CoinManager {

    public CoinManager() {
    }

    public void calculateInterest(BankManager bankManager, long interestDays) {
        double interest = bankManager.getBalance() * (interestDays + ((double) Config.get("interest") / 100));
        bankManager.setBank(new BankManager(bankManager.getPlayer(), interest));
    }

}
