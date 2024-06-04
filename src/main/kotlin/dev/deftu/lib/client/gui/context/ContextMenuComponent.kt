package dev.deftu.lib.client.gui.context

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.ScrollComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.Window
import gg.essential.elementa.constraints.RelativeWindowConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.OutlineEffect
import dev.deftu.lib.client.gui.DeftuPalette
import java.util.function.Consumer

class ContextMenuComponent internal constructor() : UIContainer() {
    private var checkBaseComponent = true
    private var baseComponent: UIComponent? = null
        get() = if (checkBaseComponent) {
            field ?: Window.of(this)
        } else Window.of(this)

    // Internal variables
    private var itemCount = 0
    private val itemVisibilityChangeListeners = mutableMapOf<ContextMenuItem, Consumer<Boolean>>()
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
            height = ContextMenuItemComponent.HEIGHT
        }.onMouseClick {
            it.stopImmediatePropagation()
        }
    }

    fun updateHeight() {
        if (itemCount == 0) return

        val window = Window.of(this)
        val maxHeight = RelativeWindowConstraint(1f / 3f)
        val newHeight = ContextMenuItemComponent.HEIGHT * itemCount
        if (newHeight.getHeight(this) > maxHeight.getHeight(window)) {
            constrain {
                height = maxHeight
            }
        } else {
            constrain {
                height = newHeight
            }
        }
    }

    fun checkVisibility() {
        if (baseComponent == null) return

        // check if the context menu is visible on the x axis
        val window = Window.of(this)
        val windowWidth = RelativeWindowConstraint(1f).getWidth(window)
        val currentX = getLeft()
        val width = getWidth()

        if (currentX + width > windowWidth) {
            val newX = currentX - width
            if (newX < 0) {
                // if the context menu is too big to fit on the screen, just move it to the left
                constrain {
                    x = 0.pixels()
                }
            } else {
                constrain {
                    x = newX.pixels()
                }
            }
        }

        // check if the context menu is visible on the y axis
        val windowHeight = RelativeWindowConstraint(1f).getHeight(window)
        val currentY = getTop()
        val height = getHeight()

        if (currentY + height > windowHeight) {
            val newY = currentY - height
            if (newY < 0) {
                // if the context menu is too big to fit on the screen, just move it to the top
                constrain {
                    y = 0.pixels()
                }
            } else {
                constrain {
                    y = newY.pixels()
                }
            }
        }
    }

    override fun afterInitialization() {
        setFloating(true)

        val checkedComponent = baseComponent
        checkedComponent?.onMouseClick {
            if (it.target != this@ContextMenuComponent) {
                close()
            }
        }

        updateHeight()
        checkVisibility()
        super.afterInitialization()
    }

    fun close() {
        setFloating(false)
        parent.removeChild(this)
    }

    fun hasItem(item: ContextMenuItem) = container.children.any { it is ContextMenuItemComponent && it.item == item }

    fun addItem(item: ContextMenuItem) = apply {
        if (!item.visible) return@apply
        item.setupComponent(this) childOf container
        itemCount++
        updateHeight()
        checkVisibility()

        val callback: Consumer<Boolean> = Consumer { value ->
            if (value) itemCount++ else itemCount--
        }
        itemVisibilityChangeListeners[item] = callback
        item.addVisibilityChangeListener(callback)
    }

    fun addItems(items: List<ContextMenuItem>) = apply {
        items.forEach { addItem(it) }
    }

    fun addItems(vararg items: ContextMenuItem) = apply {
        items.forEach { addItem(it) }
    }

    fun removeItem(item: ContextMenuItem) = apply {
        val filtered = container.children.filter { it is ContextMenuItemComponent && it.item == item }
        filtered.forEach { container.removeChild(it) }
        itemCount--
        updateHeight()
        checkVisibility()

        val callback = itemVisibilityChangeListeners[item]
        if (callback != null) {
            item.removeVisibilityChangeListener(callback)
            itemVisibilityChangeListeners.remove(item)
        }
    }

    fun removeItems(items: List<ContextMenuItem>) = apply {
        items.forEach { removeItem(it) }
    }

    fun removeItems(vararg items: ContextMenuItem) = apply {
        items.forEach { removeItem(it) }
    }

    fun setCheckBaseComponent(checkBaseComponent: Boolean) = apply {
        this.checkBaseComponent = checkBaseComponent
    }

    fun setBaseComponent(component: UIComponent) = apply {
        this.baseComponent = component
    }
}
