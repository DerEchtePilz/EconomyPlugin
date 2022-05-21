package me.derechtepilz.economy.bukkitcommands.commands;

public class CommandBase {
    public boolean isNotInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException exception) {
            return true;
        }
        return false;
    }

    public boolean isNotDouble(String s) {
        try {
            Double.parseDouble(s);
        } catch (NumberFormatException exception) {
            return true;
        }
        return false;
    }
}
