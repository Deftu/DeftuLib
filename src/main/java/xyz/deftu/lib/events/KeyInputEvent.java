package xyz.deftu.lib.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@FunctionalInterface
public interface KeyInputEvent {
    Event<KeyInputEvent> EVENT = EventFactory.createArrayBacked(KeyInputEvent.class, (listeners) -> (key, scancode, action, mods) -> {
        for (KeyInputEvent listener : listeners) {
            listener.onKeyInput(key, scancode, action, mods);
        }
    });

    void onKeyInput(int key, int scancode, int action, int mods);
}
