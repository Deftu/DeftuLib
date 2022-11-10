package xyz.deftu.lib.client

import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.OutlineEffect
import gg.essential.universal.UKeyboard
import xyz.deftu.lib.client.hud.DraggableHudWindow.Companion.ofHud
import xyz.deftu.lib.client.hud.HudComponent
import xyz.deftu.lib.events.KeyInputEvent
import java.awt.Color

object Test {
    fun start() {
        BoxHudComponent(Color.GRAY, Color.WHITE).ofHud(DeftuLibClient.draggableHudWindow, "deftulib:box")
        BoxHudComponent(Color.RED, Color.BLACK).ofHud(DeftuLibClient.draggableHudWindow, "deftulib:box2")

        KeyInputEvent.EVENT.register { key, scancode, action, mods ->
            if (UKeyboard.KEY_I != key) return@register

            DeftuLibClient.draggableHudWindow.openMenu()
        }
    }

    private class BoxHudComponent(
        color: Color,
        outlineColor: Color
    ) : HudComponent() {
        init {
            constrain {
                width = 100.pixels
                height = 100.pixels
            }

            UIBlock(color).constrain {
                width = 100.percent
                height = 100.percent
            } effect OutlineEffect(outlineColor, 1f) childOf this
        }
    }
}
