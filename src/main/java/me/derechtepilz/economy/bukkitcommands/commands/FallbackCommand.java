package me.derechtepilz.economy.bukkitcommands.commands;

import me.derechtepilz.economy.Main;
import me.derechtepilz.economy.bukkitcommands.arguments.ArgumentType;
import me.derechtepilz.economy.bukkitcommands.arguments.ItemStackArgument;
import me.derechtepilz.economy.bukkitcommands.arguments.PlayerArgument;
import me.derechtepilz.economy.bukkitcommands.arguments.StringArgument;
import me.derechtepilz.economy.economymanager.BankManager;
import me.derechtepilz.economy.itemmanager.ItemUtils;
import me.derechtepilz.economy.utility.TranslatableChatComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FallbackCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (Main.getInstance().isWasCommandAPILoaded()) {
            return false;
        }
        if (sender instanceof Player player) {
            if (args.length == 1) {
                switch (args[0].toLowerCase()) {
                    case "canceloffer" -> {
                        Main.getInstance().getItemCancelMenu().openOfferCancelMenu(player, Main.getInstance().getPlayerOffers().get(player.getUniqueId()));
                        player.sendMessage(TranslatableChatComponent.read("itemCancelOffer.player_executor.prepare_cancelling"));
                        return false;
                    }
                    case "buy" -> {
                        Main.getInstance().getItemBuyMenu().openBuyMenu(player);
                        return false;
                    }
                    case "createoffer" -> {
                        player.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_few_arguments").replace("%%s", "4").replace("%s", String.valueOf(args.length)));
                        return false;
                    }
                    case "givecoins" -> {
                        player.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_few_arguments").replace("%%s", "2").replace("%s", String.valueOf(args.length)));
                        return false;
                    }
                }
                return false;
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("canceloffer")) {
                    player.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_many_arguments").replace("%%s", "1").replace("%s", String.valueOf(args.length)));
                    return false;
                }
                if (args[0].equalsIgnoreCase("buy")) {
                    if (args[1].equalsIgnoreCase("special")) {
                        // Main.getInstance().getItemBuyMenu().openBuyMenu(player, true);
                        return false;
                    }
                    ItemStack item = new ItemStackArgument().parse(args[1]);
                    if (item == null) {
                        String itemId = args[1];
                        if (itemId.startsWith("minecraft:")) {
                            player.sendMessage(TranslatableChatComponent.read("fallbackCommand.wrong_item_id_provided").replace("%s", args[1]));
                        } else {
                            player.sendMessage(TranslatableChatComponent.read("fallbackCommand.wrong_item_id_provided").replace("%s", "minecraft:" + args[1]));
                        }
                        return false;
                    }
                    // Main.getInstance().getItemBuyMenu().openBuyMenu(player, item.getType());
                    return false;
                }
                if (args[0].equalsIgnoreCase("createoffer")) {
                    player.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_few_arguments").replace("%%s", "4").replace("%s", String.valueOf(args.length)));
                    return false;
                }
                if (args[0].equalsIgnoreCase("givecoins")) {
                    player.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_few_arguments").replace("%%s", "2").replace("%s", String.valueOf(args.length)));
                    return false;
                }
                return false;
            }
            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("canceloffer")) {
                    player.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_many_arguments").replace("%%s", "1").replace("%s", String.valueOf(args.length)));
                    return false;
                }
                if (args[0].equalsIgnoreCase("buy")) {
                    player.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_many_arguments").replace("%%s", "2").replace("%s", String.valueOf(args.length)));
                    return false;
                }
                if (args[0].equalsIgnoreCase("createoffer")) {
                    player.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_few_arguments").replace("%%s", "4").replace("%s", String.valueOf(args.length)));
                    return false;
                }
                if (args[0].equalsIgnoreCase("givecoins")) {
                    Player target = new PlayerArgument().parse(args[1]);
                    if (target == null) {
                        player.sendMessage(TranslatableChatComponent.read("fallbackCommand.player_not_recognized").replace("%s", args[1]));
                        return false;
                    }
                    if (isNotDouble(args[2])) {
                        player.sendMessage(TranslatableChatComponent.read("fallbackCommand.double_required").replace("%s", "3"));
                        return false;
                    }
                    double amount = Double.parseDouble(args[2]);
                    double balance = player.getPersistentDataContainer().get(Main.getInstance().getBalance(), PersistentDataType.DOUBLE);

                    if (!Main.getInstance().getBankAccounts().containsKey(player.getUniqueId())) {
                        player.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.player_executor.bank_account_missing"));
                        return false;
                    }
                    BankManager manager = new BankManager(player, amount + balance);
                    player.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.player_executor.give_coins_to_player").replace("%%s", String.valueOf(manager.getBalance())).replace("%s", String.valueOf(amount)));
                    if (player != sender) {
                        target.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.player_executor.receive_coins_from_player").replace("%%%s", String.valueOf(manager.getBalance())).replace("%%s", player.getName()).replace("%s", String.valueOf(amount)));
                    }
                    return false;
                }
            }
            if (args.length == 4) {
                if (args[0].equalsIgnoreCase("canceloffer")) {
                    player.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_many_arguments").replace("%%s", "1").replace("%s", String.valueOf(args.length)));
                    return false;
                }
                if (args[0].equalsIgnoreCase("buy")) {
                    player.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_many_arguments").replace("%%s", "2").replace("%s", String.valueOf(args.length)));
                    return false;
                }
                if (args[0].equalsIgnoreCase("givecoins")) {
                    player.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_many_arguments").replace("%%s", "3").replace("%s", String.valueOf(args.length)));
                    return false;
                }
                if (args[0].equalsIgnoreCase("createoffer")) {
                    ItemStack item = new ItemStackArgument().parse(args[1]);
                    if (item == null) {
                        String itemId = args[1];
                        if (itemId.startsWith("minecraft:")) {
                            player.sendMessage(TranslatableChatComponent.read("fallbackCommand.wrong_item_id_provided").replace("%s", args[1]));
                        } else {
                            player.sendMessage(TranslatableChatComponent.read("fallbackCommand.wrong_item_id_provided").replace("%s", "minecraft:" + args[1]));
                        }
                        return false;
                    }
                    if (isNotInt(args[2])) {
                        player.sendMessage(TranslatableChatComponent.read("fallbackCommand.integer_required").replace("%s", "3"));
                        return false;
                    }
                    if (isNotInt(args[3])) {
                        player.sendMessage(TranslatableChatComponent.read("fallbackCommand.integer_required").replace("%s", "4"));
                        return false;
                    }
                    int amount = Integer.parseInt(args[2]);
                    int price = Integer.parseInt(args[3]);

                    for (int i = 0; i < player.getInventory().getSize(); i++) {
                        ItemStack playerInventoryItem = player.getInventory().getItem(i);
                        if (playerInventoryItem == null) continue;
                        if (playerInventoryItem.isSimilar(item)) {
                            if (playerInventoryItem.getAmount() >= amount) {
                                playerInventoryItem.setAmount(playerInventoryItem.getAmount() - amount);
                                player.getInventory().setItem(i, playerInventoryItem);
                                ItemUtils.createSalableItem(player.getName(), playerInventoryItem, price);
                            } else {
                                player.sendMessage(TranslatableChatComponent.read("itemCreateOffer.player_executor.too_few_items").replace("%%s", String.valueOf(amount)).replace("%s", "minecraft:" + item.getType().name().toLowerCase()));
                            }
                            return false;
                        }
                    }
                    player.sendMessage(TranslatableChatComponent.read("itemCreateOffer.player_executor.missing_item"));
                    return false;
                }
                return false;
            }
            if (args.length >= 5) {
                if (args[0].equalsIgnoreCase("canceloffer")) {
                    player.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_many_arguments").replace("%%s", "1").replace("%s", String.valueOf(args.length)));
                    return false;
                }
                if (args[0].equalsIgnoreCase("buy")) {
                    player.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_many_arguments").replace("%%s", "2").replace("%s", String.valueOf(args.length)));
                    return false;
                }
                if (args[0].equalsIgnoreCase("givecoins")) {
                    player.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_many_arguments").replace("%%s", "3").replace("%s", String.valueOf(args.length)));
                    return false;
                }
                if (args[0].equalsIgnoreCase("createoffer")) {
                    player.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_many_arguments").replace("%%s", "4").replace("%s", String.valueOf(args.length)));
                    return false;
                }
                return false;
            }
            return false;
        }
        if (sender instanceof ConsoleCommandSender console) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("givecoins")) {
                    console.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_few_arguments").replace("%%s", "3").replace("%s", String.valueOf(args.length)));
                    return false;
                }
                if (args[0].equalsIgnoreCase("buy")) {
                    console.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.wrong_executor"));
                    return false;
                }
                if (args[0].equalsIgnoreCase("canceloffer")) {
                    console.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.wrong_executor"));
                    return false;
                }
                if (args[0].equalsIgnoreCase("createoffer")) {
                    console.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_few_arguments").replace("%%s", "4").replace("%s", String.valueOf(args.length)));
                    return false;
                }
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("buy")) {
                    console.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.wrong_executor"));
                    return false;
                }
                if (args[0].equalsIgnoreCase("canceloffer")) {
                    console.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.wrong_executor"));
                    return false;
                }
                if (args[0].equalsIgnoreCase("createoffer")) {
                    console.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_few_arguments").replace("%%s", "4").replace("%s", String.valueOf(args.length)));
                    return false;
                }
                if (args[0].equalsIgnoreCase("givecoins")) {
                    console.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_few_arguments").replace("%%s", "3").replace("%s", String.valueOf(args.length)));
                    return false;
                }
            }
            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("buy")) {
                    console.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.wrong_executor"));
                    return false;
                }
                if (args[0].equalsIgnoreCase("canceloffer")) {
                    console.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.wrong_executor"));
                    return false;
                }
                if (args[0].equalsIgnoreCase("createoffer")) {
                    console.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_few_arguments").replace("%%s", "4").replace("%s", String.valueOf(args.length)));
                    return false;
                }
                if (args[0].equalsIgnoreCase("givecoins")) {
                    Player target = new PlayerArgument().parse(args[1]);
                    if (target == null) {
                        console.sendMessage(TranslatableChatComponent.read("fallbackCommand.player_not_recognized").replace("%s", args[1]));
                        return false;
                    }
                    if (isNotDouble(args[2])) {
                        console.sendMessage(TranslatableChatComponent.read("fallbackCommand.double_required").replace("%s", "3"));
                        return false;
                    }
                    double amount = Double.parseDouble(args[2]);
                    double balance = target.getPersistentDataContainer().get(Main.getInstance().getBalance(), PersistentDataType.DOUBLE);

                    if (!Main.getInstance().getBankAccounts().containsKey(target.getUniqueId())) {
                        console.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.console_executor.target_bank_account_missing").replace("%s", target.getName()));
                        return false;
                    }
                    BankManager manager = new BankManager(target, balance + amount);
                    console.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.console_executor.give_coins_to_player").replace("%%%s", String.valueOf(manager.getBalance())).replace("%%s", String.valueOf(amount)).replace("%s", target.getName()));
                    target.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.console_executor.receive_coins_from_console").replace("%%s", String.valueOf(manager.getBalance())).replace("%s", String.valueOf(amount)));
                    return false;
                }
            }
            if (args.length == 4) {
                if (args[0].equalsIgnoreCase("buy")) {
                    console.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.wrong_executor"));
                    return false;
                }
                if (args[0].equalsIgnoreCase("canceloffer")) {
                    console.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.wrong_executor"));
                    return false;
                }
                if (args[0].equalsIgnoreCase("givecoins")) {
                    sender.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_many_arguments").replace("%%s", "3").replace("%s", String.valueOf(args.length)));
                    return false;
                }
                if (args[0].equalsIgnoreCase("createoffer")) {
                    ItemStack item = new ItemStackArgument().parse(args[1]);
                    if (item == null) {
                        String itemId = args[1];
                        if (itemId.startsWith("minecraft:")) {
                            console.sendMessage(TranslatableChatComponent.read("fallbackCommand.wrong_item_id_provided").replace("%s", args[1]));
                        } else {
                            console.sendMessage(TranslatableChatComponent.read("fallbackCommand.wrong_item_id_provided").replace("%s", "minecraft:" + args[1]));
                        }
                        return false;
                    }
                    if (isNotInt(args[2])) {
                        console.sendMessage(TranslatableChatComponent.read("fallbackCommand.integer_required").replace("%s", "3"));
                        return false;
                    }
                    if (isNotInt(args[3])) {
                        console.sendMessage(TranslatableChatComponent.read("fallbackCommand.integer_required").replace("%s", "4"));
                        return false;
                    }
                    int amount = Integer.parseInt(args[2]);
                    int price = Integer.parseInt(args[3]);
                    item.setAmount(amount);

                    ItemUtils.createSalableItem("console", item, price);
                    console.sendMessage(TranslatableChatComponent.read("itemCreateOffer.console_executor.console_created_offer").replace("%%s", amount + "").replace("%s", item.getType().name()));
                    Bukkit.getOnlinePlayers().forEach(p ->
                            p.sendMessage(TranslatableChatComponent.read("itemCreateOffer.console_executor.special_offer_available").replace("%%%s", price + "").replace("%%s", amount + "").replace("%s", item.getType().name()))
                    );
                    return false;
                }
                return false;
            }
            if (args.length >= 5) {
                if (args[0].equalsIgnoreCase("buy")) {
                    console.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.wrong_executor"));
                    return false;
                }
                if (args[0].equalsIgnoreCase("canceloffer")) {
                    console.sendMessage(TranslatableChatComponent.read("giveCoinsCommand.wrong_executor"));
                    return false;
                }
                if (args[0].equalsIgnoreCase("givecoins")) {
                    console.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_many_arguments").replace("%%s", "3").replace("%s", String.valueOf(args.length)));
                    return false;
                }
                if (args[0].equalsIgnoreCase("createoffer")) {
                    console.sendMessage(TranslatableChatComponent.read("fallbackCommand.too_many_arguments").replace("%%s", "4").replace("%s", String.valueOf(args.length)));
                    return false;
                }
            }
            return false;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 1 -> {
                return new StringArgument().suggests(ArgumentType.STRING, args[0], Arrays.asList("canceloffer", "createoffer", "buy", "givecoins"));
            }
            case 2 -> {
                if (args[0].equalsIgnoreCase("createoffer")) {
                    return new ItemStackArgument().suggests(ArgumentType.ITEM, args[1], null);
                }
                if (args[0].equalsIgnoreCase("buy")) {
                    return new ItemStackArgument().suggests(ArgumentType.ITEM, args[1], List.of("special"));
                }
                if (args[0].equalsIgnoreCase("givecoins")) {
                    return new PlayerArgument().suggests(ArgumentType.PLAYER, args[1], null);
                }
                if (args[0].equalsIgnoreCase("canceloffer")) {
                    return new ArrayList<>();
                }
            }
            default -> {
                return new ArrayList<>();
            }
        }
        return null;
    }

    private boolean isNotInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException exception) {
            return true;
        }
        return false;
    }

    private boolean isNotDouble(String s) {
        try {
            Double.parseDouble(s);
        } catch (NumberFormatException exception) {
            return true;
        }
        return false;
    }
}
