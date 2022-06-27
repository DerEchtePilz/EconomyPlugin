package me.derechtepilz.economy.minecraft;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.RegisteredCommand;
import me.derechtepilz.economy.utility.TranslatableChatComponent;

import java.util.ArrayList;
import java.util.List;

public class HelpCommand {
    public void register() {
        new CommandTree("economyhelp")
                .executes((sender, args) -> {
                    sender.sendMessage("§e------- " + TranslatableChatComponent.read("helpCommand.help") + " §e---------------------");
                    List<String> commands = new ArrayList<>();
                    for (RegisteredCommand registeredCommand : CommandAPI.getRegisteredCommands()) {
                        if (!commands.contains(registeredCommand.commandName())) {
                            commands.add(registeredCommand.commandName());
                        }
                    }
                    for (String command : commands) {
                        sender.sendMessage("§6/" + command);
                    }
                })
                .register();
    }
}
