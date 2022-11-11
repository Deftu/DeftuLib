package xyz.deftu.lib.client.hud

import gg.essential.elementa.components.UIContainer

abstract class HudComponent : UIContainer() {
    lateinit var hudWindow: DraggableHudWindow
        internal set
    internal val moveListeners = mutableListOf<HudComponent.() -> Unit>()
    internal val removeListeners = mutableListOf<HudComponent.() -> Unit>()

    fun remove() {
        removeListeners.forEach { it.invoke(this) }
        hudWindow.remove(this)
    }

    fun onMove(listener: HudComponent.() -> Unit) {
        moveListeners.add(listener)
    }

    fun onRemove(listener: HudComponent.() -> Unit) {
        removeListeners.add(listener)
    }
}
