package dev.deftu.lib.mixins.client;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({Screen.class})
public interface ScreenMixin {
    @Invoker <T extends Element & Drawable & Selectable> T callAddDrawableChild(T child);
}
