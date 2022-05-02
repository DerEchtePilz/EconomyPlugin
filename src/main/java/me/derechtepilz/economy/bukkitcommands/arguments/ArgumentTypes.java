package me.derechtepilz.economy.bukkitcommands.arguments;

import me.derechtepilz.economy.bukkitcommands.arguments.entity.PlayerArgument;
import me.derechtepilz.economy.bukkitcommands.arguments.general.IntegerArgument;
import me.derechtepilz.economy.bukkitcommands.arguments.type.AdvancementArgument;
import me.derechtepilz.economy.bukkitcommands.arguments.general.StringArgument;
import me.derechtepilz.economy.bukkitcommands.arguments.type.ItemStackArgument;

public enum ArgumentTypes {
    STRING_ARGUMENT(new StringArgument(), "a string"),
    PLAYER_ARGUMENT(new PlayerArgument(), "a player name"),
    ADVANCEMENT_ARGUMENT(new AdvancementArgument(), "an advancement key"),
    ITEMSTACK_ARGUMENT(new ItemStackArgument(), "an item id"),
    INTEGER_ARGUMENT(new IntegerArgument(), "an integer");

    private final Argument<?> argument;
    private final String type;
    ArgumentTypes(Argument<?> argument, String type) {
        this.argument = argument;
        this.type = type;
    }

    public Argument<?> getArgument() {
        return argument;
    }

    public String getType() {
        return type;
    }
}
