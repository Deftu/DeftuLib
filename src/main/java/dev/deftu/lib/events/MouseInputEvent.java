package dev.deftu.lib.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@FunctionalInterface
public interface MouseInputEvent {
    Event<MouseInputEvent> EVENT = EventFactory.createArrayBacked(MouseInputEvent.class, (listeners) -> (button, action, mods) -> {
        for (MouseInputEvent listener : listeners) {
            listener.onMouseInput(button, action, mods);
        }
    });

    void onMouseInput(int button, InputAction action, int mods);
}
