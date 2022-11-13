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

    fun addEnableChangeListener(listener: Consumer<Boolean>) = apply { enableChangeListeners.add(listener) }
    fun addVisibilityChangeListener(listener: Consumer<Boolean>) = apply { visibilityChangeListeners.add(listener) }

    fun closeParent() = parent.close()
    fun appendSibling(item: ContextMenuItem) = apply { parent.addItem(item) }
    fun appendSibling(item: () -> ContextMenuItem) = apply { parent.addItem(item()) }

    fun performAction() = action.accept(this)

    internal fun setupComponent(component: ContextMenuComponent): ContextMenuItemComponent {
        this.parent = component
        return ContextMenuItemComponent(this)
    }
}
