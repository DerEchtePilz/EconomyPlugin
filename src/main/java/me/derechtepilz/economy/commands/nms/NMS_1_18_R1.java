package me.derechtepilz.economy.commands.nms;

import com.mojang.brigadier.arguments.ArgumentType;
import me.derechtepilz.economy.commands.arguments.EntitySelectorArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;

public class NMS_1_18_R1 implements NMS {
    @Override
    public ArgumentType<?> _ArgumentEntity(EntitySelectorArgument.EntitySelector selector) {
        EntityArgument argument;
        switch (selector) {
            case MANY_PLAYERS -> argument = EntityArgument.players();
            case MANY_ENTITIES -> argument = EntityArgument.entities();
            case ONE_PLAYER -> argument = EntityArgument.player();
            case ONE_ENTITY -> argument = EntityArgument.entity();
            default -> throw new IncompatibleClassChangeError();
        };
        return argument;
    }

    @Override
    public ArgumentType<?> _ArgumentItem() {
        return ItemArgument.item();
    }
}
