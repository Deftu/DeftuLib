package xyz.deftu.lib.client

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import xyz.deftu.lib.DeftuLib

object DeftuLibClient : ClientModInitializer {
    val hudWindow = HudWindow()

    override fun onInitializeClient() {
        DeftuLib.setEnvironment(EnvType.CLIENT)
        DeftuLib.UPDATE_CHECKER.start()
        hudWindow.initialize()
    }
}
