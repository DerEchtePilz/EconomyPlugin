package io.github.derechtepilz.economy.componentapi

import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.entity.Player

class ChatComponentAPI {

    private val textComponent = TextComponent()
    private val textComponentList: MutableList<TextComponent> = mutableListOf()

    fun appendText(text: String): ChatComponentAPI {
        textComponent.text = text
        return this
    }

    fun withClickEvent(actionText: String, action: ClickEvent.Action): ChatComponentAPI {
        textComponent.clickEvent = ClickEvent(action, actionText)
        return this
    }

    fun showTextOnHover(text: String): ChatComponentAPI {
        textComponent.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text(text))
        return this
    }

    fun appendTextComponent(component: ChatComponentAPI): ChatComponentAPI {
        if (!textComponentList.contains(textComponent)) {
            textComponentList.add(textComponent)
        }
        textComponentList.add(ChatComponentAPI().appendText(" ").buildTextComponent())
        textComponentList.add(component.buildTextComponent())
        return this
    }

    private fun buildTextComponent(): TextComponent {
        return textComponent
    }

    fun sendToPlayer(player: Player) {
        if (!textComponentList.contains(textComponent)) {
            textComponentList.add(textComponent)
        }
        player.spigot().sendMessage(*textComponentList.toTypedArray())
    }

}
