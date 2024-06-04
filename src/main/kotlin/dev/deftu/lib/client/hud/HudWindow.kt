package dev.deftu.lib.client.hud

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.components.Window
import gg.essential.universal.UMatrixStack
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback

open class HudWindow {

    val window = Window(ElementaVersion.V5)
    private var initialized = false

    fun initialize() {
        if (initialized) return

        HudRenderCallback.EVENT.register { ctx, _ ->
            //#if MC >= 1.20
            window.draw(UMatrixStack(ctx.matrices))
            //#else
            //$$ window.draw(UMatrixStack(ctx))
            //#endif
        }

        initialized = true
    }

}
