package xyz.deftu.lib.client.gui.context

import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.ScissorEffect
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.toConstraint
import xyz.deftu.lib.client.gui.DeftuPalette

internal class ContextMenuItemComponent(
    item: ContextMenuItem,
    private val contextMenu: ContextMenuComponent
) : UIContainer() {
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

    init {
        constrain {
            y = SiblingConstraint()
            width = 100.percent
            height = 20.pixels
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
            colorState.set(if (colorState.get() != DeftuPalette.getBackground3()) colorState.get() else DeftuPalette.getBackground3())
            textColorState.set(if (textColorState.get() != DeftuPalette.getTextDisabled()) textColorState.get() else DeftuPalette.getTextDisabled())
        }

        item.addEnableChangeListener { value ->
            colorState.set(if (value) {
                if (colorState.get() != DeftuPalette.getBackground3()) colorState.get()
                else DeftuPalette.getBackground()
            } else DeftuPalette.getBackground3())

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
        }
    }
}
