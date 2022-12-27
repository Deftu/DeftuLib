package xyz.deftu.lib.client.gui.context

import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.pixels
import net.minecraft.text.Text
import java.util.function.Consumer

object ContextMenu {
    @JvmStatic
    fun create(
        xPos: Float,
        yPos: Float
    ) = ContextMenuComponent().constrain {
        x = xPos.pixels
        y = yPos.pixels
    }

    @JvmStatic
    fun item(
        text: Text,
        action: Consumer<ContextMenuItem>
    ) = ContextMenuItem(text, action)
}
