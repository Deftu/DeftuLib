package xyz.deftu.lib.client

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import xyz.deftu.lib.DeftuLib
import xyz.deftu.lib.client.hud.DraggableHudWindow
import xyz.deftu.lib.client.hud.HudWindow

object DeftuLibClient : ClientModInitializer {
    val hudWindow = HudWindow()
    val draggableHudWindow = DraggableHudWindow()

    override fun onInitializeClient() {
        DeftuLib.setEnvironment(EnvType.CLIENT)
        DeftuLib.UPDATE_CHECKER.start()
        hudWindow.initialize()
        draggableHudWindow.initialize()

        Test.start()
    }
}
