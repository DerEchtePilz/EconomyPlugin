package me.derechtepilz.economy;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIConfig;
import me.derechtepilz.economycore.EconomyAPI;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class Main extends JavaPlugin {

    private boolean isVersionSupported;
    private final Main main = this;

    // Store item-related fields
    private final List<UUID> registeredItemUuids = new ArrayList<>();
    private final HashMap<UUID, ItemStack> registeredItems = new HashMap<>();

    // Initialize command classes
    EconomyCommand economyCommand = new EconomyCommand(main);

    @Override
    public void onEnable() {
        EconomyAPI.onEnable(main);

        if (isVersionSupported) {
            CommandAPI.onEnable(main);
            commandRegistration();
        }

        listenerRegistration();
    }

    @Override
    public void onLoad() {
        EconomyAPI.onLoad();

        String version = Bukkit.getBukkitVersion().split("-")[0];
        isVersionSupported = VersionHandler.isVersionSupported(version);

        if (isVersionSupported) {
            CommandAPI.onLoad(new CommandAPIConfig().missingExecutorImplementationMessage("You cannot execute this command!"));
        }
    }

    @Override
    public void onDisable() {
        EconomyAPI.onDisable();
        CommandAPI.onDisable();
    }

    private void commandRegistration() {
        economyCommand.register();
    }

    private void listenerRegistration() {

    }

    // Store item-related methods
    public List<UUID> getRegisteredItemUuids() {
        return registeredItemUuids;
    }

    public HashMap<UUID, ItemStack> getRegisteredItems() {
        return registeredItems;
    }
}
