package io.github.derechtepilz.economy.utils

import org.bukkit.entity.Player

fun Player.sendMessage(component: TranslatableComponent) = sendMessage(component.toMessage())