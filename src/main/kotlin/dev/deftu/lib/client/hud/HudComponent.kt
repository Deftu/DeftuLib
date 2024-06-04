package dev.deftu.lib.client.hud

import gg.essential.elementa.components.UIContainer

abstract class HudComponent : UIContainer() {

    internal lateinit var container: HudContainer

    lateinit var hudWindow: DraggableHudWindow
        internal set
    internal val moveListeners = mutableListOf<HudComponent.(posX: Float, posY: Float) -> Unit>()
    private val removeListeners = mutableListOf<HudComponent.() -> Unit>()

    fun remove() {
        removeListeners.forEach { it.invoke(this) }
        hudWindow.remove(this)
    }

    fun onMove(listener: HudComponent.(posX: Float, posY: Float) -> Unit) {
        moveListeners.add(listener)
    }

    fun onRemove(listener: HudComponent.() -> Unit) {
        removeListeners.add(listener)
    }

}
