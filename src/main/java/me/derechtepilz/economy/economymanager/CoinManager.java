package me.derechtepilz.economy.economymanager;

public class CoinManager {

    private final BankManager bankManager;

    public CoinManager(BankManager bankManager) {
        this.bankManager = bankManager;
    }

    public void calculateInterest() {
        double interest = bankManager.getBalance() * 1.01;
        bankManager.setBank(new BankManager(bankManager.getPlayer(), interest));
    }

}
