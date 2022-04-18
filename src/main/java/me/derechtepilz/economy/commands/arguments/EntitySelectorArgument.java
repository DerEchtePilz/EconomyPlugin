package me.derechtepilz.economy.commands.arguments;

import me.derechtepilz.economy.commands.EconomyPluginCommandAPI;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;

public class EntitySelectorArgument extends Argument {
    private final EntitySelectorArgument.EntitySelector selector;

    public EntitySelectorArgument(String nodeName) {
        this(nodeName, EntitySelector.ONE_ENTITY);
    }

    public EntitySelectorArgument(String nodeName, EntitySelector selector) {
        super(nodeName, EconomyPluginCommandAPI.getInstance().getNMS()._ArgumentEntity(selector));
        this.selector = selector;
    }

    Class<?> getPrimitiveType(EntitySelector selector) {
        Class selected;
        switch (this.selector) {
            case MANY_ENTITIES:
            case MANY_PLAYERS: {
                selected = Collection.class;
                break;
            }
            case ONE_ENTITY: {
                selected = Entity.class;
                break;
            }
            case ONE_PLAYER: {
                selected = Player.class;
            }
            default: {
                selected = Collection.class;
            }
        }
        return selected;
    }

    public enum EntitySelector {
        ONE_ENTITY,
        MANY_ENTITIES,
        ONE_PLAYER,
        MANY_PLAYERS
    }

}
