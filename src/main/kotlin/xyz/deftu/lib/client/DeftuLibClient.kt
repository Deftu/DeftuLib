package xyz.deftu.lib.client

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.ClickEvent
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
            val firstSection = TextHelper.createLiteralText("")
                .append(TextHelper.createLiteralText("--").formatted(Formatting.GRAY, Formatting.STRIKETHROUGH))
                .append(TextHelper.createLiteralText("[").formatted(Formatting.GRAY))
            val discordSection = TextHelper.createLiteralText("Discord").formatted(Formatting.BLUE)
                .styled { it.withClickEvent(ClickEvent(ClickEvent.Action.OPEN_URL, "https://shr.deftu.xyz/discord")) }
            val secondSection = TextHelper.createLiteralText("]")
                .formatted(Formatting.GRAY)
                .append(TextHelper.createLiteralText("--").formatted(Formatting.GRAY, Formatting.STRIKETHROUGH))
                .append(TextHelper.createLiteralText("[")
                    .formatted(Formatting.GRAY))
            val githubSection = TextHelper.createLiteralText("GitHub").formatted(Formatting.BLUE)
                .styled { it.withClickEvent(ClickEvent(ClickEvent.Action.OPEN_URL, "https://shr.deftu.xyz/github")) }
            val thirdSection = TextHelper.createLiteralText("]")
                .formatted(Formatting.GRAY)
                .append(TextHelper.createLiteralText("--").formatted(Formatting.GRAY, Formatting.STRIKETHROUGH))

            val message = TextHelper.createLiteralText("")
                .append(firstSection)
                .append(discordSection)
                .append(secondSection)
                .append(githubSection)
                .append(thirdSection)

            ClientPlayConnectionEvents.JOIN.register { _, _, _ ->
                ChatHelper.sendClientMessage(message)
            }
        }

        // Test
        Test.initialize()
    }

    fun openPlayerActionScreen(player: PlayerEntity) {
        MinecraftClient.getInstance().setScreen(PlayerActionScreen(player))
    }
}
