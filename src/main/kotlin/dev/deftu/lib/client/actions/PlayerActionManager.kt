package dev.deftu.lib.client.actions

import gg.essential.universal.UKeyboard
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.world.RaycastContext
import dev.deftu.lib.DeftuLib
import dev.deftu.lib.client.DeftuLibClient
import dev.deftu.lib.events.InputAction
import dev.deftu.lib.events.InputEvent

class PlayerActionManager {

    private var initialized = false
    private val keyBinding = KeyBinding("key.${DeftuLib.ID}.player_action", UKeyboard.KEY_U, "key.categories.${DeftuLib.ID}")
    private val actions = mutableListOf<PlayerAction>()

    fun initialize() {
        if (initialized) return

        KeyBindingHelper.registerKeyBinding(keyBinding)
        InputEvent.EVENT.register { handle, button, action, mods, scancode, type ->
            if (
                MinecraftClient.getInstance().currentScreen == null &&
                (
                    keyBinding.matchesKey(button, scancode) ||
                    keyBinding.matchesMouse(button)
                )
            ) {
                if (action != InputAction.PRESS) return@register

                val player = MinecraftClient.getInstance().player ?: return@register
                val raycastResult = MinecraftClient.getInstance().crosshairTarget ?: return@register
                if (raycastResult.type != HitResult.Type.ENTITY) return@register

                val hitResult = raycastResult as EntityHitResult
                val entity = hitResult.entity
                if (entity !is PlayerEntity || entity == player) return@register

                DeftuLibClient.openPlayerActionScreen(entity)
            }
        }

        initialized = true
    }

    fun register(action: PlayerAction) {
        actions.add(action)
        action.index = actions.indexOf(action)
    }

    fun getActions() = actions.toList()

}
