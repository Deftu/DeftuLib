package xyz.deftu.lib.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@FunctionalInterface
public interface InputEvent {
    Event<InputEvent> EVENT = EventFactory.createArrayBacked(InputEvent.class, (listeners) -> (button, action, mods, type) -> {
        for (InputEvent listener : listeners) {
            listener.onInput(button, action, mods, type);
        }
    });

    void onInput(int button, int action, int mods, InputType type);

    enum InputType {
        MOUSE,
        KEYBOARD
    }
}
