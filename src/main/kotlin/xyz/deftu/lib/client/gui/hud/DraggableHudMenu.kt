package xyz.deftu.lib.client.gui.hud

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.UIComponent
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.Window
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.dsl.*
import gg.essential.universal.UKeyboard
import xyz.deftu.lib.DeftuLib
import xyz.deftu.lib.client.gui.context.ContextMenuComponent
import xyz.deftu.lib.client.gui.context.ContextMenuItem
import xyz.deftu.lib.client.hud.DraggableHudWindow
import xyz.deftu.lib.client.hud.HudComponent
import xyz.deftu.lib.client.hud.HudContainer
import xyz.deftu.lib.utils.TextHelper

open class DraggableHudMenu(
    hudWindow: DraggableHudWindow,
    restoreCurrentGuiOnClose: Boolean = true
) : WindowScreen(
    version = ElementaVersion.V2,
    restoreCurrentGuiOnClose = restoreCurrentGuiOnClose
) {
    private var selectedComponent: HudComponent? = null
    private var draggingOffset = 0f to 0f
    private val container by UIContainer().constrain {
        width = 100.percent
        height = 100.percent
    } childOf window

    init {
        // Double check that the window is initialized
        hudWindow.initialize()

        // Run through all the namespaces and add them to the menu
        window.onMouseClick {
            if (it.mouseButton == 0 && !it.target.isHudComponent()) {
                selectedComponent = null
            }
        }

        hudWindow.getNamespaces().map(Map.Entry<String, HudContainer>::value).toList().forEach { container ->
            container.getHudChildren().forEach { component ->
                val container = HudContainer().constrain {
                    width = ChildBasedSizeConstraint()
                    height = ChildBasedSizeConstraint()
                } childOf this.container

                component.onFocus {
                    selectedComponent = component
                }.onFocusLost {
                    selectedComponent = null
                }.onMouseClick {
                    if (it.mouseButton == 0) {
                        grabWindowFocus()
                        draggingOffset = it.relativeX to it.relativeY
                    } else if (it.mouseButton == 1) {
                        displayContextMenu(component, it.absoluteX, it.absoluteY)
                    }
                }.onMouseRelease {
                    draggingOffset = 0f to 0f
                }.onMouseDrag { mouseX, mouseY, mouseButton ->
                    if (selectedComponent != component) return@onMouseDrag

                    if (mouseButton == 0) {
                        move(component, container, mouseX + component.getLeft(), mouseY + component.getTop())
                    }
                }.onKeyType { typedChar, keyCode ->
                    if (component != selectedComponent) return@onKeyType

                    draggingOffset = 0f to 0f
                    when (keyCode) {
                        UKeyboard.KEY_ESCAPE -> selectedComponent = null
                        UKeyboard.KEY_UP -> move(component, container, component.getLeft(), component.getTop() - 1)
                        UKeyboard.KEY_DOWN -> move(component, container, component.getLeft(), component.getTop() + 1)
                        UKeyboard.KEY_LEFT -> move(component, container, component.getLeft() - 1, component.getTop())
                        UKeyboard.KEY_RIGHT -> move(component, container, component.getLeft() + 1, component.getTop())
                    }
                } childOf container
            }
        }
    }

    open fun getDefaultContextMenuItems() = emptyList<ContextMenuItem>()
    open fun displayContextMenu(component: HudComponent, mouseX: Float, mouseY: Float) {
        val contextMenu by ContextMenuComponent.create(
            xPos = mouseX,
            yPos = mouseY,
            item = arrayOf(
                ContextMenuItem(TextHelper.createTranslatableText("${DeftuLib.ID}.hud.menu.remove")) { item ->
                    component.remove()
                    component.hide(true)
                    item.closeParent()
                }
            ) + getDefaultContextMenuItems().toTypedArray()
        ) childOf window
    }

    private fun UIComponent.isHudComponent(): Boolean {
        var parent = this.parent
        while (true) {
            if (parent is HudComponent) return true
            if (parent is Window) return false
            parent = parent.parent
        }
    }

    private fun move(component: HudComponent, container: HudContainer, offsetX: Float, offsetY: Float) {
        val window = Window.of(component)
        val newX = (offsetX - draggingOffset.first).coerceIn(window.getLeft()..(window.getRight() - component.getWidth())) / window.getWidth() * 100
        val newY = (offsetY - draggingOffset.second).coerceIn(window.getTop()..(window.getBottom() - component.getHeight())) / window.getHeight() * 100
        container.setX(newX.percent)
        container.setY(newY.percent)
        component.moveListeners.forEach { it(component) }
    }
}
