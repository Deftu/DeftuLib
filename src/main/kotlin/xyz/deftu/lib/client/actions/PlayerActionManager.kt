package xyz.deftu.lib.client.actions

import gg.essential.universal.UKeyboard
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.world.RaycastContext
import xyz.deftu.lib.DeftuLib
import xyz.deftu.lib.client.DeftuLibClient
import xyz.deftu.lib.events.InputAction
import xyz.deftu.lib.events.InputEvent

class PlayerActionManager {
    private var initialized = false
    private val keyBinding = KeyBinding("key.${DeftuLib.ID}.player_action", UKeyboard.KEY_U, "key.categories.${DeftuLib.ID}")
    private val actions = mutableListOf<PlayerAction>()

    fun initialize() {
        if (initialized) return

        KeyBindingHelper.registerKeyBinding(keyBinding)
        InputEvent.EVENT.register { handle, button, action, mods, scancode, type ->
            if (keyBinding.matchesKey(button, scancode) || keyBinding.matchesMouse(button)) {
                if (action != InputAction.PRESS) return@register

                println("Pressed key")

                val player = MinecraftClient.getInstance().player ?: return@register
                val raycastResult = MinecraftClient.getInstance().crosshairTarget ?: return@register
                println("Raycast result: $raycastResult")
                if (raycastResult.type != HitResult.Type.ENTITY) return@register

                println("Raycast result is entity")
                val hitResult = raycastResult as EntityHitResult
                val entity = hitResult.entity
                if (entity !is PlayerEntity || entity == player) return@register

                println("Entity is player")
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
