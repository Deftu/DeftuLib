package xyz.deftu.lib.utils

import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import xyz.deftu.lib.DeftuLib

object ChatHelper {
    @JvmStatic
    fun sendClientMessage(message: Text, prefix: String = DeftuLib.PREFIX) {
        val text = TextHelper.createLiteralText("")
        text.append(TextHelper.createLiteralText(prefix))
        text.append(message)
        MinecraftClient.getInstance().inGameHud.chatHud.addMessage(text)
    }

    @JvmStatic
    fun sendClientMessage(message: String, prefix: String = DeftuLib.PREFIX) = sendClientMessage(TextHelper.createLiteralText(message), prefix)
}
