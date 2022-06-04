package me.derechtepilz.economy.bukkitcommands.commands.commandapicommands;

import me.derechtepilz.economy.bukkitcommands.arguments.ItemStackArgument;
import me.derechtepilz.economy.bukkitcommands.commands.CommandBase;
import me.derechtepilz.economy.itemmanager.ItemUtils;
import me.derechtepilz.economy.playermanager.Permission;
import me.derechtepilz.economy.utility.ChatFormatter;
import me.derechtepilz.economy.utility.RangeValidator;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import me.derechtepilz.economy.utility.config.Config;
import me.derechtepilz.economy.utility.config.ConfigFields;
import me.derechtepilz.economy.utility.exceptions.InvalidRangeException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CreateOfferCommandExecutor extends CommandBase {
    public CreateOfferCommandExecutor(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 1, 2, 3 -> sender.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_few_arguments").replace("%%s", ChatFormatter.valueOf(4)).replace("%s", ChatFormatter.valueOf(args.length)));
            case 4 -> {
                if (sender instanceof Player player) {
                    if (!Permission.hasPermission(player, Permission.CREATE_OFFER)) {
                        player.sendMessage(TranslatableChatComponent.read("command.insufficient_permission"));
                        return;
                    }
                    ItemStack item = new ItemStackArgument().parse(args[1]);
                    if (item == null) {
                        player.sendMessage(TranslatableChatComponent.read("fallbackCommand.wrong_item_id_provided").replace("%s", args[1]));
                        return;
                    }
                    if (isNotInt(args[2])) {
                        player.sendMessage(TranslatableChatComponent.read("fallbackCommand.integer_required").replace("%s", ChatFormatter.valueOf(3)));
                        return;
                    }
                    if (isNotInt(args[3])) {
                        player.sendMessage(TranslatableChatComponent.read("fallbackCommand.integer_required").replace("%s", ChatFormatter.valueOf(3)));
                        return;
                    }
                    int amount = Integer.parseInt(args[2]);
                    int price = Integer.parseInt(args[3]);

                    try {
                        int itemQuantitiesMinAmount = Integer.parseInt(Config.get(ConfigFields.ITEM_QUANTITIES_MIN_AMOUNT));
                        int itemQuantitiesMaxAmount = Integer.parseInt(Config.get(ConfigFields.ITEM_QUANTITIES_MAX_AMOUNT));
                        new RangeValidator(itemQuantitiesMinAmount, itemQuantitiesMaxAmount, amount, "Could not process command because " + ChatFormatter.valueOf(amount) + " was not in the range from " + ChatFormatter.valueOf(itemQuantitiesMinAmount) + " to " + ChatFormatter.valueOf(itemQuantitiesMaxAmount) + "!");

                        int itemPriceMinAmount = Integer.parseInt(Config.get(ConfigFields.ITEM_PRICE_MIN_AMOUNT));
                        int itemPriceMaxAmount = Integer.parseInt(Config.get(ConfigFields.ITEM_PRICE_MAX_AMOUNT));
                        new RangeValidator(itemPriceMinAmount, itemPriceMaxAmount, price, "Could not process command because " + ChatFormatter.valueOf(price) + " was not in the range from " + ChatFormatter.valueOf(itemPriceMinAmount) + " to " + ChatFormatter.valueOf(itemPriceMaxAmount) + "!");
                    } catch (InvalidRangeException e) {
                        player.sendMessage(ChatColor.RED + e.getMessage());
                        return;
                    }

                    item.setAmount(amount);

                    for (int i = 0; i < player.getInventory().getSize(); i++) {
                        if (player.getInventory().getItem(i) == null) continue;
                        if (player.getInventory().getItem(i).isSimilar(item)) {
                            if (player.getInventory().getItem(i).getAmount() >= amount) {
                                ItemUtils.createSalableItem(player.getName(), item, price);
                                player.getInventory().remove(item);
                                player.sendMessage(TranslatableChatComponent.read("itemCreateOffer.player_executor.player_created_offer").replace("%%s", ChatFormatter.valueOf(amount)).replace("%s", "minecraft:" + item.getType().name().toLowerCase()));
                            } else {
                                player.sendMessage(TranslatableChatComponent.read("itemCreateOffer.player_executor.too_few_items").replace("%%s", String.valueOf(amount)).replace("%s", "minecraft:" + item.getType().name().toLowerCase()));
                            }
                            return;
                        }
                    }
                    player.sendMessage(TranslatableChatComponent.read("itemCreateOffer.player_executor.missing_item"));
                    return;
                }
                if (sender instanceof ConsoleCommandSender console) {
                    ItemStack item = new ItemStackArgument().parse(args[1]);
                    if (item == null) {
                        console.sendMessage(TranslatableChatComponent.read("fallbackCommand.wrong_item_id_provided").replace("%s", args[1]));
                        return;
                    }
                    if (isNotInt(args[2])) {
                        console.sendMessage(TranslatableChatComponent.read("fallbackCommand.integer_required").replace("%s", ChatFormatter.valueOf(3)));
                        return;
                    }
                    if (isNotInt(args[3])) {
                        console.sendMessage(TranslatableChatComponent.read("fallbackCommand.integer_required").replace("%s", ChatFormatter.valueOf(3)));
                        return;
                    }
                    int amount = Integer.parseInt(args[2]);
                    int price = Integer.parseInt(args[3]);

                    try {
                        int itemQuantitiesMinAmount = Integer.parseInt(Config.get(ConfigFields.ITEM_QUANTITIES_MIN_AMOUNT));
                        int itemQuantitiesMaxAmount = Integer.parseInt(Config.get(ConfigFields.ITEM_QUANTITIES_MAX_AMOUNT));
                        new RangeValidator(itemQuantitiesMinAmount, itemQuantitiesMaxAmount, amount, "Could not process command because " + ChatFormatter.valueOf(amount) + " was not in the range from " + ChatFormatter.valueOf(itemQuantitiesMinAmount) + " to " + ChatFormatter.valueOf(itemQuantitiesMaxAmount) + "!");

                        int itemPriceMinAmount = Integer.parseInt(Config.get(ConfigFields.ITEM_PRICE_MIN_AMOUNT));
                        int itemPriceMaxAmount = Integer.parseInt(Config.get(ConfigFields.ITEM_PRICE_MAX_AMOUNT));
                        new RangeValidator(itemPriceMinAmount, itemPriceMaxAmount, price, "Could not process command because " + ChatFormatter.valueOf(price) + " was not in the range from " + ChatFormatter.valueOf(itemPriceMinAmount) + " to " + ChatFormatter.valueOf(itemPriceMaxAmount) + "!");
                    } catch (InvalidRangeException e) {
                        console.sendMessage(ChatColor.RED + e.getMessage());
                        return;
                    }

                    item.setAmount(amount);

                    ItemUtils.createSalableItem("console", item, price);
                    console.sendMessage(TranslatableChatComponent.read("itemCreateOffer.console_executor.console_created_offer").replace("%%s", amount + "").replace("%s", "minecraft:" + item.getType().name().toLowerCase()));
                    Bukkit.getOnlinePlayers().forEach(p ->
                            p.sendMessage(TranslatableChatComponent.read("itemCreateOffer.console_executor.special_offer_available").replace("%%%s", price + "").replace("%%s", amount + "").replace("%s", item.getType().name()))
                    );
                    return;
                }
                sender.sendMessage(TranslatableChatComponent.read("itemCreateOffer.wrong_executor"));
            }
            default -> sender.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_many_arguments").replace("%%s", ChatFormatter.valueOf(4)).replace("%s", ChatFormatter.valueOf(args.length)));
        }
    }
}
