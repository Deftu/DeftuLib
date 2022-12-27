package xyz.deftu.lib.client.gui.context

import net.minecraft.text.Text
import java.util.function.Consumer

data class ContextMenuItem internal constructor(
    var text: Text,
    val action: Consumer<ContextMenuItem>
) {
    private lateinit var parent: ContextMenuComponent
    private val enableChangeListeners = mutableListOf<Consumer<Boolean>>()
    private val visibilityChangeListeners = mutableListOf<Consumer<Boolean>>()

    var enabled = true
        set(value) {
            field = value
            enableChangeListeners.forEach { it.accept(value) }
        }
    var visible = true
        set(value) {
            field = value
            visibilityChangeListeners.forEach { it.accept(value) }
        }

    fun setEnabled(enabled: Boolean) = apply { this.enabled = enabled }
    fun setVisible(visible: Boolean) = apply { this.visible = visible }
    fun setEnabled(enabled: () -> Boolean) = apply { this.enabled = enabled() }
    fun setVisible(visible: () -> Boolean) = apply { this.visible = visible() }
    fun remove() = apply { parent.removeItem(this) }

    fun addEnableChangeListener(listener: Consumer<Boolean>) = apply { enableChangeListeners.add(listener) }
    fun removeEnableChangeListener(listener: Consumer<Boolean>) = apply { enableChangeListeners.remove(listener) }
    fun addVisibilityChangeListener(listener: Consumer<Boolean>) = apply { visibilityChangeListeners.add(listener) }
    fun removeVisibilityChangeListener(listener: Consumer<Boolean>) = apply { visibilityChangeListeners.remove(listener) }

    fun closeParent() = parent.close()
    fun appendSibling(item: ContextMenuItem) = apply { parent.addItem(item) }
    fun appendSibling(item: () -> ContextMenuItem) = apply { parent.addItem(item()) }

    fun performAction() = action.accept(this)

    internal fun setupComponent(component: ContextMenuComponent): ContextMenuItemComponent {
        this.parent = component
        if (this.parent.hasItem(this)) throw IllegalStateException("This item is already added to a context menu!")
        return ContextMenuItemComponent(this)
    }
}
