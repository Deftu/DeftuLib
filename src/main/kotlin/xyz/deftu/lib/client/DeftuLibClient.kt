package xyz.deftu.lib.client

import net.fabricmc.api.ClientModInitializer

object DeftuLibClient : ClientModInitializer {
    private val hudWindow = HudWindow()

    override fun onInitializeClient() {
        hudWindow.initialize()
    }
}
