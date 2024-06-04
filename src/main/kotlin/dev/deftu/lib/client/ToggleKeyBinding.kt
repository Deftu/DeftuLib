package dev.deftu.lib.client

import net.minecraft.client.option.KeyBinding

class ToggleKeyBinding(
    name: String,
    code: Int,
    category: String,
) : KeyBinding(name, code, category) {

    var toggle = false
        private set
        get() {
            val value = field
            field = false
            return value
        }

    fun toggle() {
        toggle = !toggle
    }

}
