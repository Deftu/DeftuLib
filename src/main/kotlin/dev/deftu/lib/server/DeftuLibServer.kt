package dev.deftu.lib.server

import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.api.EnvType
import dev.deftu.lib.DeftuLib
import dev.deftu.lib.events.EnvironmentSetupEvent

object DeftuLibServer : DedicatedServerModInitializer {
    override fun onInitializeServer() {
        EnvironmentSetupEvent.EVENT.invoker().onEnvironmentSetup(EnvType.SERVER)
    }
}
