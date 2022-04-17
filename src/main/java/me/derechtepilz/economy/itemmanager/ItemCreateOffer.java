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

package me.derechtepilz.economy.itemmanager;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.ItemStackArgument;
import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.utility.Config;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemCreateOffer {
    public ItemCreateOffer() {
        new CommandAPICommand("createoffer")
                .withArguments(new ItemStackArgument("item"))
                .withArguments(new IntegerArgument("count", (Integer) Config.get("itemQuantities.minAmount"), (Integer) Config.get("itemQuantities.maxAmount")))
                .withArguments(new IntegerArgument("price", (Integer) Config.get("itemPrice.minAmount"), (Integer) Config.get("itemPrice.maxAmount")))
                .executes((sender, args) -> {
                    if (sender instanceof Player player) {
                        ItemStack item = (ItemStack) args[0];
                        int amount = (int) args[1];
                        int price = (int) args[2];

                        for (int i = 0; i < player.getInventory().getSize(); i++) {
                            ItemStack playerInventoryItem = player.getInventory().getItem(i);
                            if (playerInventoryItem == null) continue;
                            if (playerInventoryItem.isSimilar(item)) {
                                if (playerInventoryItem.getAmount() > amount) {
                                    playerInventoryItem.setAmount(playerInventoryItem.getAmount() - amount);
                                    player.getInventory().setItem(i, playerInventoryItem);

                                    new ItemConverter(playerInventoryItem, player, price);
                                } else {
                                    player.getInventory().remove(playerInventoryItem);
                                    new ItemConverter(playerInventoryItem, player, price);
                                }
                                return;
                            }
                        }
                        player.sendMessage(TranslatableChatComponent.read("itemCreateOffer.player_executor.missing_item"));
                        return;
                    }
                    if (sender instanceof ConsoleCommandSender console) {
                        ItemStack item = (ItemStack) args[0];
                        int amount = (int) args[1];
                        int price = (int) args[2];
                        item.setAmount(amount);

                        new ItemConverter(item, price);
                        console.sendMessage(TranslatableChatComponent.read("itemCreateOffer.console_executor.console_created_offer").replace("%%s", amount + "").replace("%s", item.getType().name()));
                        Bukkit.getOnlinePlayers().forEach(p ->
                                p.sendMessage(TranslatableChatComponent.read("itemCreateOffer.console_executor.special_offer_available").replace("%%%s", price + "").replace("%%s", amount + "").replace("%s", item.getType().name()))
                        );
                        return;
                    }
                    sender.sendMessage(TranslatableChatComponent.read("itemCreateOffer.wrong_executor"));
                })
                .register();
    }
}
