package xyz.deftu.lib.client

import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.dsl.*
import org.lwjgl.glfw.GLFW
import xyz.deftu.lib.DeftuLib
import xyz.deftu.lib.client.hud.DraggableHudWindow.Companion.ofHud
import xyz.deftu.lib.client.hud.HudComponent
import xyz.deftu.lib.events.KeyInputEvent
import java.awt.Color

object Test {
    private class BoxHudComponent(
        color: Color
    ) : HudComponent() {
        private val block by UIBlock(color).constrain {
            width = 100.percent
            height = 100.percent
        } childOf this
    }

    fun initialize() {
        BoxHudComponent(Color.RED).constrain {
            width = 100.pixels
            height = 100.pixels
        }.ofHud(DeftuLibClient.draggableHudWindow, DeftuLib.ID)
        BoxHudComponent(Color.GREEN).constrain {
            width = 50.pixels
            height = 50.pixels
        }.ofHud(DeftuLibClient.draggableHudWindow, DeftuLib.ID)
        BoxHudComponent(Color.BLUE).constrain {
            width = 200.pixels
            height = 200.pixels
        }.ofHud(DeftuLibClient.draggableHudWindow, DeftuLib.ID)

        KeyInputEvent.EVENT.register { key, _, action, _ ->
            if (key == GLFW.GLFW_KEY_F7 && action == GLFW.GLFW_PRESS) {
                DeftuLibClient.draggableHudWindow.openMenu()
            }
        }
    }
}
