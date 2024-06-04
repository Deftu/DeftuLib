package dev.deftu.lib.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@FunctionalInterface
public interface InputEvent {
    Event<InputEvent> EVENT = EventFactory.createArrayBacked(InputEvent.class, (listeners) -> (handle, button, action, mods, scancode, type) -> {
        for (InputEvent listener : listeners) {
            listener.onInput(handle, button, action, mods, scancode, type);
        }
    });

    void onInput(long handle, int button, InputAction action, int mods, int scancode, InputType type);

    enum InputType {
        MOUSE,
        KEYBOARD
    }
}
