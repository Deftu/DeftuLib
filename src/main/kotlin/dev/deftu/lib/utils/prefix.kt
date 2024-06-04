package dev.deftu.lib.utils

import gg.essential.universal.ChatColor

/**
 * Provides a simple way to build
 * a chat prefix with a color, brackets
 * and different bracket types.
 */
class ChatPrefixBuilder {

    private val block = ChatPrefixBlock()

    fun setName(name: String) = apply { block.name = name }

    fun setColor(color: ChatColor) = apply { block.color = color }

    fun setBracketType(type: ChatPrefixType) = apply { block.brackets.type = type }

    fun setBracketColor(color: ChatColor) = apply { block.brackets.color = color }

    fun setBracketBold(bold: Boolean) = apply { block.brackets.bold = bold }

    fun build() = block.build()

}

/**
 * Provides a simple way to build
 * a chat suffix with a color, brackets
 * and different bracket types.
 */
fun prefix(block: ChatPrefixBlock.() -> Unit) =
    ChatPrefixBlock().apply(block).build()

class ChatPrefixBlock {

    lateinit var name: String
    lateinit var color: ChatColor
    internal var brackets = ChatPrefixBracketBlock()

    fun brackets(block: ChatPrefixBracketBlock.() -> Unit) = apply {
        this.brackets.apply(block)
    }

    internal fun build(): String {
        var value = ""
        if (brackets.type == ChatPrefixType.BRACKETS) {
            value += brackets.color
            if (brackets.bold) value += ChatColor.BOLD
            value += "["
        }

        value += ChatColor.RESET
        value += color
        value += name
        value += ChatColor.RESET
        value += brackets.color
        if (brackets.bold) value += ChatColor.BOLD
        value += if (brackets.type == ChatPrefixType.BRACKETS) "]" else " >"
        value += ChatColor.RESET
        value += " "
        return value
    }

    class ChatPrefixBracketBlock {
        var type = ChatPrefixType.BRACKETS
        var color = ChatColor.DARK_GRAY
        var bold = true
    }

}

enum class ChatPrefixType {
    BRACKETS,
    CARET
}
