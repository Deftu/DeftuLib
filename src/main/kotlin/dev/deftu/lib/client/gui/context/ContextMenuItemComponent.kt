package dev.deftu.lib.client.gui.context

import gg.essential.elementa.components.ScrollComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.ScissorEffect
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.toConstraint
import dev.deftu.lib.client.gui.DeftuPalette

internal class ContextMenuItemComponent(
    internal val item: ContextMenuItem
) : UIContainer() {
    companion object {
        val HEIGHT = 20.pixels
    }

    private val colorState = BasicState(DeftuPalette.getBackground())
    private val textColorState = BasicState(DeftuPalette.getText())

    private val background by UIBlock(colorState).constrain {
        width = 100.percent
        height = 100.percent
    } childOf this
    private val textContainer by UIContainer().constrain {
        x = 5.pixels
        y = CenterConstraint()
        width = 100.percent - 8.pixels
        height = 100.percent
    } effect ScissorEffect() childOf background
    private val text by UIText(item.text.string).constrain {
        y = CenterConstraint()
        color = textColorState.toConstraint()
    } childOf textContainer

    private val contextMenu: ContextMenuComponent?
        get() = (((parent as? UIContainer)?.parent as? ScrollComponent)?.parent as? UIBlock)?.parent as? ContextMenuComponent

    init {
        constrain {
            y = SiblingConstraint()
            width = 100.percent
            height = HEIGHT
        }.onMouseEnter {
            if (!item.enabled) return@onMouseEnter

            colorState.set(DeftuPalette.getBackground2())
            textColorState.set(DeftuPalette.getPrimary())
        }.onMouseLeave {
            if (!item.enabled) return@onMouseLeave

            colorState.set(DeftuPalette.getBackground())
            textColorState.set(DeftuPalette.getText())
        }.onMouseClick {
            if (item.enabled && it.mouseButton == 0) {
                item.performAction()
            }
        }

        if (!item.enabled) {
            colorState.set(if (colorState.get() != DeftuPalette.getBackground2()) colorState.get() else DeftuPalette.getBackground2())
            textColorState.set(if (textColorState.get() != DeftuPalette.getTextDisabled()) textColorState.get() else DeftuPalette.getTextDisabled())
        }

        item.addEnableChangeListener { value ->
            colorState.set(if (value) {
                if (colorState.get() != DeftuPalette.getBackground2()) colorState.get()
                else DeftuPalette.getBackground()
            } else DeftuPalette.getBackground2())

            textColorState.set(if (value) {
                if (textColorState.get() != DeftuPalette.getTextDisabled()) textColorState.get()
                else DeftuPalette.getText()
            } else DeftuPalette.getTextDisabled())
        }.addVisibilityChangeListener { value ->
            if (value) {
                unhide(true)
            } else {
                hide(true)
            }

            contextMenu?.updateHeight()
            contextMenu?.checkVisibility()
        }
    }
}
