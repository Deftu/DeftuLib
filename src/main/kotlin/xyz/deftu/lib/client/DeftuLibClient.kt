package xyz.deftu.lib.client

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.ClickEvent
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import xyz.deftu.lib.DeftuLib
import xyz.deftu.lib.client.actions.PlayerActionManager
import xyz.deftu.lib.client.gui.actions.PlayerActionScreen
import xyz.deftu.lib.client.hud.DraggableHudWindow
import xyz.deftu.lib.client.hud.HudWindow
import xyz.deftu.lib.events.EnvironmentSetupEvent
import xyz.deftu.lib.utils.ChatHelper
import xyz.deftu.lib.utils.TextHelper

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
                    .append(TextHelper.createLiteralText(DeftuLib.NAME).formatted(Formatting.GOLD))
                    .append(TextHelper.createLiteralText("!").formatted(Formatting.GRAY)), "")
                ChatHelper.sendClientMessage(TextHelper.createLiteralText("This message is only shown on first launch.").formatted(Formatting.GRAY), "")
                ChatHelper.sendEmptyClientMessage()

                ChatHelper.sendClientMessage(TextHelper.createLiteralText("You can find ways to contact me below:").formatted(Formatting.GRAY), "")
                ChatHelper.sendClientMessage(createUrlListText(
                    "Discord" to "https://shr.deftu.xyz/discord",
                    "GitHub" to "https://shr.deftu.xyz/github",
                    "Website" to "https://deftu.xyz"
                ), "")

                ChatHelper.sendEmptyClientMessage()

                ChatHelper.sendClientMessage(TextHelper.createLiteralText("You can also support me by donating below:").formatted(Formatting.GRAY), "")
                ChatHelper.sendClientMessage(createUrlListText(
                    "Ko-Fi" to "https://shr.deftu.xyz/ko-fi",
                    "PayPal" to "https://shr.deftu.xyz/paypal"
                ), "")

                ChatHelper.sendEmptyClientMessage()
                ChatHelper.sendClientMessage(TextHelper.createLiteralText("")
                    .append(TextHelper.createLiteralText("-".repeat(dividerSize)).formatted(Formatting.GRAY, Formatting.STRIKETHROUGH))
                    .append(TextHelper.createLiteralText(" ${DeftuLib.NAME} ").formatted(Formatting.GOLD))
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
