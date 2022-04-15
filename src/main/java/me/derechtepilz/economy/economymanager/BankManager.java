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
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class BankManager extends Bank {

    private Player player;
    private int balance;
    private BankManager manager;

    public BankManager(Player player, int balance) {
        this.player = player;
        this.balance = balance;

        player.getPersistentDataContainer().set(Main.getInstance().getBalance(), PersistentDataType.INTEGER, this.balance);
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public int getBalance() {
        return balance;
    }

    public BankManager getBankManager() {
        return manager;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public void setBalance(int balance) {
        this.balance = balance;
    }

    @Override
    public void setBank(BankManager bank) {
        this.manager = bank;
    }
}
