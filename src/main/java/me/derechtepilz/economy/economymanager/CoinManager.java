package me.derechtepilz.economy.economymanager;

import me.derechtepilz.economy.utility.config.Config;
import me.derechtepilz.economy.utility.config.ConfigFields;

public class CoinManager {

    public CoinManager() {
    }

    public void calculateInterest(BankManager bankManager, long interestDays) {
        double interest = 0;
        for (long l = 0; l < interestDays; l++) {
            interest = bankManager.getBalance() * (1 + ((double) Config.get(ConfigFields.INTEREST) / 100));
            bankManager.setBalance(interest);
        }
        new BankManager(bankManager.getPlayer(), interest);
    }

}
