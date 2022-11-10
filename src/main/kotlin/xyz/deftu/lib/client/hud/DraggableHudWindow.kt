package xyz.deftu.lib.client.hud

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.components.Window
import gg.essential.elementa.dsl.*
import gg.essential.universal.UMatrixStack
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import xyz.deftu.lib.client.gui.DraggableHudMenu

open class DraggableHudWindow {
    companion object {
        fun <T : HudComponent> T.ofHud(window: DraggableHudWindow, namespace: String) = apply {
            Window.enqueueRenderOperation {
                val container by window.namespace(namespace)
                this@ofHud childOf container
                window.namespaces[namespace] = container
            }
        }
    }

    protected open val window = Window(ElementaVersion.V2)
    private val namespaces = mutableMapOf<String, HudContainer>()
    private var initialized = false

    fun initialize() {
        if (initialized) return

        HudRenderCallback.EVENT.register { stack, _ ->
            window.draw(UMatrixStack(stack))
        }

        initialized = true
    }

    @JvmOverloads
    fun openMenu(restoreCurrentGuiOnClose: Boolean = true) = MinecraftClient.getInstance().setScreen(DraggableHudMenu(this, restoreCurrentGuiOnClose))
    fun getNamespaces() = namespaces.toMap()
    fun getNamespace(namespace: String) = getNamespaces()[namespace]

    private fun namespace(namespace: String) = namespaces.getOrPut(namespace, ::createNamespaceContainer)
    private fun createNamespaceContainer() = HudContainer().constrain {
        width = 100.percent
        height = 100.percent
    }
}
