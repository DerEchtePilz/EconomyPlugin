package me.derechtepilz.economy.minecraft;

import dev.jorel.commandapi.CommandAPIHandler;
import dev.jorel.commandapi.CommandTree;
import me.derechtepilz.economy.utility.TranslatableChatComponent;

import java.util.ArrayList;
import java.util.List;

public class HelpCommand {
    public HelpCommand() {
        new CommandTree("economyhelp")
                .executes((sender, args) -> {
                    sender.sendMessage("§e------- " + TranslatableChatComponent.read("helpCommand.help") + " §e---------------------");
                    List<String> commands = new ArrayList<>();
                    for (CommandAPIHandler.RegisteredCommand registeredCommand : CommandAPIHandler.getInstance().registeredCommands) {
                        if (!commands.contains(registeredCommand.command())) {
                            commands.add(registeredCommand.command());
                        }
                    }
                    for (String command : commands) {
                        sender.sendMessage("§6/" + command);
                    }
                })
                .register();
    }
}
