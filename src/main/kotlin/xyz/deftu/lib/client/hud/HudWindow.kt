package xyz.deftu.lib.client.hud

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.components.Window
import gg.essential.universal.UMatrixStack
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback

open class HudWindow {
    val window = Window(ElementaVersion.V2)
    private var initialized = false

    fun initialize() {
        if (initialized) return

        HudRenderCallback.EVENT.register { stack, _ ->
            window.draw(UMatrixStack(stack))
        }

        initialized = true
    }
}
