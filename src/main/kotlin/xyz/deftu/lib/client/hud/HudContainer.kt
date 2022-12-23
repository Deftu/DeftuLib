package xyz.deftu.lib.client.hud

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIContainer

class HudContainer : UIContainer() {
    override fun addChild(component: UIComponent): UIComponent {
        // Fail if the component is not a HudComponent type.
        if (component !is HudComponent)
            throw IllegalArgumentException("HudContainer can only contain HudComponents")

        // Ensure all children are of the HudComponent type.
        val children = childrenOfType<HudComponent>()
        this.children.clear()
        this.children.addAll(children)

        // Add the new component as a child.
        return super.addChild(component)
    }

    fun getHudChildren() = children.filterIsInstance<HudComponent>()
}
