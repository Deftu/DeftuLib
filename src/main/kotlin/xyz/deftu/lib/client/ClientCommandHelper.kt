package xyz.deftu.lib.client

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder

//#if MC>=11900
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
//#else
//$$ import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager
//$$ import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource
//#endif

object ClientCommandHelper {
    fun create(name: String): LiteralArgumentBuilder<FabricClientCommandSource> =
        ClientCommandManager.literal(name)
    fun <T> argument(name: String, type: ArgumentType<T>): RequiredArgumentBuilder<FabricClientCommandSource, T> =
        ClientCommandManager.argument(name, type)

    fun register(builder: LiteralArgumentBuilder<FabricClientCommandSource>) {
        //#if MC>=11900
        ClientCommandRegistrationCallback.EVENT.register(ClientCommandRegistrationCallback { dispatcher, _ ->
            dispatcher.register(builder)
        })
        //#else
        //$$ ClientCommandManager.register(builder)
        //#endif
    }
}
