package xyz.deftu.lib.client.gui.context

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.ScrollComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.Window
import gg.essential.elementa.constraints.RelativeWindowConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.OutlineEffect
import xyz.deftu.lib.client.gui.DeftuPalette

class ContextMenuComponent internal constructor() : UIContainer() {
    private var baseComponent: UIComponent? = null

    // Internal variables
    private val background by UIBlock(DeftuPalette.getBackground()).constrain {
        width = 100.percent
        height = 100.percent
    } effect OutlineEffect(
        color = DeftuPalette.getPrimary(),
        width = 1f,
        drawInsideChildren = true,
        drawAfterChildren = true
    ) childOf this
    private val container by ScrollComponent().constrain {
        width = 100.percent
        height = 100.percent
    } childOf background

    init {
        constrain {
            width = 100.pixels
            height = RelativeWindowConstraint(1f / 3f)
        }.onMouseClick {
            it.stopImmediatePropagation()
        }
    }

    override fun afterInitialization() {
        setFloating(true)

        val checkedComponent = baseComponent ?: Window.of(this)
        checkedComponent.onMouseClick {
            println("clicked ${it.target}. (${if (it.target == this@ContextMenuComponent) "correct context menu" else "wrong component"})")
            if (it.target != this@ContextMenuComponent) {
                close()
            }
        }

        super.afterInitialization()
    }

    fun close() {
        setFloating(false)
        parent.removeChild(this)
    }

    fun addItem(item: ContextMenuItem) = apply {
        if (!item.visible) return@apply
        item.setupComponent(this) childOf container
    }

    fun addItems(items: List<ContextMenuItem>) = apply {
        items.forEach { addItem(it) }
    }

    fun addItems(vararg items: ContextMenuItem) = apply {
        items.forEach { addItem(it) }
    }

    fun setBaseComponent(component: UIComponent) = apply {
        this.baseComponent = component
    }
}
