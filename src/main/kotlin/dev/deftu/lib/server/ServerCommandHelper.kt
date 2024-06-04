package dev.deftu.lib.server

//#if MC >= 1.19.2
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
//#else
//$$ import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
//#endif

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

object ServerCommandHelper {

    @JvmStatic
    fun create(name: String): LiteralArgumentBuilder<ServerCommandSource> =
        CommandManager.literal(name)

    @JvmStatic
    fun <T> argument(name: String, type: ArgumentType<T>): RequiredArgumentBuilder<ServerCommandSource, T> =
        CommandManager.argument(name, type)

    @JvmStatic
    fun register(builder: LiteralArgumentBuilder<ServerCommandSource?>?) {
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback {
                dispatcher,
                _
                //#if MC>=11900
                , _
                //#endif
            ->
            dispatcher.register(builder)
        })
    }

}
