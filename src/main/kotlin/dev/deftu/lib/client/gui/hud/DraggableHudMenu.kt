package dev.deftu.lib.client.gui.hud

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.UIComponent
import gg.essential.elementa.UIConstraints
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.Window
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.dsl.*
import gg.essential.universal.UKeyboard
import gg.essential.universal.UMatrixStack
import dev.deftu.lib.DeftuLib
import dev.deftu.lib.client.gui.context.ContextMenu
import dev.deftu.lib.client.gui.context.ContextMenuComponent
import dev.deftu.lib.client.gui.context.ContextMenuItem
import dev.deftu.lib.client.hud.DraggableHudWindow
import dev.deftu.lib.client.hud.HudComponent
import dev.deftu.lib.client.hud.HudContainer
import dev.deftu.lib.utils.TextHelper

open class DraggableHudMenu(
    val hudWindow: DraggableHudWindow,
    restoreCurrentGuiOnClose: Boolean = true
) : WindowScreen(
    version = ElementaVersion.V2,
    restoreCurrentGuiOnClose = restoreCurrentGuiOnClose
) {
    // Options
    private var defaultContextMenu = true

    // Internal variables
    private var selectedComponent: HudComponent? = null
    private var draggingOffset = 0f to 0f
    private var cachedConstraints = mutableMapOf<UIComponent, UIConstraints>()

    val container by UIContainer().constrain {
        width = 100.percent
        height = 100.percent
    } childOf window

    init {
        // Double check that the window is initialized
        hudWindow.initialize()

        // Run through all the namespaces and add them to the menu
        container.onMouseClick {
            if (it.mouseButton == 0 && !it.target.isHudComponent()) {
                selectedComponent = null
            }
        }

        // Load all the components so they can be edited.
        refresh()
    }

    fun refresh() {
        container.clearChildren()

        cachedConstraints.forEach { (component, constraints) ->
            component.constraints = constraints
            component.onWindowResize()
        }

        println("Namespaces: ${hudWindow.getNamespaces()}")
        hudWindow.getNamespaces().map(Map.Entry<String, HudContainer>::value).toList().forEach { container ->
            container.getHudChildren().forEach { component ->
                cachedConstraints[component] = component.constraints
                val container = HudContainer().constrain {
                    width = ChildBasedSizeConstraint()
                    height = ChildBasedSizeConstraint()
                } childOf this.container

                setupHudComponent(State.PRE, component)
                component.onFocus {
                    selectedComponent = component
                }.onFocusLost {
                    selectedComponent = null
                }.onMouseClick {
                    if (it.mouseButton == 0) {
                        grabWindowFocus()
                        draggingOffset = it.relativeX to it.relativeY
                    } else if (it.mouseButton == 1 && defaultContextMenu) {
                        displayContextMenu(component, it.absoluteX, it.absoluteY)
                    }
                }.onMouseRelease {
                    draggingOffset = 0f to 0f
                }.onMouseDrag { mouseX, mouseY, mouseButton ->
                    if (selectedComponent != component) return@onMouseDrag

                    if (mouseButton == 0) {
                        move(component, mouseX + component.getLeft(), mouseY + component.getTop())
                    }
                }.onKeyType { _, keyCode ->
                    if (component != selectedComponent) return@onKeyType

                    draggingOffset = 0f to 0f
                    when (keyCode) {
                        UKeyboard.KEY_ESCAPE -> {
                            selectedComponent = null
                            loseFocus()
                        }
                        UKeyboard.KEY_UP -> move(component, component.getLeft(), component.getTop() - 1)
                        UKeyboard.KEY_DOWN -> move(component, component.getLeft(), component.getTop() + 1)
                        UKeyboard.KEY_LEFT -> move(component, component.getLeft() - 1, component.getTop())
                        UKeyboard.KEY_RIGHT -> move(component, component.getLeft() + 1, component.getTop())
                    }
                } childOf container
                container.constrain {
                    x = component.constraints.x
                    y = component.constraints.y
                }

                component.constrain {
                    x = 0.pixels
                    y = 0.pixels
                }

                setupHudComponent(State.POST, component)
            }
        }
    }

    open fun getDefaultContextMenuItems(component: HudComponent) = listOf(ContextMenu.item(TextHelper.createTranslatableText("${dev.deftu.lib.DeftuLib.ID}.hud.menu.remove")) { item ->
        component.remove()
        component.hide(true)
        item.closeParent()
    })

    open fun displayContextMenu(component: HudComponent, mouseX: Float, mouseY: Float) {
        val contextMenu by ContextMenu.create(
            xPos = mouseX,
            yPos = mouseY
        ) childOf window
        contextMenu.addItems(getDefaultContextMenuItems(component))
    }

    open fun setupHudComponent(state: State, component: HudComponent) {
    }

    fun UIComponent.isHudComponent(): Boolean {
        var parent = this.parent
        while (true) {
            if (parent is HudComponent) return true
            if (parent is Window) return false
            parent = parent.parent
        }
    }

    private fun move(component: HudComponent, offsetX: Float, offsetY: Float) {
        val window = Window.of(component)
        val newX = (offsetX - draggingOffset.first).coerceIn(window.getLeft()..(window.getRight() - component.getWidth())) / window.getWidth() * 100
        val newY = (offsetY - draggingOffset.second).coerceIn(window.getTop()..(window.getBottom() - component.getHeight())) / window.getHeight() * 100
        val container = component.parent as HudContainer
        container.setX(newX.percent)
        container.setY(newY.percent)
        component.moveListeners.forEach { it(component, newX, newY) }
    }

    enum class State {
        PRE,
        POST
    }
}
