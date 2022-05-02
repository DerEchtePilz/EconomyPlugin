package me.derechtepilz.economy.bukkitcommands.arguments.type;

import me.derechtepilz.economy.bukkitcommands.arguments.Argument;
import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AdvancementArgument implements Argument<Advancement> {
    @Override
    public Advancement parse(String input) {
        return null;
    }

    public List<String> suggests(String argument, List<String> additionalArguments) {
        List<String> suggestions = new ArrayList<>();
        List<String> advancementKeys = new ArrayList<>();
        Iterator<Advancement> it = Bukkit.advancementIterator();
        while (it.hasNext()) {
            Advancement advancement = it.next();
            advancementKeys.add(advancement.getKey().getKey());
        }
        if (argument.equals("")) {
            return advancementKeys;
        }
        for (String advancementKey : advancementKeys) {
            if (advancementKey.startsWith(argument)) {
                suggestions.add(advancementKey);
            }
        }
        return suggestions;
    }
}
