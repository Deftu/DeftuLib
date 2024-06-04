package dev.deftu.lib.client.gui.actions

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.components.inspector.Inspector
import gg.essential.elementa.constraints.*
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.OutlineEffect
import gg.essential.elementa.state.BasicState
import gg.essential.universal.UMouse
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import dev.deftu.lib.client.DeftuLibClient
import dev.deftu.lib.client.actions.PlayerAction
import dev.deftu.lib.client.gui.DeftuPalette
import dev.deftu.lib.utils.ChatHelper
import dev.deftu.lib.utils.Multithreader
import dev.deftu.lib.utils.TextHelper
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class PlayerActionScreen(
    val player: PlayerEntity
) : WindowScreen(
    version = ElementaVersion.V5,
    drawDefaultBackground = false,
    restoreCurrentGuiOnClose = true
) {
    private val multithreader = Multithreader(10)
    private var tooltipUpdateFuture: Future<*>? = null

    private val actions: List<PlayerAction>
        get() = DeftuLibClient.playerActionManager.getActions().sortedWith(compareBy({
            it.priority
        }, {
            it.index
        }))

    private val tooltipText = BasicState("")
    private var tooltip by UIBlock(DeftuPalette.getBackground2()).constrain {
        x = basicXConstraint {
            if (tooltipText.get().isEmpty()) return@basicXConstraint MinecraftClient.getInstance().window.width.toFloat()

            val mouseX = UMouse.Scaled.x.toFloat()
            val width = it.getWidth()
            val screenWidth = MinecraftClient.getInstance().window.scaledWidth
            if (mouseX + width > screenWidth) {
                mouseX - width
            } else {
                mouseX
            } + 1f
        }

        y = basicYConstraint {
            if (tooltipText.get().isEmpty()) return@basicYConstraint MinecraftClient.getInstance().window.height.toFloat()

            val mouseY = UMouse.Scaled.y.toFloat()
            val height = it.getHeight()
            val screenHeight = MinecraftClient.getInstance().window.scaledHeight
            if (mouseY + height > screenHeight) {
                mouseY - height
            } else {
                mouseY
            }
        }

        width = ChildBasedSizeConstraint(5f)
        height = ChildBasedSizeConstraint(5f)
    } effect OutlineEffect(
        color = DeftuPalette.getPrimary(),
        width = 1f
    ) childOf window

    private val background by UIContainer().constrain {
        x = CenterConstraint()
        y = CenterConstraint()
        width = ChildBasedSizeConstraint()
    } childOf window

    init {
        Inspector(window) childOf window

        val tooltipText by UIText().constrain {
            x = CenterConstraint()
            y = CenterConstraint()
        } childOf tooltip
        tooltip.setFloating(true)
        this.tooltipText.onSetValue(tooltipText::setText)

        println("actions: ${actions.size}")
        val columns = actions.size / 5 + 1
        println("columns: $columns")
        var highestColumn = 0f
        val columnBlocks = (0 until columns).map {
            val column by UIContainer().constrain {
                x = SiblingConstraint()
                width = ChildBasedMaxSizeConstraint()
                height = ChildBasedSizeConstraint()
            } childOf background
            column
        }

        columnBlocks.forEach {
            it.onMouseLeave {
                this@PlayerActionScreen.tooltipText.set("")
                this@PlayerActionScreen.tooltipUpdateFuture?.cancel(true)
            }
        }

        val actionButtons = mutableListOf<UIBlock>()
        var highestWidth = 0f
        actions.forEachIndexed { index, action ->
            val columnIndex = index / 5
            val column = columnBlocks[columnIndex]

            val button by UIBlock(DeftuPalette.getBackground2()).constrain {
                y = SiblingConstraint()
                height = ChildBasedSizeConstraint() + 10.pixels
            } effect OutlineEffect(
                color = DeftuPalette.getPrimary(),
                width = 2f
            ) childOf column

            val text by UIText(action.name).constrain {
                x = CenterConstraint()
                y = CenterConstraint()
            } childOf button

            button.onMouseClick {
                if (!action.action(player, action)) return@onMouseClick

                MinecraftClient.getInstance().setScreen(null)
                it.stopImmediatePropagation()
            }.onMouseEnter {
                println("enter")
                tooltipUpdateFuture = multithreader.schedule({
                    this@PlayerActionScreen.tooltipText.set(action.description)
                }, 50, TimeUnit.MILLISECONDS)
            }

            val widthValue = (ChildBasedSizeConstraint() + 10.pixels).getWidth(button)
            if (widthValue > highestWidth) highestWidth = widthValue

            actionButtons.add(button)
        }

        columnBlocks.forEach {
            if (it.getHeight() > highestColumn) highestColumn = it.getHeight()
        }

        background.constrain {
            height = highestColumn.pixels
        }

        actionButtons.forEach {
            it.constrain {
                width = highestWidth.pixels
            }
        }
    }

    override fun initScreen(width: Int, height: Int) {
        if (MinecraftClient.getInstance().world == null) {
            MinecraftClient.getInstance().setScreen(null)
            ChatHelper.sendClientMessage(TextHelper.createTranslatableText("${dev.deftu.lib.DeftuLib.ID}.error.screen_requires_world", "PlayerActionScreen"))
            return
        }

        if (actions.isEmpty()) {
            MinecraftClient.getInstance().setScreen(null)
            ChatHelper.sendClientMessage(TextHelper.createTranslatableText("${dev.deftu.lib.DeftuLib.ID}.error.no_actions"))
            return
        }

        super.initScreen(width, height)
    }
}
