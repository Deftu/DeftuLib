package xyz.deftu.lib.client.actions

import net.minecraft.entity.player.PlayerEntity

data class PlayerAction(
    val priority: Int,
    val name: String,
    val description: String,
    val action: (PlayerEntity, PlayerAction) -> Boolean
) {
    internal var index: Int = 0
}
