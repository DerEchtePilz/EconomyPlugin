package me.derechtepilz.economy.bukkitcommands.arguments;

import me.derechtepilz.economy.bukkitcommands.api.Argument;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AdvancementArgument implements Argument<Advancement> {
    @Override
    public Advancement parse(String input) {
        return Bukkit.getAdvancement(NamespacedKey.minecraft(input));
    }

    @Override
    public String getType() {
        return ArgumentTypes.ADVANCEMENT_ARGUMENT.getType();
    }

    public List<String> suggests(String argument, List<String> additionalArguments) {
        List<String> suggestions = new ArrayList<>();
        List<String> advancementKeys = new ArrayList<>();

        List<String> otherSuggestions = new ArrayList<>();
        if (additionalArguments != null) {
            if (argument.equals("")) {
                otherSuggestions.addAll(additionalArguments);
            } else {
                for (String additionalArgument : additionalArguments) {
                    if (additionalArgument.startsWith(argument)) {
                        otherSuggestions.add(additionalArgument);
                    }
                }
            }
        }

        Iterator<Advancement> it = Bukkit.advancementIterator();
        while (it.hasNext()) {
            Advancement advancement = it.next();
            advancementKeys.add(advancement.getKey().getKey());
        }
        if (argument.equals("")) {
            if (additionalArguments != null) {
                advancementKeys.addAll(otherSuggestions);
            }
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
