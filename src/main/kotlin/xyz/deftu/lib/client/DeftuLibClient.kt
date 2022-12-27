package xyz.deftu.lib.client

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import xyz.deftu.lib.client.hud.DraggableHudWindow
import xyz.deftu.lib.client.hud.HudWindow
import xyz.deftu.lib.events.EnvironmentSetupEvent

object DeftuLibClient : ClientModInitializer {
    @Deprecated(
        message = "This property is only here temporarily for backwards compatibility.",
        replaceWith = ReplaceWith("xyz.deftu.lib.client.hud.HudWindow"),
    ) val hudWindow = xyz.deftu.lib.client.HudWindow()

    @JvmStatic
    val newHudWindow = HudWindow()
    @JvmStatic
    val draggableHudWindow = DraggableHudWindow()

    override fun onInitializeClient() {
        EnvironmentSetupEvent.EVENT.invoker().onEnvironmentSetup(EnvType.CLIENT)
        hudWindow.initialize()
        newHudWindow.initialize()
        draggableHudWindow.initialize()

        Test.initialize()
    }
}
