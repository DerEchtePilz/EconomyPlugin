package me.derechtepilz.economy.bukkitcommands.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.derechtepilz.economy.bukkitcommands.arguments.ArgumentTypes;

import java.util.List;

class CommandTree {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    JsonArray commandTree = new JsonArray();

    public CommandTree(List<ArgumentTypes> argumentTypesList, String[] args) {
        for (int i = 0; i < args.length; i++) {
            JsonObject argument = new JsonObject();
            argument.addProperty("type", argumentTypesList.get(i).name());
            commandTree.add(argument);
        }
    }

    String getCommandTree() {
        return gson.toJson(commandTree);
    }
}
