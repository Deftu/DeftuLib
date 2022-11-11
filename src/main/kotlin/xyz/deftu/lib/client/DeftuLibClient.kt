package xyz.deftu.lib.client

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import xyz.deftu.lib.client.hud.DraggableHudWindow
import xyz.deftu.lib.client.hud.HudWindow
import xyz.deftu.lib.events.EnvironmentSetupEvent

object DeftuLibClient : ClientModInitializer {
    @JvmStatic
    val hudWindow = HudWindow()
    @JvmStatic
    val draggableHudWindow = DraggableHudWindow()

    override fun onInitializeClient() {
        EnvironmentSetupEvent.EVENT.invoker().onEnvironmentSetup(EnvType.CLIENT)
        hudWindow.initialize()
        draggableHudWindow.initialize()
    }
}
