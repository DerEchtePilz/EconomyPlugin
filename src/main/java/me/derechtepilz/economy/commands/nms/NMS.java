package me.derechtepilz.economy.commands.nms;

import com.mojang.brigadier.arguments.ArgumentType;
import me.derechtepilz.economy.commands.arguments.EntitySelectorArgument;

public interface NMS {
    ArgumentType<?> _ArgumentEntity(EntitySelectorArgument.EntitySelector selector);
    ArgumentType<?> _ArgumentItem();
}
