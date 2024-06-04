package dev.deftu.lib.client

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.ClickEvent
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import dev.deftu.lib.DeftuLib
import dev.deftu.lib.client.actions.PlayerActionManager
import dev.deftu.lib.client.gui.actions.PlayerActionScreen
import dev.deftu.lib.client.hud.DraggableHudWindow
import dev.deftu.lib.client.hud.HudWindow
import dev.deftu.lib.events.EnvironmentSetupEvent
import dev.deftu.lib.utils.ChatHelper
import dev.deftu.lib.utils.TextHelper

object DeftuLibClient : ClientModInitializer {

    // HUD APIs
    @JvmStatic
    val hudWindow = HudWindow()

    @JvmStatic
    val draggableHudWindow = DraggableHudWindow()

    // Player Action APIs
    @JvmStatic
    val playerActionManager = PlayerActionManager()

    override fun onInitializeClient() {
        EnvironmentSetupEvent.EVENT.invoker().onEnvironmentSetup(EnvType.CLIENT)

        // HUD APIs
        hudWindow.initialize()
        draggableHudWindow.initialize()

        // Player Action APIs
        playerActionManager.initialize()

        // First-launch message
        if (DeftuLib.firstLaunch) {
            ClientPlayConnectionEvents.JOIN.register { _, _, _ ->
                val dividerSize = 7
                ChatHelper.sendClientMessage(TextHelper.createLiteralText("")
                    .append(TextHelper.createLiteralText("-".repeat(dividerSize)).formatted(Formatting.GRAY, Formatting.STRIKETHROUGH))
                    .append(TextHelper.createLiteralText(" ${DeftuLib.NAME} ").formatted(Formatting.GOLD))
                    .append(TextHelper.createLiteralText("-".repeat(dividerSize)).formatted(Formatting.GRAY, Formatting.STRIKETHROUGH)), "")
                ChatHelper.sendEmptyClientMessage()

                ChatHelper.sendClientMessage(TextHelper.createLiteralText("Thanks for using ").formatted(Formatting.GRAY)
                    .append(TextHelper.createLiteralText(dev.deftu.lib.DeftuLib.NAME).formatted(Formatting.GOLD))
                    .append(TextHelper.createLiteralText("!").formatted(Formatting.GRAY)), "")
                ChatHelper.sendClientMessage(TextHelper.createLiteralText("This message is only shown on first launch.").formatted(Formatting.GRAY), "")
                ChatHelper.sendEmptyClientMessage()

                ChatHelper.sendClientMessage(TextHelper.createLiteralText("You can find ways to contact me below:").formatted(Formatting.GRAY), "")
                ChatHelper.sendClientMessage(createUrlListText(
                    "Discord" to "https://s.deftu.dev/discord",
                    "GitHub" to "https://s.deftu.dev/github",
                    "Website" to "https://deftu.dev"
                ), "")

                ChatHelper.sendEmptyClientMessage()

                ChatHelper.sendClientMessage(TextHelper.createLiteralText("You can also support me by clicking the links below:").formatted(Formatting.GRAY), "")
                ChatHelper.sendClientMessage(createUrlListText(
                    "Ko-Fi" to "https://s.deftu.dev/kofi",
                    "PayPal" to "https://s.deftu.dev/paypal",
                    "BisectHosting" to "https://bisecthosting.com/deftu"
                ), "")

                ChatHelper.sendEmptyClientMessage()
                ChatHelper.sendClientMessage(TextHelper.createLiteralText("")
                    .append(TextHelper.createLiteralText("-".repeat(dividerSize)).formatted(Formatting.GRAY, Formatting.STRIKETHROUGH))
                    .append(TextHelper.createLiteralText(" ${dev.deftu.lib.DeftuLib.NAME} ").formatted(Formatting.GOLD))
                    .append(TextHelper.createLiteralText("-".repeat(dividerSize)).formatted(Formatting.GRAY, Formatting.STRIKETHROUGH)), "")
            }
        }

        // Test
        Test.initialize()
    }

    fun openPlayerActionScreen(player: PlayerEntity) {
        MinecraftClient.getInstance().setScreen(PlayerActionScreen(player))
    }

    private fun createUrlListText(
        vararg items: Pair<String, String>
    ): Text {
        val message = TextHelper.createLiteralText("")

        items.forEachIndexed { index, (name, url) ->
            val firstSection = TextHelper.createLiteralText("")
                .append(if (index == 0) TextHelper.createLiteralText("--").formatted(Formatting.GRAY, Formatting.STRIKETHROUGH) else TextHelper.createLiteralText(""))
                .append(TextHelper.createLiteralText("[").formatted(Formatting.GRAY))
            val section = TextHelper.createLiteralText(name).formatted(Formatting.BLUE, Formatting.BOLD)
                .styled { it.withClickEvent(ClickEvent(ClickEvent.Action.OPEN_URL, url)) }
            val secondSection = TextHelper.createLiteralText("]")
                .formatted(Formatting.GRAY)
                .append(TextHelper.createLiteralText("--").formatted(Formatting.GRAY, Formatting.STRIKETHROUGH))

            message.append(firstSection)
                .append(section)
                .append(secondSection)
        }

        return message
    }
}
