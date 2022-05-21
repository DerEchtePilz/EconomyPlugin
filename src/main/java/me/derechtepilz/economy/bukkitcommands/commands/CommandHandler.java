package me.derechtepilz.economy.bukkitcommands.commands;

import me.derechtepilz.economy.bukkitcommands.commands.commandapicommands.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public record CommandHandler(CommandSender sender, Command command, String label, String[] args) {
    public void executeCreateOfferCommand() {
        new CreateOfferCommandExecutor(sender, command, label, args);
    }

    public void executeCancelOfferCommand() {
        new CancelOfferCommandExecutor(sender, command, label, args);
    }

    public void executeBuyCommand() {
        new BuyCommandExecutor(sender, command, label, args);
    }

    public void executeGiveCoinsCommand() {
        new GiveCoinsCommandExecutor(sender, command, label, args);
    }

    public void executeTakeCoinsCommand() {
        new TakeCoinsCommandExecutor(sender, command, label, args);
    }

    public void executeSetCoinsCommand() {
        new SetCoinsCommandExecutor(sender, command, label, args);
    }

    public void executePermissionCommand() {
        new PermissionCommandExecutor(sender, command, label, args);
    }

    public void executeConfigCommand() {
        new ConfigCommandExecutor(sender, command, label, args);
    }
}
