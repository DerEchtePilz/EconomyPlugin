package io.github.derechtepilz.economycore;

import org.bukkit.Bukkit;

import java.util.logging.Level;
import java.util.logging.Logger;

class EconomyAPILogger extends Logger {
	protected EconomyAPILogger() {
		super("EconomyAPI", null);
		setParent(Bukkit.getServer().getLogger());
		setLevel(Level.ALL);
	}
}
