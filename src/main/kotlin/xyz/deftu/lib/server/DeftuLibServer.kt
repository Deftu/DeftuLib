package xyz.deftu.lib.server

import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.api.EnvType
import xyz.deftu.lib.DeftuLib

object DeftuLibServer : DedicatedServerModInitializer {
    override fun onInitializeServer() {
        DeftuLib.setEnvironment(EnvType.SERVER)
        DeftuLib.UPDATE_CHECKER.start()
    }
}
