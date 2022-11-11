package xyz.deftu.lib.client.gui.context

import gg.essential.elementa.components.ScrollComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.Window
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.OutlineEffect
import xyz.deftu.lib.client.gui.DeftuPalette

class ContextMenuComponent private constructor() : UIContainer() {
    companion object {
        @JvmStatic
        fun create(
            xPos: Float,
            yPos: Float,
            vararg item: ContextMenuItem
        ) = ContextMenuComponent().constrain {
            x = xPos.pixels
            y = yPos.pixels
        }.apply {
            item.forEach(::addItem)
        }
    }

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
            width = 75.pixels
            height = 100.pixels
        }.onMouseClick {
            it.stopImmediatePropagation()
        }
    }

    override fun afterInitialization() {
        setFloating(true)

        val window = Window.of(this)
        window.onMouseClick {
            var target = it.target
            while (true) {
                if (target == this@ContextMenuComponent) return@onMouseClick

                if (target is Window) {
                    close()
                    break
                }

                target = target.parent
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
}
