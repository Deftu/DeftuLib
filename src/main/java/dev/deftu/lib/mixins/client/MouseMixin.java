package dev.deftu.lib.mixins.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import dev.deftu.lib.events.InputAction;
import dev.deftu.lib.events.InputEvent;
import dev.deftu.lib.events.MouseInputEvent;

@Mixin({Mouse.class})
public class MouseMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onMouseButton", at = @At("HEAD"))
    private void deftulib$onMouseButton(long handle, int button, int action, int mods, CallbackInfo ci) {
        if (handle != client.getWindow().getHandle())
            return;

        InputAction inputAction = InputAction.from(action);
        MouseInputEvent.EVENT.invoker().onMouseInput(button, inputAction, mods);
        InputEvent.EVENT.invoker().onInput(handle, button, inputAction, mods, -1, InputEvent.InputType.MOUSE);
    }
}
