package xyz.deftu.lib.server

import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.api.EnvType
import xyz.deftu.lib.DeftuLib
import xyz.deftu.lib.events.EnvironmentSetupEvent

object DeftuLibServer : DedicatedServerModInitializer {
    override fun onInitializeServer() {
        EnvironmentSetupEvent.EVENT.invoker().onEnvironmentSetup(EnvType.SERVER)
    }
}
