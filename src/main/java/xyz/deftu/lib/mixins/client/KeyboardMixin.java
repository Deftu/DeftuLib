package xyz.deftu.lib.mixins.client;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.deftu.lib.events.InputAction;
import xyz.deftu.lib.events.InputEvent;
import xyz.deftu.lib.events.KeyInputEvent;

@Mixin({Keyboard.class})
public class KeyboardMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onKey", at = @At("HEAD"))
    private void deftulib$onKey(long handle, int key, int scancode, int action, int mods, CallbackInfo ci) {
        if (handle != client.getWindow().getHandle())
            return;

        InputAction inputAction = InputAction.from(action);
        KeyInputEvent.EVENT.invoker().onKeyInput(key, scancode, inputAction, mods);
        InputEvent.EVENT.invoker().onInput(handle, key, inputAction, mods, scancode, InputEvent.InputType.KEYBOARD);
    }
}
