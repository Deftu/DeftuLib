package xyz.deftu.lib.client.hud

import gg.essential.elementa.components.UIContainer

abstract class HudComponent : UIContainer() {
    internal val moveListeners = mutableListOf<HudComponent.() -> Unit>()

    fun onMove(listener: HudComponent.() -> Unit) {
        moveListeners.add(listener)
    }
}
