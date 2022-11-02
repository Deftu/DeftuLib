package xyz.deftu.lib.mixins.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.deftu.lib.events.InputEvent;
import xyz.deftu.lib.events.MouseInputEvent;

@Mixin({Mouse.class})
public class MouseMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onMouseButton", at = @At("HEAD"))
    private void deftulib$onMouseButton(long handle, int button, int action, int mods, CallbackInfo ci) {
        if (handle != client.getWindow().getHandle())
            return;

        MouseInputEvent.EVENT.invoker().onMouseInput(button, action, mods);
        InputEvent.EVENT.invoker().onInput(button, action, mods, InputEvent.InputType.MOUSE);
    }
}
